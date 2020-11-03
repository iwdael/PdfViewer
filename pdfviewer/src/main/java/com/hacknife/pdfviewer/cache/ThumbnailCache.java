package com.hacknife.pdfviewer.cache;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.helper.PreparePatchQueue;
import com.hacknife.pdfviewer.helper.PreparedPatchQueue;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.model.PatchKey;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThumbnailCache {
    public static final String TAG = ThumbnailCache.class.getSimpleName();
    private final PreparedPatchQueue prepared;
    private final PreparePatchQueue prepare;
    private final int thumbnailPatchCount;
    private PatchKey commenceKey = null;
    private PatchKey closureKey = null;

    public ThumbnailCache(Configurator configurator) {
        this.prepared = new PreparedPatchQueue();
        this.prepare = new PreparePatchQueue();
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
            prepare.removeInto(patch, prepared);
            Collections.sort(prepared);
        }
    }

    public void setClosure(PatchKey closure) {
        this.closureKey = closure;
//        Logger.t(TAG).log("setClosure:" + closure.toString());
    }

    public void setCommence(PatchKey commence) {
        this.commenceKey = commence;
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
                if (!prepared.isEmpty()) return prepared.removeInto(0, prepare);
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
                            if (backwardLength >= forwardLength)
                                return prepared.removeInto(backward, prepare);
                            else return prepared.removeInto(forward, prepare);
                        } else if (backwardLength > 0) {
                            return prepared.removeInto(backward, prepare);
                        } else if (forwardLength > 0) {
                            return prepared.removeInto(forward, prepare);
                        }
                    } else if (commence == -2) {
                        if (closure != prepared.size()-1) {
                            return prepared.removeInto(prepared.size()-1, prepare);
                        } else {
                            Logger.t(TAG).log("length:%d , commence:%d , commencePath:%s , closure:%d , closurePatch:%s , first:%s , last:%s", prepared.size(), commence, commenceKey, closure, closureKey, prepared.get(0), prepared.get(prepared.size() - 1));
                            return null;
                        }
                    } else if (closure == -2) {
                        //不能是0
                        if (commence != 0) {
                            return prepared.removeInto(0, prepare);
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
}
