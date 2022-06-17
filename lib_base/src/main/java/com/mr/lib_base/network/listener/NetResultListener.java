package com.mr.lib_base.network.listener;

/**
 * view基接口
 */
public interface NetResultListener<T> {

    void loadSuccess(T t);

    void loadFailure(SMException exception);

}
