package pi.tn.workplacedigital_mobile.api;

import org.json.JSONObject;

import pi.tn.workplacedigital_mobile.model.ChatResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatbotApi {

    @Headers("Content-Type: application/json")
    @POST("chat")
    Call<ChatResponse> sendMessage(@Body JSONObject message);
}