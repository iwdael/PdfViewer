package com.hacknife.pdfviewer.loader;

import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.listener.OnThumbnailListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThumbnailPool {
    public static final String TAG = ThumbnailPool.class.getSimpleName();
    public static final int CORE_POOL_SIZE = 4;
    public static final int MAX_POOL_SIZE = 4;
    public static final int KEEP_ALIVE_TIME = 10;
    public static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private Executor executor;

    public ThumbnailPool() {
    }

    public void launch() {
        this.executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new PriorityBlockingLimitQueue<Runnable>( 10));

    }
    public void push(PatchTask task) {
        executor.execute(task);
    }
}
