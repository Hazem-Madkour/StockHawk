package com.udacity.stockhawk.interfaces;

public abstract class ILoadHandler<T> {

    public String symbol;

    public void onInitialize(String symbol) {
        this.symbol = symbol;
    }

    public abstract void onSuccess(T loadedObject);

    public abstract void onFail(String errorMessage);

    public void onFinish() {

    }
}
