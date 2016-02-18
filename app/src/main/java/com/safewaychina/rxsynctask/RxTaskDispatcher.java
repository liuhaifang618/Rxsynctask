package com.safewaychina.rxsynctask;

import android.os.Process;

import java.util.concurrent.PriorityBlockingQueue;


/**
 * @author liu_haifang
 * @version 1.0
 * @Title：SAFEYE@
 * @Description：
 * @date 2015-4-15 上午9:53:25
 */
public class RxTaskDispatcher extends Thread {
    private final PriorityBlockingQueue<RxAysnTask<?>> mQueue;
    private volatile boolean mQuit = false;

    public RxTaskDispatcher(PriorityBlockingQueue<RxAysnTask<?>> queue) {
        mQueue = queue;
    }


    public void quit() {
        mQuit = true;
        interrupt();
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        RxAysnTask<?> request;
        while (true) {
            try {
                request = mQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }

            if (request.isCanceled()) {
                request.finish();
                request.onResult(true,null);
                continue;
            }

            request.publish(request.createTask());
        }
    }

}
