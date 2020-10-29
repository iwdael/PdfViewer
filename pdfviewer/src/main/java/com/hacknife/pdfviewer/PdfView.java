package com.hacknife.pdfviewer;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.artifex.mupdf.fitz.SeekableInputStream;

import com.hacknife.pdfviewer.core.PDFCore;

import com.hacknife.pdfviewer.helper.LogZ;
import com.hacknife.pdfviewer.listener.OnDoubleTapListener;
import com.hacknife.pdfviewer.listener.OnDrawListener;
import com.hacknife.pdfviewer.listener.OnErrorListener;
import com.hacknife.pdfviewer.listener.OnLongPressListener;
import com.hacknife.pdfviewer.listener.OnPageChangeListener;
import com.hacknife.pdfviewer.listener.OnPageErrorListener;
import com.hacknife.pdfviewer.listener.OnPageScrollListener;
import com.hacknife.pdfviewer.listener.OnTapListener;
import com.hacknife.pdfviewer.model.Cell;
import com.hacknife.pdfviewer.model.CacheCell;
import com.hacknife.pdfviewer.model.Direction;
import com.hacknife.pdfviewer.model.Size;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class PdfView extends ViewGroup {

    protected Configurator configurator;
    private DragPinchManager dragPinchManager;
    private CacheCell cache;
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
        cache = new CacheCell(getContext(), this.configurator);
        for (int page = configurator.pageNumber, height = 0; height < packSize.height && page < pageCount; page++) {
            Cell cell = cache.achieveCell(page, PDFCore.MODE.WIDTH);
            addView(cell);
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

    protected void scaleTo(float scale, PointF pointF) {
        offset = (offset / configurator.scale + pointF.x / configurator.scale) * scale - pointF.x;
        float distance = (this.distance / configurator.scale + pointF.y / configurator.scale) * scale - pointF.y;
        configurator.scale = scale;
        transverseLength = (configurator.scale - 1f) * packSize.width;
        for (Map.Entry<Integer, Cell> entry : displayCell.entrySet()) {
            entry.getValue().reMeasure();
        }
//        LogZ.log("scale:%f , x:%f ,y:%f", configurator.scale, pointF.x, pointF.y);

        if (distance > this.distance) {//放大

        } else {//缩小

        }

        requestLayout();
    }

    public boolean moveByRelative(float distanceX, float distanceY) {
        float relOffset = offset + distanceX;
        float relDistance = distance + distanceY;
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
        bottomRemaining = Math.abs(bottomRemaining - packSize.height);
        bottomRemainingPage--;//循环里面++

        if (distanceY < 0) {
            if (relDistance < 0) {
                if (configurator.pageNumber > 0) {
                    configurator.pageNumber--;
                    Cell cell = cache.achieveCell(configurator.pageNumber, PDFCore.MODE.WIDTH);
                    addView(cell);
                    displayCell.put(configurator.pageNumber, cell);
                    relDistance = cell.size.height + relDistance;
                } else {
                    relDistance = 0f;
                }
            }
            if (bottomRemaining - distanceY > bottomRemainingCell.size.height) {
                LogZ.log("回收1=======%d", bottomRemainingPage);
                Cell rubbish = displayCell.remove(bottomRemainingPage);
                removeView(rubbish);
                cache.holdCell(rubbish, configurator.pageNumber - 1, PDFCore.MODE.WIDTH);
            }
        } else if (distanceY > 0) {
            if (bottomRemaining - distanceY < 0) {
                Cell cell = cache.achieveCell(bottomRemainingPage + 1, PDFCore.MODE.WIDTH);
                addView(cell);
                displayCell.put(bottomRemainingPage + 1, cell);
            }
            float remaining = relDistance - displayCell.get(configurator.pageNumber).size.height;
            if (remaining > 0) {
                LogZ.log("回收2=======%d", configurator.pageNumber);
                Cell rubbish = displayCell.remove(configurator.pageNumber);
                removeView(rubbish);
                cache.holdCell(rubbish, bottomRemainingPage + 1, PDFCore.MODE.WIDTH);
                configurator.pageNumber++;
                relDistance = remaining;
            }
        }
        offset = relOffset;
        distance = relDistance;
        requestLayout();
        return true;
    }

    public static class Configurator {
        private PdfView view;

        protected Configurator(PdfView view) {
            this.view = view;
        }

        public static final int SCALE_MIN = 1;
        public static final int SCALE_MAX = 3;
        private OnDoubleTapListener doubleTapListener;
        private OnDrawListener drawListener;
        private OnErrorListener errorListener;
        private OnLongPressListener longPressListener;
        private OnPageChangeListener pageChangeListener;
        private OnPageErrorListener pageErrorListener;
        private OnPageScrollListener pageScrollListener;
        private OnTapListener tapListener;
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
        protected float space;
        protected int spaceColor;

        public Configurator spaceColor(int spaceColor) {
            this.spaceColor = spaceColor;
            return this;
        }

        public Configurator space(float space) {
            this.space = space;
            return this;
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

        public void build() {
            view.createCell(this);
            view.configurator = this;
        }
    }


}
