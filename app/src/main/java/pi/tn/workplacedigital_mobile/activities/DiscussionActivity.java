package pi.tn.workplacedigital_mobile.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.DiscussionListAdapter;
import pi.tn.workplacedigital_mobile.adapters.MessageDiscAdapter;
import pi.tn.workplacedigital_mobile.api.ApiClientMessage;
import pi.tn.workplacedigital_mobile.api.ApiServiceMessage;
import pi.tn.workplacedigital_mobile.model.Discussion;
import pi.tn.workplacedigital_mobile.model.MessageDisc;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionActivity extends AppCompatActivity {

    // Layout drawer (sidebar)
    private DrawerLayout drawerLayout;
    private ImageButton btnOpenDrawer;

    // UI pour envoyer un message
    private Button buttonSend;
    private EditText editTextMessage;

    // RecyclerViews pour messages et discussions
    private RecyclerView recyclerViewMessages, recyclerViewDiscussions;

    // Titre de la discussion affichée
    private TextView textViewDiscussionTitle;

    // Adapters pour RecyclerViews
    private MessageDiscAdapter messageAdapter;
    private DiscussionListAdapter discussionListAdapter;

    private Toolbar toolbar;

    // API Retrofit
    private ApiServiceMessage apiService;

    // ID utilisateur connecté et discussion active
    private Long currentUserId = 1L;
    private Long currentDiscussionId = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        // Récupération des vues
        drawerLayout = findViewById(R.id.drawer_layout);
        btnOpenDrawer = findViewById(R.id.btn_open_drawer);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewDiscussions = findViewById(R.id.recyclerViewDiscussions);
        toolbar = findViewById(R.id.toolbar);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        textViewDiscussionTitle = findViewById(R.id.textViewDiscussionTitle);

        // Configurer les LayoutManagers pour les RecyclerViews
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDiscussions.setLayoutManager(new LinearLayoutManager(this));

        // Initialiser l'API Retrofit
        apiService = ApiClientMessage.getClient().create(ApiServiceMessage.class);

        // Charger la liste des discussions (sidebar)
        loadDiscussionList();

        // Charger les messages de la discussion active au lancement
        loadMessages(currentDiscussionId);

        // Ouvrir/fermer la sidebar drawer au clic sur le bouton
        btnOpenDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.drawer_view))) {
                drawerLayout.closeDrawer(findViewById(R.id.drawer_view));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.drawer_view));
            }
        });

        // Flèche retour : fermer l'activité
        toolbar.setNavigationOnClickListener(v -> finish());

        // Envoyer un message quand bouton cliqué
        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            } else {
                Toast.makeText(this, "Veuillez saisir un message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Charge les messages d'une discussion via API et met à jour le RecyclerView des messages
     * @param discussionId id de la discussion à charger
     */
    private void loadMessages(Long discussionId) {
        apiService.getMessagesByDiscussion(discussionId).enqueue(new Callback<List<MessageDisc>>() {
            @Override
            public void onResponse(Call<List<MessageDisc>> call, Response<List<MessageDisc>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter = new MessageDiscAdapter(response.body(), currentUserId);
                    recyclerViewMessages.setAdapter(messageAdapter);
                    // Scroll automatique vers le dernier message
                    recyclerViewMessages.scrollToPosition(response.body().size() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<MessageDisc>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DiscussionActivity.this, "Erreur lors du chargement des messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Charge la liste des discussions via API et met à jour la sidebar
     */
    private void loadDiscussionList() {
        apiService.getDiscussionList(currentUserId).enqueue(new Callback<List<Discussion>>() {
            @Override
            public void onResponse(Call<List<Discussion>> call, Response<List<Discussion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Discussion> discussions = response.body();

                    // Ajoute ce log ici pour vérifier la taille et le contenu
                    Log.d("DiscussionActivity", "Discussions chargées : " + discussions.size());
                    for (Discussion d : discussions) {
                        Log.d("DiscussionActivity", "Discussion ID: " + d.getId() + ", Titre: " + d.getTitle());
                    }

                    discussionListAdapter = new DiscussionListAdapter(discussions, discussion -> {
                        currentDiscussionId = discussion.getId();
                        textViewDiscussionTitle.setText("Discussion #" + discussion.getId());
                        loadMessages(currentDiscussionId);
                        drawerLayout.closeDrawer(findViewById(R.id.drawer_view));
                    });
                    recyclerViewDiscussions.setAdapter(discussionListAdapter);
                } else {
                    Log.e("DiscussionActivity", "Réponse API invalide");
                }
            }

            @Override
            public void onFailure(Call<List<Discussion>> call, Throwable t) {
                t.printStackTrace();
                Log.e("DiscussionActivity", "Erreur réseau : " + t.getMessage());
            }
        });
    }


    /**
     * Envoie un message via l'API puis recharge les messages
     * @param content texte du message à envoyer
     */
    private void sendMessage(String content) {
        MessageDisc newMessage = new MessageDisc();
        newMessage.setContent(content);
        newMessage.setSenderId(currentUserId);
        newMessage.setDiscussionId(currentDiscussionId);

        apiService.sendMessage(newMessage).enqueue(new Callback<MessageDisc>() {
            @Override
            public void onResponse(Call<MessageDisc> call, Response<MessageDisc> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Vider la zone de saisie
                    editTextMessage.setText("");
                    // Recharger les messages avec le nouveau message
                    loadMessages(currentDiscussionId);
                } else {
                    Toast.makeText(DiscussionActivity.this, "Erreur lors de l'envoi du message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageDisc> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DiscussionActivity.this, "Erreur réseau lors de l'envoi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
