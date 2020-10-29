package com.hacknife.pdfviewer.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.LogZ;
import com.hacknife.pdfviewer.loader.CellLoader;

import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.listener.OnCellLoaderListener;


public class Cell extends androidx.appcompat.widget.AppCompatImageView implements OnCellLoaderListener {

    private PdfView.Configurator configurator;
    private CellLoader cellLoader;
    private PDFCore core;
    private int pageNumber = -1;
    private PDFCore.MODE pageMode;
    public Size size = new Size(0, 0);
    public Rect displayRect;

    public Cell(@NonNull Context context, PdfView.Configurator configurator) {
        super(context);
        this.configurator = configurator;
        this.core = configurator.core();
        cellLoader = new CellLoader(this);
        displayRect = new Rect(0, 0, 0, 0);
        LogZ.log("create cell");
    }

    public void reMeasure() {
        SizeF size = core.getPageSize(pageNumber);
        Size packSize = configurator.packSize();
        if (this.pageMode == PDFCore.MODE.WIDTH)
            size.widthScaleTo(packSize.width);
        else
            size.heightScaleTo(packSize.height);
        size = size.scale(configurator.scale());
        Size measureSize = size.toSize();
        if (this.size == null || (!(this.size.equals(measureSize)))) {
            this.size = size.toSize();
            LogZ.log("measureSize:%s", measureSize.toString());
            setMeasuredDimension(this.size.width, this.size.height);
        }
    }

    public Cell loadCell(int pageNumber, PDFCore.MODE mode) {
        if (this.pageNumber == pageNumber && this.pageMode == mode) return this;
        this.pageNumber = pageNumber;
        this.pageMode = mode;
        this.reMeasure();
        this.cellLoader.load(Bitmap.createBitmap(this.size.width, this.size.height, Bitmap.Config.ARGB_8888));
        return this;
    }

    @Override
    public Bitmap onLoadBitmap(Bitmap bitmap) {
        core.drawPage(bitmap, pageNumber, pageMode == PDFCore.MODE.WIDTH ? size.width : size.height, pageMode, 0, 0, 1);
        return bitmap;
    }

    @Override
    public void onDraw(Bitmap bitmap) {
        setImageBitmap(bitmap);
    }


    public void layoutKeep(int l, int t, int r, int b) {
        displayRect.set(l, t, r, b);
        layout(l, t, r, b);
    }
}
