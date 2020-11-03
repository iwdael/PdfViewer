package com.hacknife.pdfviewer.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.listener.OnThumbnailListener;
import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.listener.OnCellLoaderListener;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.model.SizeF;
import com.hacknife.pdfviewer.state.ScaleMode;


public class Cell extends Page implements OnThumbnailListener {
    public static final String TAG = "cell";
    private ScaleMode pageMode;
    public Size size = new Size(0, 0);


    public Cell(@NonNull PdfView context, Configurator configurator) {
        super(context.getContext(), configurator);
        configurator.thumbnailListeners().add(this);
    }

    private boolean reMeasure(int pageNumber) {
        SizeF size = configurator.pageCache().getPage(pageNumber).size;
        Size packSize = configurator.packSize();
        if (this.pageMode == ScaleMode.WIDTH)
            size = size.widthScaleTo(packSize.width);
        else
            size = size.heightScaleTo(packSize.height);
        size = size.scale(configurator.scale());
        Size measureSize = size.toSize();
        if (pageNumber != 0) measureSize.height += configurator.space();
        if (this.size == null || (!(this.size.equals(measureSize)))) {
            this.size = measureSize;
            return true;
        }
        return false;
    }

    public boolean reMeasure() {
        return reMeasure(pageNumber);
    }

    public Cell loadCell(int pageNumber, ScaleMode mode) {
        this.pageMode = mode;
        this.setPage(pageNumber);
        this.reMeasure(pageNumber);
        invalidate();
        return this;
    }


    @Override
    public void onReload() {
        postInvalidate();
    }

    public void layoutKeep(int l, int t, int r, int b) {
        layout(l, t, r, b);
        checkThumbnail();
    }


    @Override
    public boolean onThumbnail(Patch patch) {
        return patch.page == pageNumber;
    }
}
