package com.yushi.yunbang.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.yushi.yunbang.base.YunApplication;
import com.yushi.yunbang.interfaces.BcReceiver;

import java.util.HashMap;

/**
 * Created by Yanghj on 2017/1/13.
 * 应用内发送localbroadcastmanager
 */

public enum ToolLocalBroadcast {
    INSTANCE;

    public static ToolLocalBroadcast getInstance() {
        return INSTANCE;
    }

    private HashMap<BcReceiver, BroadcastReceiver> mReceivers = new HashMap<>(1);
    private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(YunApplication.getContext());

	//注册广播
    public void register(final BcReceiver receiver, final String action) {
        BroadcastReceiver broadcastReceiver = null;
        if (!mReceivers.containsKey(receiver)) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    receiver.onReceiver(context, intent);
                }
            };
            mReceivers.put(receiver, broadcastReceiver);
        } else {
            broadcastReceiver = mReceivers.get(receiver);
        }

        mLocalBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(action));
    }

    public void register(final BcReceiver receiver, final IntentFilter filter) {
        BroadcastReceiver broadcastReceiver = null;
        if (!mReceivers.containsKey(receiver)) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    receiver.onReceiver(context, intent);
                }
            };
            mReceivers.put(receiver, broadcastReceiver);
        } else {
            broadcastReceiver = mReceivers.get(receiver);
        }

        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

	//移除该页面广播
    public void unregister(BcReceiver receiver) {
        if (mReceivers.containsKey(receiver)) {
            mLocalBroadcastManager.unregisterReceiver(mReceivers.get(receiver));
            mReceivers.remove(receiver);
        }
    }

    //发送广播
    public void sendBroadcast(String action) {
        this.sendRegister(action, null, null);
    }

    public void sendRegister(String action, String key, String value) {
        Intent intent = new Intent(action);
        if (null != key && null != value) {
            intent.putExtra(key, value);
        }

        this.sendBroadcast(intent);
    }

    public void sendBroadcast(Intent intent) {
        mLocalBroadcastManager.sendBroadcast(intent);
    }
}
