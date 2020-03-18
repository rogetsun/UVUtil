package com.uv.utils.threadpool;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by uv2sun on 2017/5/17.
 * 自定义线程池
 * 普通线程池当核心线程满了, 会先进队列, 队列满了才加线程数, 直到线程数达到max-pool-size.
 * 本线程池当核心线程满了, 会先加线程执行, 直到达到max-pool-size, 才进队列.
 */
public class UvExecutor extends ThreadPoolExecutor {
    //默认尝试次数
    private static final int DEFAULT_TRY_EXEC_TIMES = 100;
    //默认尝试时间间隔
    private static final int DEFAULT_TRY_DURATION = 1000;
    //默认空闲线程存活时长
    private static final int DEFAULT_KEEP_ALIVE_TIME = 120;

    /**
     * 尝试加入执行队列执行，如果报异常执行队列满，则每隔 tryDuration毫秒再执行一次，执行 tryExecTimes次。
     */
    private int tryExecTimes;

    /**
     * 尝试加入执行队列执行，如果报异常执行队列满，则每隔 tryDuration毫秒再执行一次，执行 tryExecTimes次。
     */
    private int tryDuration;
    /**
     * 定义一个成员变量，用于记录当前线程池中已提交的任务数量
     */
    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // ThreadPoolExecutor的勾子方法，在task执行完后需要将池中已提交的任务数 - 1
        submittedTaskCount.decrementAndGet();
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        // do not increment in method beforeExecute!
        // 将池中已提交的任务数 + 1
        submittedTaskCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            // retry to offer the task into queue.
            final TaskQueue queue = (TaskQueue) super.getQueue();
            try {
                boolean offerOk = false;
                for (int i = 0; i < this.getTryExecTimes(); i++) {
                    offerOk = queue.retryOffer(command, this.getTryDuration(), TimeUnit.MILLISECONDS);
                    if (offerOk) {
                        break;
                    }
                }
                if (!offerOk) {
                    submittedTaskCount.decrementAndGet();
                    //todo 不会被下面的Throwable截获到么?
                    throw new RejectedExecutionException("Queue capacity is full.", rx);
                }
            } catch (InterruptedException x) {
                submittedTaskCount.decrementAndGet();
                throw new RejectedExecutionException(x);
            }
        } catch (Throwable t) {
            // decrease any way
            submittedTaskCount.decrementAndGet();
            throw t;
        }
    }

    public int getSubmittedTaskCount() {
        return submittedTaskCount.get();
    }


    public UvExecutor(int corePoolSize, int maximumPoolSize, int queueSize) {
        super(corePoolSize, maximumPoolSize, UvExecutor.DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new TaskQueue<>(queueSize));
        ((TaskQueue) this.getQueue()).setExecutor(this);
    }

    public UvExecutor(int corePoolSize, int maximumPoolSize, int queueSize, String threadPoolName) {
        super(corePoolSize, maximumPoolSize, UvExecutor.DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new TaskQueue<>(queueSize), new ThreadFactory(threadPoolName));
        ((TaskQueue) this.getQueue()).setExecutor(this);
    }


    public UvExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, int queueSize) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new TaskQueue<>(queueSize));
        ((TaskQueue) this.getQueue()).setExecutor(this);
    }

    public UvExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, int queueSize, String threadPoolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new TaskQueue<>(queueSize), new ThreadFactory(threadPoolName));
        ((TaskQueue) this.getQueue()).setExecutor(this);
    }


    public UvExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, TaskQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        workQueue.setExecutor(this);
    }

    public UvExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, TaskQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        workQueue.setExecutor(this);
    }

    public UvExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, TaskQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        workQueue.setExecutor(this);
    }

    public UvExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, TaskQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        workQueue.setExecutor(this);
    }


    public int getTryExecTimes() {
        return this.tryExecTimes == 0 ? UvExecutor.DEFAULT_TRY_EXEC_TIMES : this.tryExecTimes;
    }

    /**
     * 尝试加入执行队列执行，如果报异常执行队列满，则每隔 tryDuration 毫秒再执行一次，执行 tryExecTimes次。
     */
    public void setTryExecTimes(int tryExecTimes) {
        this.tryExecTimes = tryExecTimes;
    }

    public int getTryDuration() {
        return this.tryDuration == 0 ? UvExecutor.DEFAULT_TRY_DURATION : this.tryDuration;
    }

    /**
     * 尝试加入执行队列执行，如果报异常执行队列满，则每隔 tryDuration毫秒再执行一次，执行 tryExecTimes次。
     */
    public void setTryDuration(int tryDuration) {
        this.tryDuration = tryDuration;
    }

//    public static void main(String[] args) throws InterruptedException {
//        UvExecutor executor = new UvExecutor(2, 4, 2);
//        System.out.println(executor);
//        for (int i = 0; i < 10; i++) {
//            UvRun r = new UvRun();
//            r.setIdx(i);
//            executor.execute(r);
//            System.out.println(executor.getSubmittedTaskCount() + "\t" + executor);
//        }
//        for (int i = 0; i < 20; i++) {
//            TimeUnit.SECONDS.sleep(1);
//            System.out.println(executor.getSubmittedTaskCount() + "\t" + executor);
//
//        }
//        System.out.println(executor.getSubmittedTaskCount() + "\t" + executor);
//        executor.shutdown();
//    }
//
//    private static class UvRun implements Runnable {
//        private int idx;
//
//        @Override
//        public void run() {
//            System.out.println(idx + ": begin");
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (InterruptedException e) {
//
//            }
//            System.out.println(idx + ": end");
//        }
//
//        public int getIdx() {
//            return idx;
//        }
//
//        public void setIdx(int idx) {
//            this.idx = idx;
//        }
//
//        @Override
//        public String toString() {
//            return "UvRun{" +
//                    "idx=" + idx +
//                    '}';
//        }
//    }
}
