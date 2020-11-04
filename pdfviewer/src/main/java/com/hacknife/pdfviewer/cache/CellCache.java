package com.hacknife.pdfviewer.cache;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.helper.TaskFactory;
import com.hacknife.pdfviewer.widget.Cell;
import com.hacknife.pdfviewer.state.ScaleMode;

import java.util.HashMap;
import java.util.Map;

public class CellCache {
    public static final String TAG = "CellCache";
    private PdfView context;
    private Configurator configurator;
    private Map<Integer, Cell> cellMap;


    public CellCache(PdfView context, Configurator configurator) {
        this.context = context;
        this.configurator = configurator;
        this.cellMap = new HashMap<>();
    }

    public Cell achieveCell(int page, ScaleMode mode) {
        if (cellMap.containsKey(page)) {
            return cellMap.remove(page).loadCell(page, mode);
        } else
            return new Cell(context, configurator).loadCell(page, mode);
    }

    public void holdCell(Cell cell, int page, ScaleMode mode, boolean bottom) {
        if (!cellMap.containsKey(page) && page >= 0 && page < configurator.core().pageCount()) {
            cell.loadCell(page, mode);
            cellMap.put(page, cell);
        }
    }


    public void clear() {
        cellMap.clear();
    }
}
