package pi.tn.workplacedigital_mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.api.ApiClient;
import pi.tn.workplacedigital_mobile.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private EditText emailField;
    private Button sendLinkBtn;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = findViewById(R.id.email_field);
        sendLinkBtn = findViewById(R.id.send_link_btn);

        apiService = ApiClient.getClient().create(ApiService.class);

        sendLinkBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();

            // Validation email @ooredoo.tn
            if (email.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.endsWith("@gmail.com")) {    //@ooredoo.tn
                Toast.makeText(this, "L'email doit se terminer par @ooredoo.tn", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("email", email);

            Call<Map<String, String>> call = apiService.requestPasswordReset(body);
            call.enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ForgotPasswordActivity.this, "Lien envoyé par mail", Toast.LENGTH_LONG).show();

                        // Affiche le lien dans la console Logcat
                        String resetLink = response.body().get("resetLink");
                        Log.d(TAG, "Lien de réinitialisation: " + resetLink);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Email introuvable", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
