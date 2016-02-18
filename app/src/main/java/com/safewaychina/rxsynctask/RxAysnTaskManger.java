package com.safewaychina.rxsynctask;

/**
 * @author liu_haifang
 * @version 1.0
 * @Title：SAFEYE@
 * @Description：
 * @date 2015-09-14
 */
public class RxAysnTaskManger {

    private static RxAysnTaskManger instance = new RxAysnTaskManger();

    private RxTaskRequestStack rxTaskRequestStack;

    private RxAysnTaskManger() {
        rxTaskRequestStack = RxTaskStarter.newRequestQueue();
        rxTaskRequestStack.start();
    }

    public static RxAysnTaskManger getInstance() {
        return instance;
    }

    public void publishTask(RxAysnTask rxAysnTask) {
        rxTaskRequestStack.add(rxAysnTask);
    }

    public void publishTask(String tag, RxAysnTask rxAysnTask) {
        rxAysnTask.setTag(tag);
        rxTaskRequestStack.add(rxAysnTask);
    }

    /**
     * 发布单一的任务
     *
     * @param rxAysnTask
     */
    public void publishSingleTask(String tag, RxAysnTask rxAysnTask) {
        rxAysnTask.setTag(tag);
        String newTag = tag == null ? rxAysnTask.getTag() : tag;
        RxAysnTask task = rxTaskRequestStack.findTaskByTag(newTag);
        if (task == null || (task != null && task.isFinsh())) {
            rxTaskRequestStack.add(rxAysnTask);
        }
    }

    /**
     * 取消单个任务
     *
     * @param rxAysnTask
     */
    public void cancleAllTask(final RxAysnTask rxAysnTask) {
        rxTaskRequestStack.cancelAll(new RxTaskRequestStack.RequestFilter() {
            @Override
            public boolean apply(RxAysnTask<?> request) {
                return request == rxAysnTask;
            }
        });
    }


    /**
     * 根据tag取消所有的任务
     *
     * @param tag
     */
    public void cancleAllTasks(String tag) {
        if (rxTaskRequestStack != null) {
            rxTaskRequestStack.cancelAll(tag);
        }
    }

    /**
     * 清除所有的异步你请求
     */
    public void reset() {
        if (rxTaskRequestStack != null) {
            rxTaskRequestStack.stop();
        }

    }


}
