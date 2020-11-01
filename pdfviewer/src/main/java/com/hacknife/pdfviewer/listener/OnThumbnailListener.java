package com.hacknife.pdfviewer.listener;

import com.hacknife.pdfviewer.model.Patch;

public interface OnThumbnailListener {
    boolean onThumbnail(Patch patch);
    void  onReload();

}
