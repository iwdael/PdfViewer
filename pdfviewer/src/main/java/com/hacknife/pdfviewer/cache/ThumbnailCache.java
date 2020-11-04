package com.hacknife.pdfviewer.cache;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.helper.TaskFactory;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.model.PatchKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThumbnailCache {
    public static final String TAG = ThumbnailCache.class.getSimpleName();
    private final List<Patch> prepared;
    private final List<Patch> prepare;
    private final int thumbnailPatchCount;
    private final Configurator configurator;
    private PatchKey commenceKey = null;
    private PatchKey closureKey = null;

    public ThumbnailCache(Configurator configurator) {
        this.configurator = configurator;
        this.prepared = new LinkedList<>();
        this.prepare = new LinkedList<>();
        thumbnailPatchCount = configurator.thumbnailPatchCount();
        int patchSize = configurator.thumbnailPatchSize();
        for (int i = 0; i < thumbnailPatchCount; i++) {
            Patch patch = new Patch(patchSize, patchSize);
            prepared.add(patch);
        }
    }

    //内容变更通知
    public void notifyDataChange(Patch patch) {
        synchronized (prepared) {
            int index = prepare.indexOf(patch);
            if (index != -1) {
                Patch value = prepare.remove(index);
                prepared.add(value);
            } else {
                Logger.t(TAG).log("prepare未找到：" + patch.toString());
            }
            Collections.sort(prepared);
        }
    }

    public void setClosure(PatchKey closureKey) {
        if (this.closureKey == null || !this.closureKey.equals(closureKey)) {
            this.closureKey = closureKey;
            balanceBothEndsPatch();
        }
//        Logger.t(TAG).log("setClosure:" + closure.toString());
    }


    public void setCommence(PatchKey commenceKey) {
        if (this.commenceKey == null || !this.commenceKey.equals(commenceKey)) {
            this.commenceKey = commenceKey;
            balanceBothEndsPatch();
        }
//        Logger.t(TAG).log("setCommence:" + commence.toString());
    }


    //获取
    public Patch achieve(PatchKey patch) {
        int index = prepared.indexOf(patch);
        if (index == -1) return null;
        else return prepared.get(index);
    }

    public Patch rubbish() {
        synchronized (prepared) {
            int commence = -1; // -1 表示為扎到 ，-2 表示邊界之外
            int closure = -1;
            if (commenceKey != null && closureKey != null) {
                commence = -2;
                closure = -2;
                for (int backward = 0, forward = prepared.size() - 1; backward < prepared.size(); backward++, forward--) {
                    Patch backwardPatch = prepared.get(backward);
                    Patch forwardPatch = prepared.get(forward);
                    if (backwardPatch.compareTo(commenceKey) <= 0) {
                        commence = backward;
                    }
                    if (forwardPatch.compareTo(closureKey) >= 0) {
                        closure = forward;
                    }
                }
            }

            if (commence == -1 || closure == -1) {
                if (!prepared.isEmpty()) {
                    Patch value = prepared.remove(0);
                    prepare.add(value);
                    return value;
                }
            }

            if (commence == -2 && closure == -2) { //要在中间
                Logger.t(TAG).log("内容处于commence，closure中间:");
            } else {
                for (int backward = 0, forward = prepared.size() - 1; forward > backward; backward++, forward--) {
                    if (commence != -2 && closure != -2) { //
                        //向后的距离
                        int backwardLength = commence - backward;
                        int forwardLength = forward - closure;
                        if (backwardLength > 0 && forwardLength > 0) {
                            if (backwardLength >= forwardLength) {
                                Patch value = prepared.remove(backward);
                                prepare.add(value);
                                return value;
                            } else {
                                Patch value = prepared.remove(forward);
                                prepare.add(value);
                                return value;
                            }
                        } else if (backwardLength > 0) {

                            Patch value = prepared.remove(backward);
                            prepare.add(value);
                            return value;

                        } else if (forwardLength > 0) {
                            Patch value = prepared.remove(forward);
                            prepare.add(value);
                            return value;
                        }
                    } else if (commence == -2) {
                        if (closure != prepared.size() - 1) {
                            Patch value = prepared.remove(prepared.size() - 1);
                            prepare.add(value);
                            return value;
                        } else {
                            Logger.t(TAG).log("length:%d , commence:%d , commencePath:%s , closure:%d , closurePatch:%s , first:%s , last:%s", prepared.size(), commence, commenceKey, closure, closureKey, prepared.get(0), prepared.get(prepared.size() - 1));
                            return null;
                        }
                    } else if (closure == -2) {
                        //不能是0
                        if (commence != 0) {
                            Patch value = prepared.remove(0);
                            prepare.add(value);
                            return value;
                        } else {
                            Logger.t(TAG).log("length:%d , commence:%d , commencePath:%s , closure:%d , closurePatch:%s , first:%s , last:%s", prepared.size(), commence, commenceKey, closure, closureKey, prepared.get(0), prepared.get(prepared.size() - 1));
                            return null;
                        }

                    }
                }
            }
            Logger.t(TAG).log("length:%d ,  commence index:%d , commence:%s , closure index:%d , closure:%s , prepared:%s", prepared.size(), commence, commence >= 0 ? prepared.get(commence) : "", closure, closure >= 0 ? prepared.get(closure) : "", prepared.toString());
            //可以考虑从线程池中回收
            return null;
        }
    }

    //平衡两端
    private void balanceBothEndsPatch() {
        int commence = -2;
        int closure = -2;
        List<Patch> patches = new ArrayList<>(prepared);
        if (commenceKey == null || closureKey == null) return;
        for (int backward = 0, forward = patches.size() - 1; backward < patches.size(); backward++, forward--) {
            Patch backwardPatch = patches.get(backward);
            Patch forwardPatch = patches.get(forward);
            if (backwardPatch.compareTo(commenceKey) <= 0) {
                commence = backward;
            }
            if (forwardPatch.compareTo(closureKey) >= 0) {
                closure = forward;
            }
        }
        if (commence != -2 && closure != -2) {//只处理左右都有剩余的情况
            int commenceLength = commence;
            int closureLength = patches.size() - 1 - closure;
            int differ = commenceLength - closureLength;
            if (differ == 0) {
                //前后都相等 不处理
            } else if (differ > 0) { //前面多，向后加载 differ
//                Logger.t(TAG).log("reload after| count:%d , location:%s", differ, patches.get(patches.size() - 1).clone());
                TaskFactory.createThumbnailPatchTask(configurator, patches.get(patches.size() - 1).clone(), differ, false);
            } else {
//                Logger.t(TAG).log("reload before| count:%d , location:%s", differ, patches.get(0).clone());
                TaskFactory.createThumbnailPatchTask(configurator, patches.get(0).clone(), -differ, true);
            }
        }
    }


}
