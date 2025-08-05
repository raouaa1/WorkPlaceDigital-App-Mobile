package pi.tn.workplacedigital_mobile.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.api.RetrofitSignalementClientInstance;
import pi.tn.workplacedigital_mobile.api.SignalementApi;
import pi.tn.workplacedigital_mobile.model.Signalement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignalementActivity extends AppCompatActivity {

    private EditText editTextMotif;
    private Button buttonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signalement);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setTitle("Signalement 🚨");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        editTextMotif = findViewById(R.id.editTextMotif);
        buttonValider = findViewById(R.id.buttonValiderSignalement);

        buttonValider.setOnClickListener(v -> {
            String motif = editTextMotif.getText().toString().trim();
            if (TextUtils.isEmpty(motif)) {
                Toast.makeText(SignalementActivity.this, "Veuillez écrire un motif", Toast.LENGTH_SHORT).show();
                return;
            }

            Signalement signalement = new Signalement();
            signalement.setMotif(motif);
            // Pas besoin de setDateSignalement : backend ajoute la date automatiquement

            SignalementApi api = RetrofitSignalementClientInstance.getRetrofitInstance().create(SignalementApi.class);
            Call<Signalement> call = api.addSignalement(signalement);
            call.enqueue(new Callback<Signalement>() {
                @Override
                public void onResponse(Call<Signalement> call, Response<Signalement> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignalementActivity.this, "Signalement envoyé avec succès", Toast.LENGTH_SHORT).show();
                        editTextMotif.setText("");
                    } else {
                        Toast.makeText(SignalementActivity.this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Signalement> call, Throwable t) {
                    Toast.makeText(SignalementActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
