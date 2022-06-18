package com.mr.lib_base.network.listener;

import com.mr.lib_base.network.SMException;

/**
 * view基接口
 */
public interface NetResultListener<T> {

    void loadSuccess(T t);

    void loadFailure(SMException exception);

}
