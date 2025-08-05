package pi.tn.workplacedigital_mobile.api;

import java.util.List;
import pi.tn.workplacedigital_mobile.model.Signalement;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SignalementApi {

    @POST("signalements")
    Call<Signalement> addSignalement(@Body Signalement signalement);

    @GET("signalements")
    Call<List<Signalement>> getAllSignalements();
}
