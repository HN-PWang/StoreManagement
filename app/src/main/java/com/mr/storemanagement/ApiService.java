package com.mr.storemanagement;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/Mobile/GetLogin")
    Observable<ResponseBody> login(@HeaderMap Map<String, String> header, @Query("w") String w);

}
