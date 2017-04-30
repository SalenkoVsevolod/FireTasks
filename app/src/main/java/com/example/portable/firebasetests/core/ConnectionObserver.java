package com.example.portable.firebasetests.core;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 27.04.2017.
 */

public class ConnectionObserver {

    private static ConnectionObserver instance;
    private ArrayList<OnConnectionStateChangingListener> mListeners;

    private ConnectionObserver() {
        mListeners = new ArrayList<>();
    }

    public static ConnectionObserver getInstance() {
        if (instance == null) {
            instance = new ConnectionObserver();
        }
        return instance;
    }

    public void notifyStateChanged(boolean online) {
        for (OnConnectionStateChangingListener listener : mListeners) {
            listener.stateChanged(online);
        }
    }

    public void subscribe(OnConnectionStateChangingListener listener) {
        mListeners.add(listener);
    }

    public void unsubscribe(OnConnectionStateChangingListener listener) {
        mListeners.remove(listener);
    }

    public interface OnConnectionStateChangingListener {
        void stateChanged(boolean online);
    }
}
