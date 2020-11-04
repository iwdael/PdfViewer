package com.hacknife.pdfviewer.loader;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.cache.PageCache;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.listener.OnThumbnailListener;
import com.hacknife.pdfviewer.model.PDF;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.model.SizeF;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThumbnailPool {
    public static final String TAG = ThumbnailPool.class.getSimpleName();
    public static final int CORE_POOL_SIZE = 1;
    public static final int MAX_POOL_SIZE = 1;
    public static final int KEEP_ALIVE_TIME = 10;
    public static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private ThreadPoolExecutor executor;
    private final Configurator configurator;
    private final PageCache pageCache;

    public ThumbnailPool(Configurator configurator) {
        this.configurator = configurator;
        this.pageCache = configurator.pageCache();
    }

    public void launch() {
        this.executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new PriorityBlockingLimitQueue<Runnable>(configurator.thumbnailPatchCount() * 4), new RejectedHandler(configurator.thumbnailPatchCount() * 4));
    }

    public void push(Runnable task) {
        executor.execute(task);
    }


    public static class RejectedHandler implements RejectedExecutionHandler {
        private final int limit;

        public RejectedHandler(int limit) {
            this.limit = limit;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.getQueue().size() > limit) {
                Runnable old = executor.getQueue().poll();
                executor.execute(r);
//                Logger.t(TAG).log("丟棄老任務:" + old.toString());
            } else {
//                Logger.t(TAG).log("任务被拒绝:" + r.toString());
            }
        }
    }

    public static class PriorityBlockingLimitQueue<E> extends PriorityBlockingQueue<E> {
        public static final String TAG = PriorityBlockingLimitQueue.class.getSimpleName();
        private final int limit;

        public PriorityBlockingLimitQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean offer(E e) {
            if (size() > limit) {
                return false;
            } else if (contains(e)) {
                return false;
            } else {
                return super.offer(e);
            }
        }
    }


}
