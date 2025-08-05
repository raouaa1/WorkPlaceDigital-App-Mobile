package pi.tn.workplacedigital_mobile.api;

import java.util.List;

import pi.tn.workplacedigital_mobile.model.Discussion;
import pi.tn.workplacedigital_mobile.model.MessageDisc;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServiceMessage {

    // Récupérer tous les messages d'une discussion par son id
    @GET("discussions/{discussionId}/messages")
    Call<List<MessageDisc>> getMessagesByDiscussion(@Path("discussionId") Long discussionId);

    // Exemple d'endpoint REST pour récupérer la liste des discussions d'un utilisateur
    @GET("/api/discussions/user/{userId}")
    Call<List<Discussion>> getDiscussionList(@Path("userId") Long userId);

    @POST("messages")
    Call<MessageDisc> sendMessage(@Body MessageDisc message);




}
