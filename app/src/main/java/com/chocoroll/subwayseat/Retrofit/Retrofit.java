package com.chocoroll.subwayseat.Retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by RA on 2015-05-12.
 */
public interface Retrofit {
    public static final String ROOT = "http://myseat.dothome.co.kr";

    @POST("/getMyLastSeat.php") // 내 자리정보를 가져옴
    public void getMyLastSeat(@Body JsonObject info, Callback<JsonObject> callback);

    @POST("/getSubwaySeat.php") // 칸의 자리정보를 가져옴
    public void getSubwaySeat(@Body JsonObject info, Callback<JsonArray> callback);

    @POST("/addSeat.php")   // 자리 추가
    public void addSeat(@Body JsonObject info, Callback<JsonObject> callback);
    @POST("/delSeat.php")    //자리 삭제
    public void delSeat(@Body JsonObject info, Callback<String> callback);

    // 질문,답변
    @POST("/getPostList.php")
    public void getPostList(@Body JsonObject info, Callback<JsonArray> callback);
    @POST("/sendPost.php")
    public void sendPost(@Body JsonObject info, Callback<String> callback);
    @POST("/getReplyList.php")
    public void getReplyList(@Body JsonObject info, Callback<JsonArray> callback);
    @POST("/sendReply.php")
    public void sendReply(@Body JsonObject info, Callback<String> callback);

}
