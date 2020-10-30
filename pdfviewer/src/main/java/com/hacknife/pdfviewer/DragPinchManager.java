/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hacknife.pdfviewer;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.view.ViewCompat;

import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.helper.Scroller;


import static com.hacknife.pdfviewer.PdfView.Configurator.SCALE_MAX;
import static com.hacknife.pdfviewer.PdfView.Configurator.SCALE_MIN;

/**
 * This Manager takes care of moving the PdfView,
 * set its zoom track user actions.
 */
@SuppressLint("ClickableViewAccessibility")
class DragPinchManager implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener, Runnable {
    public static final String TAG = "Gesture";
    private PdfView pdfView;
    private Scroller scroller;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean isScaling = false;

    DragPinchManager(PdfView pdfView) {
        this.pdfView = pdfView;
        gestureDetector = new GestureDetector(pdfView.getContext(), this);
        scaleGestureDetector = new ScaleGestureDetector(pdfView.getContext(), this);
        pdfView.setOnTouchListener(this);
        scroller = new Scroller(pdfView.getContext(), new DecelerateInterpolator());
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Logger.t(TAG).log("onDoubleTap");
        pdfView.onDoubleTap(e);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Logger.t(TAG).log("onSingleTapUp");
        pdfView.onSingleTap(e);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isScaling) return true;
        return pdfView.onScroll(distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Logger.t(TAG).log("onLongPress");
        pdfView.onLongPress(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        scroller.finish();
        scroller.fling(0, 0, (int) velocityX, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        ViewCompat.postOnAnimation(pdfView, this);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float dr = detector.getScaleFactor();
        float wantedZoom = pdfView.configurator.scale * dr;
        float minZoom = Math.min(SCALE_MIN, pdfView.configurator.scaleMin);
        float maxZoom = Math.min(SCALE_MAX, pdfView.configurator.scaleMax);
        if (wantedZoom < minZoom) {
            dr = minZoom / pdfView.configurator.scale;
        } else if (wantedZoom > maxZoom) {
            dr = maxZoom / pdfView.configurator.scale;
        }
        pdfView.onScale(pdfView.configurator.scale * dr, new PointF(detector.getFocusX(), detector.getFocusY()));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        isScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isScaling = false;
        pdfView.onScaleEnd();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scroller.finish();
        boolean retVal = scaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        return retVal;
    }


    @Override
    public void run() {
        if (scroller.computeScrollOffset()) {
            int offsetX = scroller.getOffsetX();
            int offsetY = scroller.getOffsetY();
            pdfView.onScroll(offsetX, offsetY);
            ViewCompat.postOnAnimation(pdfView, this);
        }else {
            Logger.t(TAG).log("fling finish");
        }
    }
}
