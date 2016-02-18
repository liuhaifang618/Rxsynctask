package com.safewaychina.rxsynctask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liu_haifang
 * @version 1.0
 * @Title：SAFEYE@
 * @Description：
 * @date 2015-4-15 上午9:29:39
 */
public class RxTaskRequestStack {


    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;


    /**
     * 序号生成器
     */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private final PriorityBlockingQueue<RxAysnTask<?>> mRequestQueue = new PriorityBlockingQueue();

    private final Set<RxAysnTask<?>> mCurrentRequests = new HashSet();

    private RxTaskDispatcher[] mDispatchers;


    public RxTaskRequestStack() {
        this.mDispatchers = new RxTaskDispatcher[DEFAULT_NETWORK_THREAD_POOL_SIZE];
    }

    public RxTaskRequestStack(int threadPoolSize) {
        this.mDispatchers = new RxTaskDispatcher[threadPoolSize];
    }


    public void start() {
        stop();
        for (int i = 0; i < mDispatchers.length; i++) {
            RxTaskDispatcher networkDispatcher = new RxTaskDispatcher(mRequestQueue);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    public <T> RxAysnTask<T> add(RxAysnTask<T> request) {
        System.out.println("requst queue num " + mRequestQueue.size());
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
            findTaskByTag(request.getTag());
        }
        request.setRequestQueue(this);
        int number = getSequenceNumber();
        System.out.println("new request seque= " + number);
        request.setSequence(number);
        synchronized (mRequestQueue) {
            mRequestQueue.add(request);
        }

        return request;
    }


    void finish(RxAysnTask<?> request) {
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
        synchronized (mRequestQueue) {
            mRequestQueue.remove(request);
        }
    }


    public void stop() {
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
    }

    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }


    public interface RequestFilter {
        public boolean apply(RxAysnTask<?> request);
    }


    public void cancelAll(final String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(RxAysnTask<?> request) {
                return request.getTag().equals(tag);
            }
        });
    }

    protected RxAysnTask findTaskByTag(final String tag) {
        if (tag == null) {
            return null;
        }
        for (RxAysnTask<?> request : mCurrentRequests) {
            if (request.getTag().equals(tag)) {
                return request;
            }
        }
        return null;
    }


    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (RxAysnTask<?> request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.cancel();
                }
            }
        }
    }
}
