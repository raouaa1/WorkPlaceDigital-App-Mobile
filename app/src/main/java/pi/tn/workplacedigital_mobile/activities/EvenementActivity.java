package pi.tn.workplacedigital_mobile.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.EvenementAdapter;
import pi.tn.workplacedigital_mobile.api.EvenementApi;
import pi.tn.workplacedigital_mobile.api.RetrofitClientInstance;
import pi.tn.workplacedigital_mobile.api.RetrofitEvenementClientInstance;
import pi.tn.workplacedigital_mobile.model.Evenement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EvenementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EvenementAdapter adapter;
    private List<Evenement> evenementList = new ArrayList<>();
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evenement);

        recyclerView = findViewById(R.id.recyclerViewEvenements);
        backArrow = findViewById(R.id.backArrow);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EvenementAdapter(evenementList);
        recyclerView.setAdapter(adapter);

        // Bouton retour
        backArrow.setOnClickListener(v -> onBackPressed());

        // Chargement des événements
        fetchEvenements();
    }

    private void fetchEvenements() {
        Retrofit retrofit = RetrofitEvenementClientInstance.getRetrofitInstance();
        EvenementApi api = retrofit.create(EvenementApi.class);

        api.getAllEvenements().enqueue(new Callback<List<Evenement>>() {
            @Override
            public void onResponse(Call<List<Evenement>> call, Response<List<Evenement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    evenementList.clear();
                    evenementList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EvenementActivity.this, "Erreur chargement événements", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Evenement>> call, Throwable t) {
                Toast.makeText(EvenementActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
