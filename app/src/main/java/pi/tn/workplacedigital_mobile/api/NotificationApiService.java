package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import pi.tn.workplacedigital_mobile.model.Notification;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface NotificationApiService {

    // GET all notifications
    @GET("notifications/all")
    Call<List<Notification>> getAllNotifications();

    // POST add a notification
    @POST("notifications/add")
    Call<Notification> addNotification(@Body Notification notification);

    // PUT mark as read
    @PUT("notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") Long id);

    // DELETE a notification
    @DELETE("notifications/{id}")
    Call<Void> deleteNotification(@Path("id") Long id);
}
