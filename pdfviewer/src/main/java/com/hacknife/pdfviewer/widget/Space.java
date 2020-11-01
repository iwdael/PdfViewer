//package com.hacknife.pdfviewer.widget;
//
//import android.view.View;
//
//import com.hacknife.pdfviewer.PdfView;
//
//public class Space extends View {
//    private PdfView context;
//
//
//    public Space(PdfView context) {
//        super(context.getContext());
//        this.context = context;
//    }
//
//
//    public void layout(int page, int l, int t, int r, int b) {
//        if (page != 0) {
//            if (getParent() == null) context.addView(this);
//            layout(l, t, r, b);
//        } else {
//            if (context.indexOfChild(this) != -1) context.removeView(this);
//        }
//    }
//}
