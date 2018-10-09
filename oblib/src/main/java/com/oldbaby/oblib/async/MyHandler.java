package com.oldbaby.oblib.async;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.oldbaby.oblib.util.MLog;

/**
 * use this handler when you want to override handler's handleMessage method.
 */
public class MyHandler extends Handler {

    private static final String TAG = "myobserver";
    private static final int DEFAULT_HANDLE = 987789;

    /**
     * 一个消息类型对应的一种操作
     */
    public static interface HandlerListener {
        boolean handleMessage(Message msg);
    }

    private SparseArray<HandlerListener> handlables;

    public MyHandler(HandlerListener resHandler) {
        handlables = new SparseArray<HandlerListener>();
        handlables.put(DEFAULT_HANDLE, resHandler);
    }

    /**
     * @param key       message's what
     * @param handlable
     */
    public void addHandle(int key, HandlerListener handlable) {
        handlables.put(key, handlable);
        MLog.d(TAG, String.format("add %d and %s", key, handlables.get(key)
                .toString()));
    }

    @Override
    public void handleMessage(Message msg) {
        if (handlables != null) {
            HandlerListener handlable = handlables.get(msg.what);
            if (handlable == null) {
                handlable = handlables.get(DEFAULT_HANDLE);
            }
            if (handlable != null) {
                handlable.handleMessage(msg);
                MLog.d(TAG, handlable.toString() + " handling msg");
                return;
            }
        }
        super.handleMessage(msg);
    }
}