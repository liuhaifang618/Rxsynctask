package com.safewaychina.rxsynctask;


/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2015-4-15 下午3:23:46
 * @author liu_haifang
 * @version 1.0
 */
public class RxTaskStarter {
	
//	private static SearchUtil instance;
//	
//	
//	public  static SearchUtil getInstance(){
//		if (instance == null) {
//			synchronized (SearchUtil.class) {
//				if (instance == null) {
//					instance = new SearchUtil();
//				}
//			}
//		}
//		return instance;
//	}
	
	public static RxTaskRequestStack newRequestQueue() {
		RxTaskRequestStack queue = new RxTaskRequestStack();
		queue.start();
		return queue;
	}
	
}
