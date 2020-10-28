package com.hacknife.pdfviewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.artifex.mupdf.fitz.SeekableInputStream;

import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.CellHelper;
import com.hacknife.pdfviewer.helper.LogZ;
import com.hacknife.pdfviewer.listener.OnDoubleTapListener;
import com.hacknife.pdfviewer.listener.OnDrawListener;
import com.hacknife.pdfviewer.listener.OnErrorListener;
import com.hacknife.pdfviewer.listener.OnLongPressListener;
import com.hacknife.pdfviewer.listener.OnPageChangeListener;
import com.hacknife.pdfviewer.listener.OnPageErrorListener;
import com.hacknife.pdfviewer.listener.OnPageScrollListener;
import com.hacknife.pdfviewer.listener.OnTapListener;
import com.hacknife.pdfviewer.model.BmPack;
import com.hacknife.pdfviewer.model.Cell;
import com.hacknife.pdfviewer.model.Direction;


import java.io.File;

import static com.hacknife.pdfviewer.model.BmPack.SPACE;


public class PdfView extends View {

    protected Configurator configurator;
    private int width;
    private int height;
    private Cell cell;
    private Paint bmPaint;
    public boolean loaded = false;
    private float offsetX = 0f;
    private float offsetY = 0f;
    private DragPinchManager dragPinchManager;
    private boolean scaling = false;

    public PdfView(Context context) {
        this(context, null);
    }

    public PdfView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PdfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bmPaint = new Paint();
        bmPaint.setAntiAlias(true);
        dragPinchManager = new DragPinchManager(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        if (configurator != null) configurator.build();
    }

    private void createCell() {
        if (width == 0 || height == 0) return;
        int cellW = CellHelper.calculateCellSize(configurator.bmPack.col - SPACE, width);
        int cellH = CellHelper.calculateCellSize(configurator.bmPack.row - SPACE, height);
        configurator.bmPack.width = cellW;
        configurator.bmPack.height = cellH;
        Log.v("dzq", "cellW:" + cellW + " , cellH:" + cellH);
        cell = CellHelper.createCells(configurator.bmPack.col, configurator.bmPack.row, cellW, cellH);
        loadPdfToCell(cell);
    }


    private void loadPdfToCell(Cell cell) {
        loaded = false;
        CellHelper.loadPdfToCell(configurator, cell, 0, 0, 0, configurator.scale, width, height, true);
        loaded = true;
        postInvalidate();
    }

    private void drawRow(Canvas canvas, Cell cellR, float dH) {
        float dW = 0f;
        float cellW = configurator.bmPack.width;
        float cellH = configurator.bmPack.height;
        while (dW < width) {
            Rect src = new Rect(0, 0, (int) cellW, (int) cellH);
            Rect dest = new Rect((int) dW, (int) dH, (int) (dW + cellW), (int) (dH + cellH));
            canvas.drawBitmap(cellR.bitmap, src, dest, bmPaint);
            dW = dW + cellW;
            cellR = cellR.next(Direction.HORIZONTAL);
        }
    }

    public void drawCells(Canvas canvas, Cell cellC) {
        float dH = 0;
        float cellH = configurator.bmPack.height;
        while (dH < height) {
            drawRow(canvas, cellC, dH);
            dH = dH + cellH;
            cellC = cellC.next(Direction.VERTICAL);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (cell == null) return;
        if (!loaded) return;
        drawCells(canvas, cell);
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
        scaling = true;
        configurator.scale *= scale;
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
        private Direction rollingDirection = Direction.VERTICAL;
        private PDFCore core;
        private float centerX = 0.5f;
        private float centerY = 0.5f;
        protected float scale = 1f;
        private BmPack bmPack = new BmPack(10, 10);


        public Configurator bmPack(BmPack bmPack) {
            this.bmPack = bmPack;
            return this;
        }

        public BmPack bmPack() {
            return bmPack;
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
            view.configurator = this;
            view.createCell();
        }
    }


}
