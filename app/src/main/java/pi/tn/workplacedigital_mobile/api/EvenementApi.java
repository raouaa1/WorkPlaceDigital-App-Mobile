package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import pi.tn.workplacedigital_mobile.model.Evenement;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EvenementApi {
    @GET("/api/evenements")
    Call<List<Evenement>> getAllEvenements();
}
