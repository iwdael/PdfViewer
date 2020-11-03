package com.hacknife.pdfviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.artifex.mupdf.fitz.SeekableInputStream;
import com.hacknife.pdfviewer.cache.ThumbnailCache;
import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.loader.PdfLoader;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.widget.Cell;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.state.Prepare;
import com.hacknife.pdfviewer.state.ScaleMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class PdfView extends ViewGroup implements PdfLoader.OnPdfLoaderListener {
    private static final String TAG = PdfView.class.getSimpleName();
    private static final String TAG_CREATE = "TAG_CREATE";
    private static final String TAG_RECYCLER_SCROLL_TOP = "TAG_RECYCLER_SCROLL_TOP";
    private static final String TAG_RECYCLER_SCROLL_BOTTOM = "TAG_RECYCLER_SCROLL_BOTTOM";
    private static final String TAG_RECYCLER_SCALE_TOP = "TAG_RECYCLER_SCALE_TOP";
    private static final String TAG_RECYCLER_SCALE_BOTTOM = "TAG_RECYCLER_SCALE_BOTTOM";
    protected Configurator configurator;
    private DragPinchManager dragPinchManager;
    private Map<Integer, Cell> displayCell;
    protected Size packSize;
    private float offset = 0f;
    private float distance = 0f;
    private float transverseLength = 0f; //页面放大后，非翻滚页面的距离
    private Prepare prepared = Prepare.FAIL;

    public PdfView(Context context) {
        this(context, null);
    }

    public PdfView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PdfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragPinchManager = new DragPinchManager(this);
        displayCell = new HashMap<>();
        setBackgroundColor(Color.parseColor("#E8FFEB3B"));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (prepared != Prepare.PREPARED) return;
        layoutCell();
    }

    private void layoutCell() {
        if (prepared != Prepare.PREPARED) return;
        int pageCount = configurator.core.pageCount();

        for (int page = configurator.pageNumber, height = (int) -distance; height < packSize.height && page < pageCount; page++) {
            Cell cell = displayCell.get(page);
            if (page==configurator.pageNumber){
                configurator.thumbnailCache().setCommence(cell.keys()[0]);
            }
            configurator.thumbnailCache().setClosure(cell.keys()[1]);
            height += cell.size.height;

        }
        for (int page = configurator.pageNumber, height = (int) -distance; height < packSize.height && page < pageCount; page++) {
            Cell cell = displayCell.get(page);
            cell.layoutKeep((int) -offset, height, (int) -offset + cell.size.width, height + cell.size.height);
            height += cell.size.height;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0) return;
        packSize = new Size(w, h);
        if (configurator != null) {
            configurator.packSize.width = w;
            configurator.packSize.height = h;
            configurator.build();
        }
    }


    @SuppressLint("DefaultLocale")
    protected void tryToad(Configurator configurator) {
        if (prepared == Prepare.PREPARING) return;
        if (packSize == null) return;
        prepared = Prepare.PREPARING;
        Logger.t("").log("try load");
        //检查文件 页数 当前页面 是否正确 ，不正确则认为加载失败
        if (configurator.core.pageCount() == 0) {
            prepared = Prepare.FAIL;
        }
        if (configurator.pageNumber >= configurator.core.pageCount()) {
            prepared = Prepare.FAIL;
        }

        if (prepared == Prepare.FAIL) {
            if (configurator.errorListener != null)
                configurator.errorListener.onError(new RuntimeException(String.format("pageCount:%d , pageNumber:%d", configurator.core.pageCount(), configurator.pageNumber)));
            Logger.t("").log("try load fail");
            return;
        }
        configurator.thumbnailPool.launch();
        PdfLoader pdfLoader = new PdfLoader(this);
        pdfLoader.execute(configurator);

    }

    public Configurator formFile(File file) {
        return new Configurator(this).core(new PDFCore(file.getAbsolutePath()));
    }

    public Configurator formPath(String file) {
        return new Configurator(this).core(new PDFCore(file));
    }

    public Configurator formStream(SeekableInputStream stream) {
        return new Configurator(this).core(new PDFCore(stream));
    }

    private synchronized void scaleToKernel(float scale, PointF pointF) {
        int scalePage = configurator.pageNumber;
        int pageCount = configurator.core.pageCount();
        ScaleMode mode = configurator.scaleMode;
        float offset = (this.offset / configurator.scale + pointF.x / configurator.scale) * scale - pointF.x;
        float distance = (this.distance / configurator.scale + pointF.y / configurator.scale) * scale - pointF.y;
        configurator.scale = scale;
        transverseLength = (configurator.scale - 1f) * packSize.width;
        if (offset < 0) offset = 0;
        else if (offset > transverseLength) offset = transverseLength;
        this.offset = offset;
        for (Map.Entry<Integer, Cell> entry : displayCell.entrySet()) {
            entry.getValue().reMeasure();
        }
        if (distance > this.distance) {//放大
            if (distance > displayCell.get(configurator.pageNumber).size.height) {
                //把顶部的页面放大没了
                this.distance = distance - displayCell.get(configurator.pageNumber).size.height;
                Logger.t(TAG_RECYCLER_SCALE_TOP).log("remove:%d", configurator.pageNumber);
                Cell rubbish = displayCell.remove(configurator.pageNumber);
                removeView(rubbish);
                configurator.cellCache.holdCell(rubbish, configurator.pageNumber, mode);
                configurator.pageNumber++;
            } else {
                this.distance = distance;
            }
            //计算最后一个超出cell布局的高度 bottomRemaining
            int bottomRemaining = (int) -this.distance;
            int bottomRemainingPage = configurator.pageNumber;
            for (; bottomRemaining < packSize.height && bottomRemainingPage < pageCount; bottomRemainingPage++) {
                Cell cell = displayCell.get(bottomRemainingPage);
                bottomRemaining += cell.size.height;
            }
            bottomRemainingPage--;//循环里面++
            //无法测量出超出高度之外的布局
            int overPage = bottomRemainingPage + 1;
            Cell overCell = displayCell.get(overPage);
            if (overCell != null) {//说明当前有超出的布局
                Logger.t(TAG_RECYCLER_SCALE_BOTTOM).log("remove:%d", overPage);
                Cell rubbish = displayCell.remove(overPage);
                removeView(rubbish);
                configurator.cellCache.holdCell(rubbish, overPage, mode);
            }

        } else if (distance < this.distance) {//缩小
            boolean topped = false;
            if (distance < 0) {
                configurator.pageNumber--;
                if (configurator.pageNumber >= 0) {
                    Cell cell = configurator.cellCache.achieveCell(configurator.pageNumber, mode);
                    addView(cell);
                    Logger.t(TAG_RECYCLER_SCROLL_TOP).log("add:%d", configurator.pageNumber);
                    displayCell.put(configurator.pageNumber, cell);
                    this.distance = distance + cell.size.height;
                } else {
                    topped = true;
                    configurator.pageNumber++;
                    this.distance = 0;
                }
            } else {
                this.distance = distance;
            }

            int tailPage = -1;
            int renderHeight = (int) -this.distance;
            int lastRender = -1;
            for (int page = configurator.pageNumber; renderHeight < packSize.height && page < pageCount; page++) {
                Cell cell = displayCell.get(page);
                if (cell == null) {
                    tailPage = page;
                    break;
                }
                lastRender = page;
                renderHeight += cell.size.height;
            }

            if (renderHeight < packSize.height && lastRender + 1 == pageCount && !topped) {//说明是最后一页了
                int diff = (packSize.height - renderHeight);
                this.distance -= diff;
                if (this.distance < 0) {
                    Logger.t(TAG_RECYCLER_SCROLL_BOTTOM).log("最后一页重置偏移量:%f", this.distance);
                    configurator.pageNumber--;
                    if (configurator.pageNumber >= 0) {
                        Cell cell = configurator.cellCache.achieveCell(configurator.pageNumber, mode);
                        addView(cell);
                        Logger.t(TAG_RECYCLER_SCROLL_TOP).log("add:%d", configurator.pageNumber);
                        displayCell.put(configurator.pageNumber, cell);
                        this.distance = distance + cell.size.height;
                    } else {
                        configurator.pageNumber++;
                        this.distance = 0;
                    }
                    Logger.t(TAG_RECYCLER_SCROLL_BOTTOM).log("最后一页重置偏移量:%f", this.distance);
                }
            }

            if (tailPage != -1) {
                if (tailPage < pageCount) {
                    Logger.t(TAG_RECYCLER_SCALE_BOTTOM).log("add:%d", tailPage);
                    Cell cell = configurator.cellCache.achieveCell(tailPage, mode);
                    addView(cell);
                    Logger.t(TAG_RECYCLER_SCROLL_BOTTOM).log("add:%d", tailPage);
                    displayCell.put(tailPage, cell);
                }
            }
        }
        if (configurator.pageNumber != scalePage && prepared == Prepare.PREPARED)
            if (configurator.pageChangeListener != null)
                configurator.pageChangeListener.onPageChanged(configurator.pageNumber, pageCount);
        layoutCell();
    }

    private synchronized boolean moveByRelativeKernel(float distanceX, float distanceY) {
        int pageCount = configurator.core.pageCount();
        int movePage = configurator.pageNumber;
        ScaleMode mode = configurator.scaleMode;
        if (!configurator.transverseEnable) distanceX = 0;
        float relOffset = offset + distanceX;
        float relDistance = distance + distanceY;
        boolean contentChange = false;
        if (relOffset < 0) relOffset = 0;
        else if (relOffset > transverseLength) relOffset = transverseLength;
        //计算最后一个超出cell布局的高度 bottomRemaining
        int bottomRemaining = (int) -distance;
        Cell bottomRemainingCell = null;
        int bottomRemainingPage = configurator.pageNumber;
        for (; bottomRemaining < packSize.height && bottomRemainingPage < pageCount; bottomRemainingPage++) {
            Cell cell = displayCell.get(bottomRemainingPage);
            bottomRemaining += cell.size.height;
            bottomRemainingCell = cell;
        }
        bottomRemaining = bottomRemaining - packSize.height;
        bottomRemainingPage--;//循环里面++
        if (distanceY < 0) {
            if (relDistance < 0) {
                if (configurator.pageNumber > 0) {
                    configurator.pageNumber--;
                    Cell cell = configurator.cellCache.achieveCell(configurator.pageNumber, mode);
                    addView(cell);
                    contentChange = true;
                    Logger.t(TAG_RECYCLER_SCROLL_TOP).log("add:%d", configurator.pageNumber);
                    displayCell.put(configurator.pageNumber, cell);
                    relDistance = cell.size.height + relDistance;
                } else {
                    relDistance = 0f;
                }
            }
            if (bottomRemaining - distanceY > bottomRemainingCell.size.height) {
                Logger.t(TAG_RECYCLER_SCALE_BOTTOM).log("remove:%d", bottomRemainingPage);
                Cell rubbish = displayCell.remove(bottomRemainingPage);
                removeView(rubbish);
                contentChange = true;
                configurator.cellCache.holdCell(rubbish, configurator.pageNumber - 1, mode);
            }
        } else if (distanceY > 0) {
            if (bottomRemaining > 0) {
                if (bottomRemaining - distanceY < 0) {
                    if (bottomRemainingPage + 1 < pageCount) {
                        Cell cell = configurator.cellCache.achieveCell(bottomRemainingPage + 1, mode);
                        addView(cell);
                        contentChange = true;
                        Logger.t(TAG_RECYCLER_SCROLL_BOTTOM).log("add:%d", bottomRemainingPage + 1);
                        displayCell.put(bottomRemainingPage + 1, cell);
                    } else {
                        relDistance = distance + bottomRemaining;
                    }
                }
            } else {
                relDistance = distance;
            }
            float remaining = relDistance - displayCell.get(configurator.pageNumber).size.height;
            if (remaining > 0) {
                Logger.t(TAG_RECYCLER_SCROLL_TOP).log("remove:%d", configurator.pageNumber);
                Cell rubbish = displayCell.remove(configurator.pageNumber);
                removeView(rubbish);
                contentChange = true;
                configurator.cellCache.holdCell(rubbish, bottomRemainingPage + 1, mode);
                configurator.pageNumber++;
                relDistance = remaining;
            }
        }

        if (offset == relOffset && distance == relDistance && !contentChange) return false;
        offset = relOffset;
        distance = relDistance;
        layoutCell();
        if (configurator.pageNumber != movePage && prepared == Prepare.PREPARED)
            if (configurator.pageChangeListener != null)
                configurator.pageChangeListener.onPageChanged(configurator.pageNumber, pageCount);
        return true;
    }

    public void onScale(float scale, PointF point) {
        if (prepared != Prepare.PREPARED) return;
        scaleToKernel(scale, point);
        if (configurator.scaleListener != null)
            configurator.scaleListener.onScale(scale, point);
    }

    public boolean onScroll(float distanceX, float distanceY) {
        if (prepared != Prepare.PREPARED) return false;
        boolean isScrolled = moveByRelativeKernel(distanceX, distanceY);
        if (configurator.pageScrollListener != null)
            configurator.pageScrollListener.onPageScrolled(isScrolled, configurator.pageNumber, distance, distanceX, distanceY);
        return isScrolled;
    }

    public void onScaleEnd() {
        for (Map.Entry<Integer, Cell> entry : displayCell.entrySet()) {
            entry.getValue().onReload();
        }

        for (Map.Entry<Integer, Cell> entry : displayCell.entrySet()) {
            entry.getValue().reMeasure();
        }
    }

    public void onLongPress(MotionEvent e) {
        if (prepared == Prepare.PREPARED && configurator.longPressListener != null)
            configurator.longPressListener.onLongPress(e);
    }

    public void addView(Cell child) {
        if (indexOfChild(child) == -1) {
            super.addView(child);
        }
    }

    public void removeView(Cell view) {
        super.removeView(view);
    }

    public void onSingleTap(MotionEvent e) {
        if (prepared == Prepare.PREPARED && configurator.tapListener != null)
            configurator.tapListener.onTap(e);
    }

    public void onDoubleTap(MotionEvent e) {
        if (prepared == Prepare.PREPARED && configurator.doubleTapListener != null)
            configurator.doubleTapListener.onDoubleTap(e);
    }

    @Override
    public void onPdfLoaded() {
        int pageCount = configurator.core.pageCount();
        transverseLength = (configurator.scale - 1f) * packSize.width;
        displayCell.clear();
        for (int page = configurator.pageNumber, height = 0; height < packSize.height && page < pageCount; page++) {
            Cell cell = configurator.cellCache.achieveCell(page, configurator.scaleMode);
            addView(cell);
            Logger.t(TAG_CREATE).log("add:" + page);
            displayCell.put(page, cell);
            height += cell.size.height;
        }
        prepared = Prepare.PREPARED;
        requestLayout();
    }

    public Configurator getConfigurator() {
        return configurator;
    }
}
