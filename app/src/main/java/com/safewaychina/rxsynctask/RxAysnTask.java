package com.safewaychina.rxsynctask;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 使用rxjava使用的异步任务类，效率更高、防止内存泄露
 *
 * @author liu_haifang
 * @version 1.0
 * @Title:SAFEYE@
 * @Description:
 * @date 2015-09-11
 */
public abstract class RxAysnTask<R> implements Comparable<RxAysnTask<R>> {

    /**
     * 标记
     */
    protected String mTag;
    /**
     * 序列号
     */
    protected Integer mSequence;
    /**
     * 请求队列
     */
    protected RxTaskRequestStack mRequestQueue;
    /**
     * 请求是否取消
     */
    private boolean mRequstCache;

    /**
     * 任务是否允许缓存
     */
    private boolean mIsFinsh;


    private Observable observable;
    private Subscription subscription;
    private Object[] params;

    private R result;


    public RxAysnTask(Object... params) {
        if (params != null) {
            this.params = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                this.params[i] = params[i];
            }
        }
    }

    /**
     * 创建初步主线程
     *
     * @return
     */
    protected Observable createTask() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable call() {

                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        if (!subscriber.isUnsubscribed()) {
                            onBefore();
                            subscriber.onNext("");
                            subscriber.onCompleted();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());
            }
        }).flatMap(new Func1<String, Observable<Void>>() {
            @Override
            public Observable<Void> call(String o) {
                //Log.i("bbbc", (Thread.currentThread() == Looper.getMainLooper().getThread()) + "");
                return Observable.create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        try {
                            result = runInBackground(params);
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.newThread());
            }
        });
    }

    /**
     * 发布任务
     */
    protected void publish(Observable observable) {
        this.observable = observable;
        subscription = observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<R>() {
                    @Override
                    public void call(R r) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onError(throwable);
                        finish();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        onResult(mRequstCache, result);
                        mIsFinsh = true;
                        finish();
                    }
                });
    }


    /**
     * 任务开始前执行
     */
    public void onBefore() {

    }


    /**
     * 异步执行耗时操作
     *
     * @param objs
     * @return
     */
    public abstract R runInBackground(Object... objs);

    /**
     * 返回处理结果
     *
     * @param result
     */

    public void onResult(boolean iscancle, R result) {

    }

    /**
     * 异常捕获
     *
     * @param throwable
     */
    public void onError(Throwable throwable) {

    }


    public void setRequestQueue(RxTaskRequestStack requestQueue) {
        this.mRequestQueue = requestQueue;
    }

    /**
     * 获取异步任务的缓冲数据,会有延迟
     */
    public void cache() {
        if (mIsFinsh) {
            Log.i("aaaa", "observable == null" + (observable == null));
            publish(observable == null ? createTask() : observable.cache());
        }
    }

    /**
     * 任务取消
     */

    public void cancel() {
        mRequstCache = true;
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


    /**
     * @author liu_haifang
     * @date 2015-4-15 上午10:02:54
     */
    public void finish() {
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }
    }

    /**
     * 设置优先级
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    @Override
    public int compareTo(RxAysnTask<R> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        return left == right ?
                this.mSequence - other.mSequence :
                right.ordinal() - left.ordinal();
    }

    /**
     * 是否取消任务链
     *
     * @return
     */
    public boolean isCanceled() {
        return mRequstCache;
    }

    public void setSequence(Integer sequence) {
        this.mSequence = sequence;
    }

    public RxTaskRequestStack getRequestQueue() {
        return mRequestQueue;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String mTag) {
        this.mTag = mTag;
    }

    /**
     * 是否完成
     *
     * @return
     */
    public boolean isFinsh() {
        return mIsFinsh;
    }


}
