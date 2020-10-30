package com.hacknife.pdfviewer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.artifex.mupdf.fitz.SeekableInputStream;
import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.listener.OnDoubleTapListener;
import com.hacknife.pdfviewer.listener.OnDrawListener;
import com.hacknife.pdfviewer.listener.OnErrorListener;
import com.hacknife.pdfviewer.listener.OnLongPressListener;
import com.hacknife.pdfviewer.listener.OnPageChangeListener;
import com.hacknife.pdfviewer.listener.OnPageErrorListener;
import com.hacknife.pdfviewer.listener.OnPageScrollListener;
import com.hacknife.pdfviewer.listener.OnScaleListener;
import com.hacknife.pdfviewer.listener.OnTapListener;
import com.hacknife.pdfviewer.model.Cache;
import com.hacknife.pdfviewer.model.Cell;
import com.hacknife.pdfviewer.model.Direction;
import com.hacknife.pdfviewer.model.Size;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class PdfView extends ViewGroup {
    private static final String TAG_CREATE = "TAG_CREATE";
    private static final String TAG_RECYCLER_SCROLL_TOP = "TAG_RECYCLER_SCROLL_TOP";
    private static final String TAG_RECYCLER_SCROLL_BOTTOM = "TAG_RECYCLER_SCROLL_BOTTOM";
    private static final String TAG_RECYCLER_SCALE_TOP = "TAG_RECYCLER_SCALE_TOP";
    private static final String TAG_RECYCLER_SCALE_BOTTOM = "TAG_RECYCLER_SCALE_BOTTOM";
    protected Configurator configurator;
    private DragPinchManager dragPinchManager;
    private Cache cache;
    private Map<Integer, Cell> displayCell;
    private int pageCount;
    private Size packSize;
    private float offset = 0f;
    private float distance = 0f;
    private float transverseLength = 0f; //页面放大后，非翻滚页面的距离


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
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutCell();
    }

    private void layoutCell() {
        if (configurator == null) return;
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


    private void createCell(Configurator configurator) {
        if (packSize == null) return;
        transverseLength = (configurator.scale - 1f) * packSize.width;
        pageCount = configurator.core.pageCount();
        if (cache != null) cache.clear();
        cache = new Cache(this, this.configurator);
        displayCell.clear();
        for (int page = configurator.pageNumber, height = 0; height < packSize.height && page < pageCount; page++) {
            Cell cell = cache.achieveCell(page, PDFCore.MODE.WIDTH);
            addView(cell);
            Logger.t(TAG_CREATE).log("add:" + page);
            displayCell.put(page, cell);
            height += cell.size.height;
        }
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
                cache.holdCell(rubbish, configurator.pageNumber, PDFCore.MODE.WIDTH);
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
                cache.holdCell(rubbish, overPage, PDFCore.MODE.WIDTH);
            }

        } else if (distance < this.distance) {//缩小
            boolean topped = false;
            if (distance < 0) {
                configurator.pageNumber--;
                if (configurator.pageNumber >= 0) {
                    Cell cell = cache.achieveCell(configurator.pageNumber, PDFCore.MODE.WIDTH);
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
                        Cell cell = cache.achieveCell(configurator.pageNumber, PDFCore.MODE.WIDTH);
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
                    Cell cell = cache.achieveCell(tailPage, PDFCore.MODE.WIDTH);
                    addView(cell);
                    Logger.t(TAG_RECYCLER_SCROLL_BOTTOM).log("add:%d", tailPage);
                    displayCell.put(tailPage, cell);
                }
            }
        }
        if (configurator.pageNumber != scalePage)
            if (configurator.pageChangeListener != null)
                configurator.pageChangeListener.onPageChanged(configurator.pageNumber, pageCount);
        layoutCell();
    }

    private synchronized boolean moveByRelativeKernel(float distanceX, float distanceY) {
        int movePage = configurator.pageNumber;
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
                    Cell cell = cache.achieveCell(configurator.pageNumber, PDFCore.MODE.WIDTH);
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
                cache.holdCell(rubbish, configurator.pageNumber - 1, PDFCore.MODE.WIDTH);
            }
        } else if (distanceY > 0) {
            if (bottomRemaining > 0) {
                if (bottomRemaining - distanceY < 0) {
                    if (bottomRemainingPage + 1 < pageCount) {
                        Cell cell = cache.achieveCell(bottomRemainingPage + 1, PDFCore.MODE.WIDTH);
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
                cache.holdCell(rubbish, bottomRemainingPage + 1, PDFCore.MODE.WIDTH);
                configurator.pageNumber++;
                relDistance = remaining;
            }
        }

        if (offset == relOffset && distance == relDistance && !contentChange) return false;

        offset = relOffset;
        distance = relDistance;
        layoutCell();
        if (configurator.pageNumber != movePage)
            if (configurator.pageChangeListener != null)
                configurator.pageChangeListener.onPageChanged(configurator.pageNumber, pageCount);
        return true;
    }

    public void onScale(float scale, PointF point) {
        scaleToKernel(scale, point);
        if (configurator.scaleListener != null)
            configurator.scaleListener.onScale(scale, point);
    }

    public boolean onScroll(float distanceX, float distanceY) {
        boolean isScrolled = moveByRelativeKernel(distanceX, distanceY);
        if (configurator.pageScrollListener != null)
            configurator.pageScrollListener.onPageScrolled(isScrolled, configurator.pageNumber, distance, distanceX, distanceY);
        return isScrolled;
    }

    public void onScaleEnd() {
        for (Map.Entry<Integer, Cell> entry : displayCell.entrySet()) {
            entry.getValue().reload();
        }

        for (Map.Entry<Integer, Cell> entry : displayCell.entrySet()) {
            entry.getValue().reMeasure();
        }
    }

    public void onLongPress(MotionEvent e) {
        if (configurator.longPressListener != null) configurator.longPressListener.onLongPress(e);
    }

    public void addView(Cell child) {
        if (indexOfChild(child) == -1) {
            super.addView(child);
            if (child.pageNumber != 0 && child.space.getParent() == null) {
                super.addView(child.space);
            }
        }
    }

    public void removeView(Cell view) {
        super.removeView(view);
        super.removeView(view.space);
    }

    public void onSingleTap(MotionEvent e) {
        if (configurator.tapListener != null) configurator.tapListener.onTap(e);
    }

    public void onDoubleTap(MotionEvent e) {
        if (configurator.doubleTapListener != null) configurator.doubleTapListener.onDoubleTap(e);
    }

    public static class Configurator {
        private PdfView view;

        protected Configurator(PdfView view) {
            this.view = view;
        }

        public static final int SCALE_MIN = 1;
        public static final int SCALE_MAX = 3;
        protected OnDoubleTapListener doubleTapListener;
        protected OnDrawListener drawListener;
        protected OnErrorListener errorListener;
        protected OnLongPressListener longPressListener;
        protected OnPageChangeListener pageChangeListener;
        protected OnPageErrorListener pageErrorListener;
        protected OnPageScrollListener pageScrollListener;
        protected OnTapListener tapListener;
        protected OnScaleListener scaleListener;
        protected int scaleMin = SCALE_MIN;
        protected int scaleMax = SCALE_MAX;
        private boolean scaleEnable = true;
        private boolean transverseEnable = true;
        private Direction rollingDirection = Direction.VERTICAL;
        private PDFCore core;
        private float centerX = 0.5f;
        private float centerY = 0.5f;
        protected float scale = 1f;
        protected Size packSize = new Size(0, 0);
        protected int pageNumber;
        protected int space = 2;
        protected int spaceColor = Color.parseColor("#FF000000");


        public Configurator transverseEnable(boolean transverseEnable) {
            this.transverseEnable = transverseEnable;
            return this;
        }

        public boolean transverseEnable() {
            return transverseEnable;
        }

        public Configurator spaceColor(int spaceColor) {
            this.spaceColor = spaceColor;
            return this;
        }

        public int spaceColor() {
            return spaceColor;
        }

        public Configurator space(int space) {
            this.space = space;
            return this;
        }

        public int space() {
            return space;
        }

        public Configurator pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public int pageNumber() {
            return pageNumber;
        }

        public Size packSize() {
            return packSize;
        }

        public float scale() {
            return scale;
        }

        public Configurator scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Configurator centerX(float centerX) {
            this.centerX = centerX;
            return this;
        }

        public Configurator centerY(float centerY) {
            this.centerY = centerY;
            return this;
        }

        public Configurator core(PDFCore core) {
            this.core = core;
            return this;
        }

        public PDFCore core() {
            return core;
        }

        public Configurator rollingDirection(Direction rollingDirection) {
            this.rollingDirection = rollingDirection;
            return this;
        }

        public Direction rollingDirection() {
            return rollingDirection;
        }

        public Configurator setScaleEnable(boolean scaleEnable) {
            this.scaleEnable = scaleEnable;
            return this;
        }

        public boolean scaleEnable() {
            return scaleEnable;
        }

        public Configurator setScaleMax(int scaleMax) {
            this.scaleMax = scaleMax;
            return this;
        }

        public Configurator setScaleMin(int scaleMin) {
            this.scaleMin = scaleMin;
            return this;
        }

        public Configurator onDoubleTapListener(OnDoubleTapListener doubleTapListener) {
            this.doubleTapListener = doubleTapListener;
            return this;
        }

        public Configurator onDrawListener(OnDrawListener drawListener) {
            this.drawListener = drawListener;
            return this;
        }

        public Configurator onError(OnErrorListener errorListener) {
            this.errorListener = errorListener;
            return this;
        }

        public Configurator onLongPress(OnLongPressListener longPressListener) {
            this.longPressListener = longPressListener;
            return this;
        }

        public Configurator onPageChange(OnPageChangeListener pageChangeListener) {
            this.pageChangeListener = pageChangeListener;
            return this;
        }

        public Configurator onPageError(OnPageErrorListener pageErrorListener) {
            this.pageErrorListener = pageErrorListener;
            return this;
        }

        public Configurator onPageScroll(OnPageScrollListener pageScrollListener) {
            this.pageScrollListener = pageScrollListener;
            return this;
        }

        public Configurator onTap(OnTapListener tapListener) {
            this.tapListener = tapListener;
            return this;
        }

        public Configurator onScale(OnScaleListener scaleListener) {
            this.scaleListener = scaleListener;
            return this;
        }

        public void build() {
            view.createCell(this);
            view.configurator = this;
        }
    }

    public Configurator getConfigurator() {
        return configurator;
    }
}
