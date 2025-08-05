package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pi.tn.workplacedigital_mobile.model.UserProfile;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserProfileApiService {

    @GET("/api/user-profiles/{id}")
    Call<UserProfile> getUser(@Path("id") Long id);

    @Multipart
    @PUT("/api/user-profiles/{id}/update-profile")
    Call<UserProfile> updateUser(
            @Path("id") Long id,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("bio") RequestBody bio,
            @Part MultipartBody.Part image
    );

    @DELETE("/api/user-profiles/{id}")
    Call<Void> deleteUser(@Path("id") Long id);
}
