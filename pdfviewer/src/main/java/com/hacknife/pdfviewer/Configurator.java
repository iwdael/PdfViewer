package com.hacknife.pdfviewer;

import android.graphics.Color;

import com.hacknife.pdfviewer.cache.CellCache;
import com.hacknife.pdfviewer.cache.PageCache;
import com.hacknife.pdfviewer.cache.ThumbnailCache;
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
import com.hacknife.pdfviewer.listener.OnThumbnailListener;
import com.hacknife.pdfviewer.loader.ThumbnailPool;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.state.Direction;
import com.hacknife.pdfviewer.state.ScaleMode;

import java.util.ArrayList;
import java.util.List;

public class Configurator {
    public static final String TAG = Configurator.class.getSimpleName();
    protected PdfView view;


    protected Configurator(PdfView view) {
        this.view = view;
        this.cellCache = new CellCache(view, this);

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
    protected boolean scaleEnable = true;
    protected boolean transverseEnable = true;
    protected Direction rollingDirection = Direction.VERTICAL;
    protected PDFCore core;
    protected float centerX = 0.5f;
    protected float centerY = 0.5f;
    protected float scale = 1f;
    protected Size packSize = new Size(0, 0);
    protected int pageNumber;
    protected int space = 2;
    //    protected int spaceColor = Color.parseColor("#FF000000");
    protected int spaceColor = Color.parseColor("#F70FFFE8");
    protected ThumbnailCache thumbnailCache;
    protected PageCache pageCache;
    protected CellCache cellCache;
    protected int thumbnailCount = 10;
    protected ScaleMode scaleMode = ScaleMode.WIDTH;
    protected ThumbnailPool thumbnailPool;
    protected float thumbnailScale = 0.25f;
    //缩略图每列碎片个数
    protected int thumbnailPatchLandscapeCount = 4;
    protected List<OnThumbnailListener> thumbnailListeners = new ArrayList<>();
    //容器横向大小
    protected int pageSize = 0;
    //缩略图横向大小
    protected int thumbnailLandscapeSize = 0;
    //缩略图碎片大小 正方形
    protected int thumbnailPatchSize = 0;
    //缩略图碎片总个数
    protected int thumbnailPatchCount = 0;

    public int thumbnailPatchCount() {
        return thumbnailPatchCount;
    }

    public int thumbnailPatchSize() {
        return thumbnailPatchSize;
    }

    public int thumbnailLandscapeSize() {
        return thumbnailLandscapeSize;
    }

    public int pageSize() {
        return pageSize;
    }

    public List<OnThumbnailListener> thumbnailListeners() {
        return thumbnailListeners;
    }

    public float thumbnailScale() {
        return thumbnailScale;
    }


    public ThumbnailPool thumbnailPool() {
        return thumbnailPool;
    }

    public Configurator scaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        return this;
    }

    public ScaleMode scaleMode() {
        return scaleMode;
    }

    public Configurator thumbnailCount(int thumbnailCount) {
        this.thumbnailCount = thumbnailCount;
        return this;
    }


    public PageCache pageCache() {
        return pageCache;
    }

    public Configurator pageCache(PageCache pageCache) {
        this.pageCache = pageCache;
        return this;
    }

    public ThumbnailCache thumbnailCache() {
        return thumbnailCache;
    }

    public Configurator thumbnailCache(ThumbnailCache thumbnailCache) {
        this.thumbnailCache = thumbnailCache;
        return this;
    }

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
        view.configurator = this;
        if (view.packSize == null) return;
        this.packSize.width = view.packSize.width;
        this.packSize.height = view.packSize.height;
        this.thumbnailPool = new ThumbnailPool();
        this.pageSize = (scaleMode == ScaleMode.WIDTH ? packSize.width : packSize.height);
        this.thumbnailLandscapeSize = (int) (pageSize * thumbnailScale);
        this.thumbnailPatchSize = (int) (thumbnailLandscapeSize / thumbnailPatchLandscapeCount) + 1;
        this.thumbnailPatchCount = this.thumbnailPatchLandscapeCount * this.thumbnailPatchLandscapeCount * this.thumbnailCount;
        view.tryToad(this);
    }


}