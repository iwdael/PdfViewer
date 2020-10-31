package com.hacknife.pdfviewer.loader;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.cache.PageCache;
import com.hacknife.pdfviewer.cache.ThumbnailCache;
import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.model.PDF;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.model.SizeF;
import com.hacknife.pdfviewer.state.ScaleMode;

public class PdfLoader extends AsyncTask<Configurator, Void, Void> {

    private OnPdfLoaderListener pdfLoaderListener;

    public PdfLoader(OnPdfLoaderListener pdfLoaderListener) {
        this.pdfLoaderListener = pdfLoaderListener;
    }

    @Override
    protected Void doInBackground(Configurator... configurators) {
        Configurator configurator = configurators[0];
        PDFCore pdfCore = configurator.core();
        //加载页面大小
        PageCache pageCache = new PageCache();
        for (int i = 0; i < pdfCore.pageCount(); i++) {
            pageCache.put(i, new PDF(i, pdfCore.getPage(i)));
        }
        configurator.pageCache(pageCache);
        //加载缩略图
        ThumbnailCache thumbnailCache = new ThumbnailCache();
        Size packSize = configurator.packSize();
        ScaleMode mode = configurator.scaleMode();
        int pageNumber = configurator.pageNumber();
        int thumbnailCount = configurator.thumbnailCount();
        int page = pageNumber - (thumbnailCount / 3);
        if (page < 0) page = 0;
        if (thumbnailCount >= pdfCore.pageCount()) thumbnailCount = pdfCore.pageCount() - 1;
        for (; page < thumbnailCount; page++) {
            SizeF size = mode == ScaleMode.WIDTH ? pageCache.getPage(page).size.widthScaleTo(packSize.width) : pageCache.getPage(page).size.widthScaleTo(packSize.height);
            size.scale(0.25f);
            Size bmSize = size.toSize();
            Bitmap bitmap = Bitmap.createBitmap(bmSize.width, bmSize.height, Bitmap.Config.ARGB_8888);
            pageCache.getPage(page).drawPage(bitmap, mode == ScaleMode.WIDTH ? bmSize.width : bmSize.height, mode, 0, 0, 1);
            thumbnailCache.put(page, bitmap);
        }
        configurator.thumbnailCache(thumbnailCache);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        pdfLoaderListener.onPdfLoaded();
    }

    public interface OnPdfLoaderListener {
        void onPdfLoaded();
    }
}
