package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import pi.tn.workplacedigital_mobile.model.Activite;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ActiviteApi {
    @GET("activites")
    Call<List<Activite>> getAllActivites();
}
