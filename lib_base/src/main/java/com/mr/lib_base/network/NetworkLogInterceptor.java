package com.mr.lib_base.network;

import android.text.TextUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 拦截OKHttp的请求信息
 */
public class NetworkLogInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        if ("GET".equalsIgnoreCase(request.method())) {
//            VVICLog.i("*****GET请求*****" + url);
        } else if ("POST".equalsIgnoreCase(request.method())) {
            StringBuilder paramSB = new StringBuilder();
            String headerStr = getRequestHeaderString(request);
            if (!TextUtils.isEmpty(headerStr)) {
                paramSB.append(headerStr);
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                // 有请求参数
                paramSB.append("\n");
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                if (charset != null) {
                    String paramStr = buffer.readString(charset);
                    String[] params = paramStr.split("&");
                    for (String param : params) {
                        paramSB.append("【");
                        paramSB.append(param);
                        paramSB.append("】");
                    }
                }
            }
//            VVICLog.i("*****POST请求*****" + url + "\n" + paramSB.toString());
        }

        // 响应内容处理
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        MediaType contentType = responseBody.contentType();
        String responseStr = responseBody.string();
        String responseSB = "（状态码" +
                response.code() +
                "）（响应地址" +
                response.request().url().encodedPath() +
                "）响应内容===>>>" +
                responseStr;
//        VVICLog.i(responseSB);
        ResponseBody newBody = ResponseBody.create(contentType, responseStr);
        return response.newBuilder().body(newBody).build();
    }

    /**
     * 获取请求内容的头部信息
     */
    private String getRequestHeaderString(Request request) {
        StringBuilder headerSB = new StringBuilder();
        Headers headers = request.headers();
        if (headers != null && headers.size() > 0) {
            int count = headers.size();
            for (int i = 0; i < count; i++) {
                String name = headers.name(i);
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    headerSB.append("【");
                    headerSB.append(name);
                    headerSB.append("=");
                    headerSB.append(headers.value(i));
                    headerSB.append("】");
                }
            }
        }
        return headerSB.toString();
    }

}
