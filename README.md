# Rxsynctask

#### 使用Rxbus，采用volley设计思路实现的异步操作库。防止使用Asyntask 产生的内存泄露。

#Usage

```java
        protected void showInitRefresh() {
        RxAysnTaskManger rxAysnTaskManger = RxAysnTaskManger.getInstance();

        rxAysnTaskManger.publishTask(new RxAysnTask<List<CheckTask>>() {
            @Override
            public List<CheckTask> runInBackground(Object... objs) {
                CheckTaskBiz checkTaskBiz = new CheckTaskBizImpl();
                return checkTaskBiz.queryExpireCheckTasks(ApplicationRef.getLoginInfo().getId());
            }

            @Override
            public void onResult(boolean iscancle, List<CheckTask> checkTasks) {
                expireTaskAdapter.replaceAll(checkTasks);
                refreshFrameLayout.refreshComplete();
            }
        });
    }
```

 



#Compatibility
  
  * Android GINGERBREAD 2.3+
  
# 历史记录


### Version: 1.0

  * 初始化编译


## License

    Copyright 2015, liuhaifang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

