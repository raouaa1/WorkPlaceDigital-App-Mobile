package pi.tn.workplacedigital_mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.api.ApiClient;
import pi.tn.workplacedigital_mobile.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordInput;
    private Button resetPasswordBtn;
    private ApiService apiService;
    private String token; // récupéré automatiquement depuis le lien

    // Regex mot de passe fort : min 8 chars, majuscule, minuscule, chiffre et symbole
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordInput = findViewById(R.id.new_password_input);
        resetPasswordBtn = findViewById(R.id.reset_password_btn);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Récupérer token depuis l'intent URI (lien cliqué)
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.getQueryParameter("token") != null) {
            token = data.getQueryParameter("token");
        } else {
            Toast.makeText(this, "Token manquant dans le lien", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        resetPasswordBtn.setOnClickListener(v -> {
            String newPassword = newPasswordInput.getText().toString().trim();

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer un nouveau mot de passe", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                Toast.makeText(this, "Mot de passe trop faible : min 8 caractères, majuscule, minuscule, chiffre et symbole", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("token", token);
            body.put("newPassword", newPassword);

            Call<Map<String, String>> call = apiService.resetPassword(body);
            call.enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Mot de passe réinitialisé avec succès !", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Token invalide ou expiré", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
