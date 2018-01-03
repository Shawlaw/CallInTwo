package github.shawlaw.app.callintwo.utils;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Shawlaw on 2017/12/12.
 */

public class WorkerUtils {
    private ExecutorService mNormalWorker;
    private HashMap<String, ExecutorService> mSerialWorkers;

    private WorkerUtils() {
        mSerialWorkers = new HashMap<>();
    }

    public static WorkerUtils getInstance() {
        return InnerInstance.sInstance;
    }

    public Future submit(Runnable task) {
        initWorker();
        return mNormalWorker.submit(task);
    }

    public Future enqueue(Runnable task, @NonNull String queueName) {
        ExecutorService theWorker = mSerialWorkers.get(queueName);
        if (theWorker == null || theWorker.isShutdown() || theWorker.isTerminated()) {
            theWorker = new ThreadPoolExecutor(
                    1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new LoaderThreadFactory()
            );
            mSerialWorkers.put(queueName, theWorker);
        }
        return theWorker.submit(task);
    }

    private void initWorker() {
        if (mNormalWorker == null || mNormalWorker.isShutdown() || mNormalWorker.isTerminated()) {
            mNormalWorker = new ThreadPoolExecutor(
                    1, CpuUtils.getNumberOfCores() + 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new LoaderThreadFactory()
            );
        }
    }

    private static class LoaderThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        LoaderThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "worker_loader-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private static class InnerInstance {
        private static WorkerUtils sInstance = new WorkerUtils();
    }
}
