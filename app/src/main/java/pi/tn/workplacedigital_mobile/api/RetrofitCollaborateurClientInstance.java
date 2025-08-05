package pi.tn.workplacedigital_mobile.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client spécifique aux appels API Collaborateurs
 * avec la base URL sur le port 9098.
 */
public class RetrofitCollaborateurClientInstance {

    private static Retrofit retrofit;
    // Base URL pour accéder au backend Collaborateurs (port 9098)
    private static final String BASE_URL = "http://172.19.3.134:9098/api/";

    /**
     * Retourne une instance Retrofit singleton configurée avec BASE_URL.
     * @return Retrofit instance
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
