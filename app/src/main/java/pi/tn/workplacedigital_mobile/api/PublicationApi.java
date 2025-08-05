package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pi.tn.workplacedigital_mobile.models.Publication;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PublicationApi {

    /**
     * Récupérer toutes les publications
     * GET /publications
     */
    @GET("publications")
    Call<List<Publication>> getAllPublications();

    /**
     * Créer une publication avec texte + image et/ou vidéo
     * POST /publications/with-media
     * @param contenu texte de la publication
     * @param auteurId id de l'auteur
     * @param image fichier image (optionnel, peut être null)
     * @param video fichier vidéo (optionnel, peut être null)
     */
    @Multipart
    @POST("publications/with-media")
    Call<ResponseBody> createPublicationWithMedia(
            @Part("contenu") RequestBody contenu,
            @Part("auteurId") RequestBody auteurId,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video
    );


    /**
     * Aimer une publication
     * PUT /publications/{id}/like?userId=xxx
     * @param publicationId id de la publication à liker
     * @param userId id de l'utilisateur qui aime
     */
    @PUT("publications/{id}/like")
    Call<Void> likePublication(@Path("id") Long publicationId, @Query("userId") Long userId);

    /**
     * Supprimer une publication
     * DELETE /publications/{id}
     * @param id id de la publication à supprimer
     */
    @DELETE("publications/{id}")
    Call<Void> deletePublication(@Path("id") Long id);

    /**
     * Modifier une publication (texte et/ou média)
     * PUT /publications/{id}/update-content
     * @param id id de la publication
     * @param contenu nouveau texte
     * @param image fichier image (optionnel, peut être null)
     * @param video fichier vidéo (optionnel, peut être null)
     */
    @Multipart
    @PUT("publications/{id}/update-content")
    Call<ResponseBody> updatePublication(
            @Path("id") Long id,
            @Part("contenu") RequestBody contenu,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video
    );
}
