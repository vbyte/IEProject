package com.jiang.geo;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

public enum LocationNotifyManager {

    INSTANCE;

    private Observable mObservable;

    private LocationNotifyManager() {
        mObservable = new Observable();
    }

    public void addObserver(Observer observer) {
        mObservable.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        mObservable.deleteObserver(observer);
    }

    public void deleteObservers() {
        mObservable.deleteObservers();
    }

    public void notifyObservers() {
        mObservable.notifyObservers();
    }
}
