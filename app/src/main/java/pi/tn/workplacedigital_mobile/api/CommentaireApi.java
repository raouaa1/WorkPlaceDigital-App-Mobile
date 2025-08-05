package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import pi.tn.workplacedigital_mobile.model.Commentaire;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentaireApi {
    @GET("commentaires/publication/{publicationId}")
    Call<List<Commentaire>> getCommentairesParPublication(@Path("publicationId") int publicationId);

    @POST("commentaires")
    Call<Commentaire> ajouterCommentaire(@Body Commentaire commentaire);

    @PUT("commentaires/{id}")
    Call<Commentaire> modifierCommentaire(@Path("id") int id, @Body Commentaire commentaire);

    @DELETE("commentaires/{id}")
    Call<Void> supprimerCommentaire(@Path("id") int id);
}
