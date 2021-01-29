package com.example.driver;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface NotifyResidents {
    String base_url = "http://192.168.43.112:8000/notify/";
//    @GET
//    Call<NotifyResponse> getResponse(@Url String url);

    @GET("{area}")
    Call<List<NotifyResponse>> getResponse(@Path("area") String areaname);
}
