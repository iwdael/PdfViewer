package com.hacknife.pdfviewer.model;

import android.content.Context;

import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.core.PDFCore;

import java.util.HashMap;
import java.util.Map;

public class CacheCell {
    private Context context;
    private PdfView.Configurator configurator;
    private Map<Integer, Cell> cellMap;

    public CacheCell(Context context, PdfView.Configurator configurator) {
        this.context = context;
        this.configurator = configurator;
        this.cellMap = new HashMap<>();
    }

    public Cell achieveCell(int page, PDFCore.MODE mode) {
        if (cellMap.containsKey(page))
            return cellMap.get(page).loadCell(page, mode);
        else
            return new Cell(context, configurator).loadCell(page, mode);
    }

    public void holdCell(Cell cell, int page, PDFCore.MODE mode) {
        if (!cellMap.containsKey(page)) {
            cell.loadCell(page, mode);
            cellMap.put(page, cell);
        }
    }


}
