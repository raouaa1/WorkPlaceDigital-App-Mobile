package pi.tn.workplacedigital_mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.NotificationAdapter;
import pi.tn.workplacedigital_mobile.api.NotificationApiService;
import pi.tn.workplacedigital_mobile.api.RetrofitClientNotifiation;
import pi.tn.workplacedigital_mobile.model.Notification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialisation du RecyclerView
        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Gestion du clic sur la flèche de retour
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Appel Retrofit pour récupérer les notifications
        NotificationApiService apiService = RetrofitClientNotifiation.getClient().create(NotificationApiService.class);
        apiService.getAllNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Remplir le RecyclerView avec l’adapter
                    adapter = new NotificationAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(NotificationsActivity.this, "Erreur lors du chargement des notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Échec de connexion : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
