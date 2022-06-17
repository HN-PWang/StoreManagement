package com.mr.lib_base.network;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mr.lib_base.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * 自定义RXJAVA观察者，处理返回结果和异常信息
 * 如果data是空/或者cls是空的，K必然是String类型
 */
public abstract class SMSubscriber<K> implements Subscriber<ResponseBody> {

    /**
     * K是一个Entity而不能是List<Entity>
     */
    private Class<K> cls;

    public SMSubscriber(Class<K> cls) {
        this.cls = cls;
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            String responseString = responseBody.string();
            JSONObject jsonObject = new JSONObject(responseString);
            int code = jsonObject.getInt("code");
            String data = jsonObject.optString("data");
            String message = jsonObject.getString("message");

            if (code == NetworkConstants.RESPONSE_SUCCESS_CODE) {
                // 返回200
                if (this.cls == null) {
                    // 表示不关心返回的数据，只要知道是成功就好
                    onSuccess((K) message, null);
                } else {
                    if (!TextUtils.isEmpty(data)) {
                        if (data.startsWith("[") && data.endsWith("]")) {
                            // data是一个jsonArray
                            toParseDataIsArray(responseString);
                        } else {
                            // data是一个jsonObject
                            toParseDataIsObject(responseString);
                        }
                    } else {
                        // "data":null或者"data":""的情况
                        onSuccess((K) message, null);
                    }
                }
            } else {
                // 返回非200
                errorException(new SMException(code, message, data));
            }
        } catch (JsonSyntaxException exception) {
            errorException(new SMException(NetworkConstants.PARSER_ERROR_CODE));
        } catch (JSONException exception) {
            errorException(new SMException(NetworkConstants.SERVER_ERROR_CODE));
        } catch (Exception e) {
//            VVICLog.e("VVICSubscriber Exception: " + e.getMessage());
        }
    }

    /**
     * data是一个JSON Array的解析方式
     */
    private void toParseDataIsArray(String responseString) {
        BaseResponseIsList<K> baseResponseIsList = VVICGson.fromJson(responseString, this.cls, BaseResponseIsList.class);
        List<K> resultList = baseResponseIsList.getData();
        if (resultList != null) {
            onSuccess(null, resultList);
        } else {
            errorException(new SMException(NetworkConstants.SERVER_ERROR_CODE));
        }
    }

    /**
     * data是一个JSON Object的解析方式
     */
    private void toParseDataIsObject(String responseString) {
        BaseResponseNoList<K> baseResponseNoList = VVICGson.fromJson(responseString, this.cls, BaseResponseNoList.class);
        K result = baseResponseNoList.getData();
        if (result != null) {
            onSuccess(result, null);
        } else {
            errorException(new SMException(NetworkConstants.SERVER_ERROR_CODE));
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
//            VVICLog.i("=====响应内容(异常)=====" + e.getMessage());
        }
        if (e instanceof HttpException) {
            // 非2XX响应码的异常，例如404
            errorException(new SMException(NetworkConstants.SERVER_ERROR_CODE,
                    SMException.getErrorMsgFromLocalCode(NetworkConstants.SERVER_ERROR_CODE), "",
                    ((HttpException) e).code(), ((HttpException) e).message()));
        } else if (e instanceof IOException) {
            // 连接超时、主机地址找不到等异常
            errorException(new SMException(NetworkConstants.TIMEOUT_ERROR_CODE));
        } else if (e instanceof JsonParseException || e instanceof JSONException) {
            // JSON解析异常
            errorException(new SMException(NetworkConstants.PARSER_ERROR_CODE));
        } else if (e instanceof SMException) {
            // VVIC自定义异常
            SMException exception = (SMException) e;
            errorException(exception);
        } else {
            // 其他异常
            errorException(new SMException(NetworkConstants.SERVER_ERROR_CODE));
        }
    }

    private void errorException(SMException exception) {
        Context context = BaseApplication.getInstance().getApplicationContext();
        if (!NetworkUtils.checkNetwork(context)) {
            // 网络错误
            onError(new SMException(NetworkConstants.NETWORK_ERROR_CODE));
        } else {
            onError(exception);
        }
    }

    public abstract void onError(SMException exception);

    public abstract void onSuccess(K k, List<K> list);

}
