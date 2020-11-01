package com.hacknife.pdfviewer.loader;

import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingLimitQueue<E> extends PriorityBlockingQueue<E>   {
    public static final String TAG = PriorityBlockingLimitQueue.class.getSimpleName();
    private final int limit ;

    public PriorityBlockingLimitQueue(int limit) {
        this.limit = limit;
    }
}
