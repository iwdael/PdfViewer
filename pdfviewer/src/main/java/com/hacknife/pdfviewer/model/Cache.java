package com.hacknife.pdfviewer.model;

import android.content.Context;

import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.core.PDFCore;


import java.util.HashMap;
import java.util.Map;

public class Cache {
    private PdfView context;
    private PdfView.Configurator configurator;
    private Map<Integer, Cell> cellMap;

    public Cache(PdfView context, PdfView.Configurator configurator) {
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
        if (!cellMap.containsKey(page) && page >= 0 && page < configurator.core().pageCount()) {
            cell.loadCell(page, mode);
            cellMap.put(page, cell);
        }
    }


    public void clear() {
        cellMap.clear();
    }
}
