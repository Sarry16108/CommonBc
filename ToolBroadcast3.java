package com.example.administrator.testall.selfbroadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;

import com.example.administrator.testall.MyApplication;

import java.util.Map;

/**
 * Created by Yanghj on 2017/1/13.
 * 全局使用broadcast类
 * ToolBroadcast结构是<action, <activity, callback>>,所有相同的action放在同一个元素中，取时候方便，但是添加过程麻烦，不推荐。
 * 以ToolBroadcast相比，写法比较简便易懂，也没有很繁杂的过程。
 */

public enum  ToolBroadcast3 {
    INSTANCE;

    public static ToolBroadcast3 getInstance() {
        return INSTANCE;
    }

    private Map<BcReceiver, IntentFilter>  mReceivers = new ArrayMap<>();
    private BroadcastReceiver   broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            for (BcReceiver receiver : mReceivers.keySet()) {
                if (null != receiver && mReceivers.get(receiver).matchAction(action)) {
                    receiver.onReceiver(context, intent);
                }
            }
        }
    };


    private Activity getActivity(BcReceiver receiver) {
        if (null == receiver) {
            return null;
        }

        Activity activity = null;
        if (receiver instanceof Fragment) {
            return ((Fragment) receiver).getActivity();
        } else if (receiver instanceof Activity) {
            return (Activity) receiver;
        } else {
            return null;
        }
    }


    public boolean register(BcReceiver receiver, String action) {
        Activity activity = getActivity(receiver);
        if (null == activity) {
            return false;
        }

        //本窗口已经添加过监听，则只需要添加action
        if (mReceivers.containsKey(receiver)) {
            mReceivers.get(receiver).addAction(action);
        } else {
            mReceivers.put(receiver, new IntentFilter(action));
        }

        activity.registerReceiver(broadcastReceiver, new IntentFilter(action));
        return true;
    }

    public boolean register(BcReceiver receiver, IntentFilter filter) {
        Activity activity = getActivity(receiver);
        if (null == activity) {
            return false;
        }

        if (mReceivers.containsKey(receiver)) {
            for (int i = 0; i < filter.countActions(); ++i) {
                mReceivers.get(receiver).addAction(filter.getAction(i));
            }
        } else {
            mReceivers.put(receiver, filter);
        }

        activity.registerReceiver(broadcastReceiver, filter);
        return true;
    }

    public void unregister(BcReceiver receiver) {
        mReceivers.remove(receiver);
        Activity activity = getActivity(receiver);
        if (null == activity) {
            return;
        }

        activity.unregisterReceiver(broadcastReceiver);
    }

    //发送广播
    public boolean sendBroadcast(BcReceiver receiver, String action) {
        Activity activity = getActivity(receiver);
        if (null == activity) {
            return false;
        }

        activity.sendBroadcast(new Intent(action));
        return true;
    }

    public boolean sendRegister(BcReceiver receiver, String action, String key, String value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        return sendBroadcast(receiver, intent);
    }

    public boolean sendBroadcast(BcReceiver receiver, Intent intent) {
        Activity activity = getActivity(receiver);
        if (null == activity) {
            return false;
        }

        activity.sendBroadcast(intent);
        return true;
    }
}
