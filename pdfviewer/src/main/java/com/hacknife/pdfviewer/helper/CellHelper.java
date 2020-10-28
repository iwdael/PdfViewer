package com.hacknife.pdfviewer.helper;

import android.graphics.Bitmap;
import android.util.Log;

import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.core.PDFCore;
import com.hacknife.pdfviewer.model.Cell;
import com.hacknife.pdfviewer.model.Direction;
import com.hacknife.pdfviewer.model.SizeF;

public class CellHelper {

    public static void main(String[] args) {
        int col = 10;
        int row = 10;
        boolean isNext = false;
        Cell cell = createCells(col, row, 10, 10);
        Cell temp = cell;


        System.out.println("==========================================================");
        isNext = true;
        for (int i = 0; i < row; i++) {
            printLineCell(col, temp, isNext);
            if (isNext)
                temp = temp.next(Direction.VERTICAL);
            else
                temp = temp.previous(Direction.VERTICAL);
        }

//        System.out.println("==========================================================");
//
//        isNext = false;
//        for (int i = 0; i < row; i++) {
//            printLineCell(col, temp, isNext);
//            if (isNext)
//                temp = temp.next(Direction.VERTICAL);
//            else
//                temp = temp.previous(Direction.VERTICAL);
//        }


        System.out.println("==========================================================");

        temp = cell.next(Direction.VERTICAL).next(Direction.HORIZONTAL);

        isNext = true;
        for (int i = 0; i < row; i++) {
            printLineCell(col, temp, isNext);
            if (isNext)
                temp = temp.next(Direction.VERTICAL);
            else
                temp = temp.previous(Direction.VERTICAL);
        }

        System.out.println("==========================================================");

        temp = cell.next(Direction.VERTICAL).next(Direction.HORIZONTAL);

        isNext = false;
        for (int i = 0; i < row; i++) {
            printLineCell(col, temp, isNext);
            if (isNext)
                temp = temp.next(Direction.VERTICAL);
            else
                temp = temp.previous(Direction.VERTICAL);
        }
    }

    private static void printLineCell(int number, Cell cell, boolean isNext) {
        Cell temp = cell;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < number; i++) {
            builder.append(temp.toString()).append("  ");
            if (isNext)
                temp = temp.next(Direction.HORIZONTAL);
            else
                temp = temp.previous(Direction.HORIZONTAL);
        }
        System.out.println(builder.toString());
    }


    public static Cell createCells(int col, int row, float cellW, float cellH) {
        Cell top = null;
        Cell temp = null;
        for (int i = 0; i < row; i++) {
            Cell c = createRowCell(col, cellW, cellH, i);
            if (top == null) {
                top = c;
            } else {
                Cell topC = temp.next(Direction.HORIZONTAL);
                Cell bottomC = c.next(Direction.HORIZONTAL);
                for (int i1 = 0; i1 < col; i1++) {
                    topC.setBottom(bottomC);
                    bottomC.setTop(topC);
                    topC = topC.next(Direction.HORIZONTAL);
                    bottomC = bottomC.next(Direction.HORIZONTAL);
                }
            }
            temp = c;
        }
        assert top != null;
        Cell topC = top.next(Direction.HORIZONTAL);
        Cell tempC = temp.next(Direction.HORIZONTAL);
        for (int i1 = 0; i1 < col; i1++) {
            topC.setTop(tempC);
            tempC.setBottom(topC);
            topC = topC.next(Direction.HORIZONTAL);
            tempC = tempC.next(Direction.HORIZONTAL);
        }
        return top;
    }

    public static Cell createRowCell(int col, float cellW, float cellH, int row) {
        Cell left = null;
        Cell temp = null;
        for (int i = 0; i < col; i++) {
            Cell c = new Cell(cellW, cellH, i, row);
            if (left == null) left = c;
            if (temp != null) {
                temp.setRight(c);
                c.setLeft(temp);
            }
            temp = c;
        }
        assert left != null;
        left.setLeft(temp);
        temp.setRight(left);
        return left;
    }

    public static void loadPdfToCell(PdfView.Configurator configurator, Cell vertex, int pageNumber, float offsetX, float offsetY, float scale, int pageSizeWidth, int pageSizeHeight, boolean isWidthMode) {
        long start = System.currentTimeMillis();
        PDFCore core = configurator.core();
        SizeF size = core.getPageSize(pageNumber);
        if (isWidthMode)
            size.widthScaleTo(pageSizeWidth);
        else
            size.heightScaleTo(pageSizeHeight);
        size = size.scale(scale);
        int pdfWidth = (int) Math.min(size.width, configurator.bmPack().getRowWidth());
        int pdfHeight = (int) Math.min(size.height, configurator.bmPack().getColHeight());
        Bitmap pdf = Bitmap.createBitmap(pdfWidth, pdfHeight, Bitmap.Config.ARGB_8888);
        core.drawPage(pdf, pageNumber, isWidthMode ? pageSizeWidth : pageSizeHeight, isWidthMode ? PDFCore.MODE.WIDTH : PDFCore.MODE.HEIGHT, offsetX, offsetY, scale);
        float cellW = configurator.bmPack().width;
        float cellH = configurator.bmPack().height;
        float locationY = offsetY;
        Cell cellRow = vertex;
        while (locationY < pdfHeight) {
            Cell cellCol = cellRow;
            float locationX = offsetX;
            while (locationX < pdfWidth) {
                cellCol.bitmap.recycle();
                Log.v("dzq", "read bitmap:" + cellCol.toString() + " , x :" + locationX + " , y:" + locationY + " , w:" + cellW + " , h:" + cellH);
                if (locationY + cellH <= pdfHeight)
                    cellCol.bitmap = Bitmap.createBitmap(pdf, (int) locationX, (int) locationY, (int) cellW, (int) cellH);
                else
                    cellCol.bitmap = Bitmap.createBitmap(pdf, (int) locationX, (int) locationY, (int) cellW, (int) (pdf.getHeight() - locationY));
                cellCol = cellCol.next(Direction.HORIZONTAL);
                locationX += cellW;
            }
            cellRow = cellRow.next(Direction.VERTICAL);
            locationY += cellH;
        }
        pdf.recycle();
        Log.v("dzq", "read time:" + (System.currentTimeMillis() - start));
    }

    public static int calculateCellSize(int col, int size) {
        if (size % col == 0) return size / col;
        for (int i = col; i < col * 2; i++) {
            if (size % i == 0) return size / i;
        }
        Log.v("dzq", "未找到合适的");
        return size;
    }
}
