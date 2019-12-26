package com.shresthagaurav.taskmanager.api;

import com.shresthagaurav.taskmanager.model.ImageModel;
import com.shresthagaurav.taskmanager.model.User_model;



import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {
    @POST("users/signup")
    Call<Void> register(@Body User_model cud);
    @Multipart
    @POST("upload")
    Call<ImageModel> uploadImage(@Part MultipartBody.Part imageFile);
}
