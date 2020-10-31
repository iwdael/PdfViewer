package com.hacknife.pdfviewer.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.loader.CellLoader;

import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.listener.OnCellLoaderListener;
import com.hacknife.pdfviewer.widget.Space;


public class Cell extends androidx.appcompat.widget.AppCompatImageView implements OnCellLoaderListener {

    private Configurator configurator;
    private CellLoader cellLoader;
    private PDFCore core;
    public int pageNumber = -1;
    private PDFCore.MODE pageMode;
    public Size size = new Size(0, 0);

    public Space space;
    private Size bitmapSize = new Size(0, 0);
    private Size willBitmapSize = new Size(0, 0);
    private PdfView context;

    public Cell(@NonNull PdfView context, Configurator configurator) {
        super(context.getContext());
        this.configurator = configurator;
        this.context = context;
        this.core = configurator.core();
        cellLoader = new CellLoader(this);
        space = new Space(context);
        space.setBackgroundColor(configurator.spaceColor());
    }

    public boolean reMeasure() {
        SizeF size = core.getPageSize(pageNumber);
        Size packSize = configurator.packSize();
        if (this.pageMode == PDFCore.MODE.WIDTH)
            size.widthScaleTo(packSize.width);
        else
            size.heightScaleTo(packSize.height);
        size = size.scale(configurator.scale());
        Size measureSize = size.toSize();
        if (pageNumber != 0) measureSize.height += configurator.space();
        if (this.size == null || (!(this.size.equals(measureSize)))) {
            this.size = measureSize;
            willBitmapSize.width = this.size.width;
            willBitmapSize.height = this.size.height - (pageNumber != 0 ? configurator.space() : 0);
            return true;
        }
        return false;
    }

    public Cell loadCell(int pageNumber, PDFCore.MODE mode) {
        this.pageMode = mode;
        this.reMeasure();
//        if ((!bitmapSize.equals(willBitmapSize)) || (pageNumber != this.pageNumber))
//            this.cellLoader.load(Bitmap.createBitmap(this.willBitmapSize.width, this.willBitmapSize.height, Bitmap.Config.ARGB_8888));
        setImageBitmap(configurator.thumbnailCache().getPage(pageNumber));
        this.pageNumber = pageNumber;
        return this;
    }

    public void reload() {
//        if (!bitmapSize.equals(willBitmapSize))
//            this.cellLoader.load(Bitmap.createBitmap(this.willBitmapSize.width, this.willBitmapSize.height, Bitmap.Config.ARGB_8888));
        setImageBitmap(configurator.thumbnailCache().getPage(pageNumber));
    }

    @Override
    public Bitmap onLoadBitmap(Bitmap bitmap) {
        core.drawPage(bitmap, pageNumber, pageMode == PDFCore.MODE.WIDTH ? bitmap.getWidth() : bitmap.getHeight(), pageMode, 0, 0, 1);
        return bitmap;
    }

    @Override
    public void onDraw(Bitmap bitmap) {
        setImageBitmap(bitmap);
        bitmapSize.width = bitmap.getWidth();
        bitmapSize.height = bitmap.getHeight();
//        bitmap.recycle();
    }


    public void layoutKeep(int l, int t, int r, int b) {
         space.layout(pageNumber, l, t, r, t + configurator.space());
        if (getParent() == null) context.addView(this);
        layout(l, t + (pageNumber != 0 ? configurator.space() : 0), r, b);

//        if (this.pageNumber % 2 == 0)
//            setBackgroundColor(Color.parseColor("#EB3700B3"));
//        else
//            setBackgroundColor(Color.parseColor("#FF555555"));

    }
}
