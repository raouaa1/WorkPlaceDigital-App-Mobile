package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import pi.tn.workplacedigital_mobile.model.Collaborateur;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CollaborateurApi {
    @GET("collaborateurs")
    Call<List<Collaborateur>> getAllCollaborateurs();
}
