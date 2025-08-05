package pi.tn.workplacedigital_mobile.api;

import java.util.List;
import java.util.Map;

import pi.tn.workplacedigital_mobile.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<User> login(@Body User user);

    @POST("auth/register")
    Call<User> register(@Body User user);

    @GET("users/all")
    Call<List<User>> getAllUsers();

    @GET("users/{userId}/amis")
    Call<List<User>> getAmisByUserId(@Path("userId") long userId);

    @POST("users/{userId}/amis/{amiId}")
    Call<Void> addAmi(@Path("userId") long userId, @Path("amiId") long amiId);

    @DELETE("users/{userId}/amis/{amiId}")
    Call<Void> removeAmi(@Path("userId") long userId, @Path("amiId") long amiId);

    @GET("users/{userId}/all-with-ami-status")
    Call<List<User>> getAllUsersWithAmiStatus(@Path("userId") long userId);

    @POST("/api/auth/request-reset")
    Call<Map<String, String>> requestPasswordReset(@Body Map<String, String> body);

    @POST("/api/auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body Map<String, String> body);


}
