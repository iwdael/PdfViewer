package com.hacknife.pdfviewer.loader;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.cache.PageCache;
import com.hacknife.pdfviewer.cache.ThumbnailCache;
import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.model.Page;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.model.SizeF;

public  class PdfLoader extends AsyncTask<Configurator, Void, Void> {

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
            pageCache.put(i, new Page(i, pdfCore.getPageSize(i)));
        }
        configurator.pageCache(pageCache);
        //加载缩略图
        ThumbnailCache thumbnailCache =new ThumbnailCache();
        Size packSize = configurator.packSize();
        PDFCore.MODE mode = configurator.mode();
        int pageNumber = configurator.pageNumber();
        int thumbnailCount = configurator.thumbnailCount();
        int page = pageNumber - (thumbnailCount / 3);
        if (page < 0) page = 0;
        if (thumbnailCount >= pdfCore.pageCount()) thumbnailCount = pdfCore.pageCount() - 1;
        for (; page < thumbnailCount; page++) {
            SizeF size = mode == PDFCore.MODE.WIDTH ? pageCache.getPage(page).size.widthScaleTo(packSize.width) : pageCache.getPage(page).size.widthScaleTo(packSize.height);
            size.scale(0.25f);
            Size bmSize = size.toSize();
            Bitmap bitmap = Bitmap.createBitmap(bmSize.width, bmSize.height, Bitmap.Config.ARGB_8888);
            pdfCore.drawPage(bitmap, page, mode == PDFCore.MODE.WIDTH ? bmSize.width : bmSize.height, mode, 0, 0, 1);
            thumbnailCache.put(page,bitmap);
        }
        configurator.thumbnailCache(thumbnailCache);
        Logger.t("").log(pageCache.toString());
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        pdfLoaderListener.onPdfLoaded();
    }

    public interface OnPdfLoaderListener{
        void onPdfLoaded();
    }
}
