package com.hacknife.pdfviewer.demo;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.listener.OnErrorListener;
import com.hacknife.pdfviewer.listener.OnLongPressListener;
import com.hacknife.pdfviewer.state.Direction;
import com.hacknife.pdfviewer.PdfView;

public class CoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final PdfView view = new PdfView(this);

        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Uri uri = getIntent().getData();
        if (uri.getScheme().equals("file")) {
            String path = uri.getPath();
            view.formPath(path)
                    .rollingDirection(Direction.HORIZONTAL)
//                    .pageNumber(2)
//                    .scale(2)
//                    .space(50)
                    .transverseEnable(false)
                    .onLongPress(e -> view.getConfigurator().transverseEnable(!view.getConfigurator().transverseEnable()))
                    .onError(t -> Logger.t("").log("error:"+t.toString()))
                    .build();
        }


    }


}
