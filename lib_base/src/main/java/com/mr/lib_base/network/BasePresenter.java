package com.mr.lib_base.network;

import com.google.gson.Gson;
import com.mr.lib_base.base.BaseActivity;
import com.mr.lib_base.network.listener.NetLoadingListener;
import com.mr.lib_base.network.listener.NetResultListener;

import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Presenter的基类，定义了一些基本的调度方法
 * 一、getEntityClass等于null，表示不关注返回的数据，对应的T肯定是String
 * 二、getEntityClass不等于null，表示关注返回的数据，
 * 1、data返回的是Entity，那么T也是Entity
 * 2、data返回的是List<Entity>，那么T也是Entity
 * 3、data返回的是空的，那么T必然也是String
 */
public abstract class BasePresenter<T> {

    private BaseActivity mBaseActivity;

    private NetLoadingListener mNetLoadingListener;

    private NetResultListener mNetResultListener;

    protected BaseModel mBaseModel;

    public BasePresenter(BaseActivity baseActivity, NetResultListener resultListener
            , NetLoadingListener loadingListener) {
        mBaseActivity = baseActivity;
        mNetResultListener = resultListener;
        mNetLoadingListener = loadingListener;
    }

    /**
     * 执行请求操作
     */
    public void executeRequest() {
        Class<T> cls = getEntityClass();
        getRequestObservable().subscribe(new VVICSubscriber<T>(cls) {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                startLoading();
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(SMException exception) {
                finishLoading();
                if (mNetResultListener != null) {
                    mNetResultListener.loadFailure(exception);
                }
            }

            @Override
            public void onSuccess(T t) {
                finishLoading();
                if (mNetResultListener != null) {
                    mNetResultListener.loadSuccess(t);
                }
            }
        });
    }

    /**
     * 新的请求方式
     * 无需请求服务器时间
     * 直接去请求对应API接口
     */
    private Observable<ResponseBody> getRequestObservable() {
        if (mBaseModel == null) {
            throw new NullPointerException("Model can not be empty!");
        }
        Observable<ResponseBody> o = toPerformApi()
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
//        if (mBaseActivity != null) {
//            return o.compose(mBaseActivity.<ResponseBody>bindUntilEvent(ActivityEvent.DESTROY));
//        } else {
//            return o;
//        }
        return o;
    }

    /**
     * 回调给界面层，要开始调用接口了。用于加载对话框，如果不需要关心加载对话框可传null
     */
    private void startLoading() {
        if (mNetLoadingListener != null) {
            mNetLoadingListener.startLoading();
        }
    }

    /**
     * 调用接口完成，回调给界面层处理
     */
    protected void finishLoading() {
        if (mNetLoadingListener != null) {
            mNetLoadingListener.finishLoading();
        }
    }

    /**
     * 需要重写的抽象方法，用于调用Model层API
     */
    protected abstract Observable<ResponseBody> toPerformApi();

    /**
     * 需要重写的抽象方法，用于获取Class<T>
     */
    protected abstract Class<T> getEntityClass();

}
