package com.yushi.yunbang.tools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;

import com.yun8zhaohuo.library.tools.ToolLog;
import com.yushi.yunbang.entity.CommonReceiver;

import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Created by yanghj on 2016/11/25.
 */

public class ToolBroadcast extends BroadcastReceiver {
    private final String TAG = "ToolBroadcast";

    private static ToolBroadcast    mToolBroadcast;
    private Map<String, LinkedHashMap<String, CommonReceiver>> mActivityList = new ArrayMap<>();


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
        Map<String, CommonReceiver> receivers = mActivityList.get(action);
        for (CommonReceiver receiver : receivers.values()) {
            receiver.onReceiver(intent);
        }
    }

    //添加注册
    public void registerBroadcast(CommonReceiver broadcastReceiver, String action) {
        if (null == broadcastReceiver || null == action) {
            ToolLog.e(TAG, "sendBroadcast", "parameters contain error");
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

        LinkedHashMap<String, CommonReceiver> map = mActivityList.get(action);
        if (null == map) {
            map = new LinkedHashMap<>(3);
        }
        map.put(broadcastReceiver.getClass().getSimpleName(), broadcastReceiver);
        mActivityList.put(action, map);
    }

    //解注册
    public void unRegisterBroacast(CommonReceiver broadcastReceiver, String action) {
        if (null == broadcastReceiver || null == action) {
            ToolLog.e(TAG, "sendBroadcast", "parameters contain error");
            return;
        }

        Map<String, CommonReceiver> receivers = mActivityList.get(action);
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
    public void sendBroadcast(Activity activity, Intent intent) {
        if (null == activity || null == intent) {
            ToolLog.e(TAG, "sendBroadcast", "parameters contain error");
            return;
        }
        activity.sendBroadcast(intent);
    }

    public void sendBroadcast(Activity activity, String key, int value) {
        Intent intent = new Intent();
        intent.putExtra(key, value);
        sendBroadcast(activity, intent);
    }
}
