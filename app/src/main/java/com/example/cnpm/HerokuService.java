package com.example.cnpm;

import com.example.cnpm.model.ResultLogin;
import com.example.cnpm.model.ResultLogout;
import com.example.cnpm.model.ResultUpload;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface HerokuService {
    @GET("api/v1/logout")
    Call<ResultLogout> logout(@Header("Accesstoken") String accesstoken);

    @POST("api/v1/signup")
    Call<ResultLogout> signup(@Body Register register);

    @POST("api/v1/login")
    Call<ResultLogin> login(@Body User user);

    @PUT("api/v1/account")
    Call<ResultLogout> update(@Header("Accesstoken") String accesstoken,@Body Register register);

    @Multipart
    @POST("api/v1/image")
    Call<ResultUpload> uploadImage(@Header("Accesstoken") String accessToken,
                                   @Header("File-Name") String fileName,
                                   @Part MultipartBody.Part image);
    @Multipart
    @POST("api/v1/image/enhancement")
    Call<ResponseBody> enhanceImage(@Header("Accesstoken") String accessToken,
                                    @Header("File-Name") String fileName,
                                    @Header("Style") String style,
                                    @Part MultipartBody.Part image);
    @GET("api/v1/image")
    Call<ResponseBody> downloadImage(@Header("Accesstoken") String accessToken,
                                     @Header("File-Name") String fileName);
}
