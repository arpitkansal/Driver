package com.example.driver;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FetchPathApi {
FetchActivity act = new FetchActivity();

String a = act.driver_id;
//    String base_url = "http://127.0.0.1:8000/garbage/driverpath/";
    String base_url = "http://192.168.43.112:8000/garbage/driverpath/";
    @GET("1")
    Call<List<Areas>> getAreas();

//    @GET("{driver}")
//    Call<List<Areas>> getAreas(@Path("driver") String a);

}
