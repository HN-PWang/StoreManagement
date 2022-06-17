package com.mr.lib_base.network;

import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.network.listener.NetLoadingListener;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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

    private RxAppCompatActivity mBaseActivity;

    private NetLoadingListener mNetLoadingListener;

    private NetResultListener mNetResultListener;


    protected BaseModel mBaseModel;


    public BasePresenter(RxAppCompatActivity baseActivity, NetResultListener resultListener, NetLoadingListener loadingListener) {
        mBaseActivity = baseActivity;
        this.mNetResultListener = resultListener;
        this.mNetLoadingListener = loadingListener;
    }

    /**
     * 执行请求操作
     */
    public Subscription executeRequest() {
        Class<T> cls = getEntityClass();
        return getRequestWithoutServiceTimeObservable().subscribe(new SMSubscriber<T>(cls) {
            @Override
            public void onError(SMException exception) {
                finishLoading();
                if (mNetResultListener != null) {
                    mNetResultListener.loadFailure(exception);
                }
                //统一code处理 在BaseActivity可以接收
                if (RetrofitClient.get().getAuthCallback() != null && mBaseActivity instanceof BaseActivity) {
                    RetrofitClient.get().getAuthCallback().onAuthResult(exception.getErrorCode(), exception.getErrorMsg());
                }
            }

            @Override
            public void onSuccess(T t, List<T> list) {
                finishLoading();
                if (mNetResultListener != null) {
                    if (t != null) {
                        mNetResultListener.loadSuccess(t);
                    } else {
                        mNetResultListener.loadSuccess(list);
                    }
                }
            }
        });
    }

    /**
     * 新的请求方式
     * 无需请求服务器时间
     * 直接去请求对应API接口
     */
    private Observable<ResponseBody> getRequestWithoutServiceTimeObservable() {
        if (mBaseModel == null) {
            throw new NullPointerException("Model can not be empty!");
        }
        Observable<ResponseBody> o = toPerformApi()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        startLoading();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        if (mBaseActivity != null) {
            return o.compose(mBaseActivity.<ResponseBody>bindUntilEvent(ActivityEvent.DESTROY));
        } else {
            return o;
        }
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
