package com.hacknife.pdfviewer.cache;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.model.Cell;
import com.hacknife.pdfviewer.state.ScaleMode;

import java.util.HashMap;
import java.util.Map;

public class CellCache {
    public static final String TAG = "cell_cache";
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
            Logger.t(TAG).log("从缓存读取:%d , mode:%s", page, mode.toString());
            return cellMap.get(page).loadCell(page, mode);
        } else
            return new Cell(context, configurator).loadCell(page, mode);
    }

    public void holdCell(Cell cell, int page, ScaleMode mode) {
        Logger.t(TAG).log("保存到缓存:%d , mode:%s", page, mode.toString());
        if (!cellMap.containsKey(page) && page >= 0 && page < configurator.core().pageCount()) {
            cell.loadCell(page, mode);
            cellMap.put(page, cell);
        }
    }


    public void clear() {
        cellMap.clear();
    }
}
