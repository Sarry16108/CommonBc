package com.example.administrator.testall.selfbroadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;


import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by yanghj on 2016/11/25.
 */

public class ToolBroadcast extends BroadcastReceiver {
    private final String TAG = "ToolBroadcast";

    private static ToolBroadcast    mToolBroadcast;
    //<action, <activity, callback>>，activity不能是class name，否则遇到同一个页面start两次的就会出现覆盖现象。
    private Map<String, LinkedHashMap<Integer, BcReceiver>> mActivityList = new ArrayMap<>();


    public static ToolBroadcast getInstance() {
        if (null == mToolBroadcast) {
            synchronized (ToolBroadcast.class) {
                if (null == mToolBroadcast) {
                    mToolBroadcast = new ToolBroadcast();
                }
            }
        }

        return mToolBroadcast;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Map<Integer, BcReceiver> receivers = mActivityList.get(action);
        for (BcReceiver receiver : receivers.values()) {
            receiver.onReceiver(context, intent);
        }
    }

    //批量添加广播
    public void registerBroadcast(BcReceiver broadcastReceiver, IntentFilter filter) {
        if (null == broadcastReceiver || null == filter) {
            Log.e(TAG, "sendBroadcast" + " parameters contain error");
            return;
        }

        for (int i = 0; i < filter.countActions(); ++i) {
            registerBroadcast(broadcastReceiver, filter.getAction(i));
        }
    }

    //添加注册
    public void registerBroadcast(BcReceiver broadcastReceiver, String action) {
        if (null == broadcastReceiver || null == action) {
            Log.e(TAG, "sendBroadcast" + " parameters contain error");
            return;
        }

        //同一个action多次注册，onReceive会多次调用
        if (!mActivityList.containsKey(action)) {
            IntentFilter intentFilter = new IntentFilter(action);
            if (broadcastReceiver instanceof FragmentActivity) {
                ((Activity)broadcastReceiver).registerReceiver(this, intentFilter);
            } else if (broadcastReceiver instanceof Fragment) {
                Activity activity = ((Fragment)broadcastReceiver).getActivity();
                if (null != activity) {
                    activity.registerReceiver(this, intentFilter);
                } else {
                    return;
                }
            }
        }

        LinkedHashMap<Integer, BcReceiver> map = mActivityList.get(action);
        if (null == map) {
            map = new LinkedHashMap<>(3);
        }
        map.put(broadcastReceiver.hashCode(), broadcastReceiver);
        mActivityList.put(action, map);
    }


    //批量解注册
    public void unRegisterBroacast(BcReceiver broadcastReceiver, IntentFilter filter) {
        if (null == broadcastReceiver || null == filter) {
            Log.e(TAG, "sendBroadcast" + " parameters contain error");
            return;
        }

        for (int i = 0; i < filter.countActions(); ++i) {
            unRegisterBroacast(broadcastReceiver, filter.getAction(i));
        }
    }

    //解注册
    public void unRegisterBroacast(BcReceiver broadcastReceiver, String action) {
        if (null == broadcastReceiver || null == action) {
            Log.e(TAG, "sendBroadcast" + " parameters contain error");
            return;
        }

        Map<Integer, BcReceiver> receivers = mActivityList.get(action);
        if (null == receivers) {
            return;
        }

        if (1 == receivers.size()) {
            mActivityList.remove(action);
            if (broadcastReceiver instanceof FragmentActivity) {
                ((Activity)broadcastReceiver).unregisterReceiver(this);
            } else if (broadcastReceiver instanceof Fragment) {
                Activity activity = ((Fragment)broadcastReceiver).getActivity();
                if (null != activity) {
                    activity.unregisterReceiver(this);
                } else {
                    return;
                }
            }
        } else {
            String name = broadcastReceiver.getClass().getSimpleName();
            receivers.remove(name);
        }
    }

    //发送广播消息
    public void sendBroadcast(Activity activity, String action, Intent intent) {
        if (null == activity || null == intent) {
            Log.e(TAG, "sendBroadcast" + " parameters contain error");
            return;
        }
        intent.setAction(action);
        activity.sendBroadcast(intent);
    }


    //发送广播消息，intent包含action
    public void sendBroadcast(Activity activity, Intent intent) {
        if (null == activity || null == intent) {
            Log.e(TAG, "sendBroadcast" + " parameters contain error");
            return;
        }

        if (TextUtils.isEmpty(intent.getAction())) {
            Log.e(TAG, "sendBroadcast" + " intent doesn't contains action");
            return;
        }
        activity.sendBroadcast(intent);
    }

    public void sendBroadcast(Activity activity, String action, String key, int value) {
        Intent intent = new Intent();
        intent.putExtra(key, value);
        sendBroadcast(activity, action, intent);
    }
}
