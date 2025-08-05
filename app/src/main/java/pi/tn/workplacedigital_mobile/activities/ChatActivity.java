package pi.tn.workplacedigital_mobile.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.MessageAdapter;
import pi.tn.workplacedigital_mobile.model.Message;

public class ChatActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout sidebarHistory;
    private ListView lvHistory;
    private Button btnOpenSidebar, btnCloseSidebar;

    private RecyclerView rvMessages;
    private EditText editTextMessage;
    private Button buttonSend;

    private final ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;

    private final ArrayList<String> historyTitles = new ArrayList<>();
    private ArrayAdapter<String> historyAdapter;

    private static final String URL_CHAT = "http://172.19.3.131:8088/api/chat";
    private static final String URL_MESSAGES = "http://172.19.3.131:8088/api/chat/messages";

    private com.android.volley.RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialiser la queue Volley
        requestQueue = Volley.newRequestQueue(this);

        // Initialisation des vues
        drawerLayout = findViewById(R.id.drawer_layout);
        sidebarHistory = findViewById(R.id.sidebar_history);
        lvHistory = findViewById(R.id.lv_history);
        btnOpenSidebar = findViewById(R.id.btn_open_sidebar);
        btnCloseSidebar = findViewById(R.id.btn_close_sidebar);

        rvMessages = findViewById(R.id.rv_messages);
        editTextMessage = findViewById(R.id.et_message);
        buttonSend = findViewById(R.id.btn_send);

        // Toolbar avec bouton retour
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Configuration RecyclerView messages
        messageAdapter = new MessageAdapter(messages);
        rvMessages.setAdapter(messageAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        // Adapter et ListView pour l'historique
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyTitles);
        lvHistory.setAdapter(historyAdapter);

        // Charger historique au lancement
        loadHistory();

        // Gestion ouverture/fermeture sidebar
        btnOpenSidebar.setOnClickListener(v -> drawerLayout.openDrawer(sidebarHistory));
        btnCloseSidebar.setOnClickListener(v -> drawerLayout.closeDrawer(sidebarHistory));

        // Clic sur un élément de l'historique : charger messages correspondants
        lvHistory.setOnItemClickListener((parent, view, position, id) -> {
            String title = historyTitles.get(position);
            loadMessagesForTitle(title);
            drawerLayout.closeDrawer(sidebarHistory);
        });

        // Envoyer message au clic bouton
        buttonSend.setOnClickListener(v -> sendMessage());

        Button btnNewChat = findViewById(R.id.btn_new_chat);

        btnNewChat.setOnClickListener(v -> {
            messages.clear();
            messageAdapter.notifyDataSetChanged();
            editTextMessage.setText("");
            rvMessages.scrollToPosition(0);
            Toast.makeText(this, "Nouvelle conversation démarrée", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Envoie un message au backend et récupère la réponse
     */
    private void sendMessage() {
        final String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) return;

        // Ajouter message utilisateur localement
        Message userMessage = new Message("User", messageText);
        messages.add(userMessage);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        // Vider champ texte
        editTextMessage.setText("");

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("message", messageText);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_CHAT,
                    jsonBody,
                    response -> {
                        try {
                            if (response.has("response")) {
                                String botResponse = response.getString("response");

                                // Ajouter message bot localement
                                Message botMessage = new Message("Bot", botResponse);
                                messages.add(botMessage);
                                messageAdapter.notifyItemInserted(messages.size() - 1);
                                scrollToBottom();

                                // Recharge l'historique pour mise à jour
                                loadHistory();
                            } else {
                                Toast.makeText(this, "Réponse serveur invalide", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Erreur traitement réponse", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Erreur serveur: " + (error.getMessage() != null ? error.getMessage() : ""), Toast.LENGTH_SHORT).show()
            );

            // Politique de retry personnalisée (timeout 8 sec)
            request.setRetryPolicy(new DefaultRetryPolicy(
                    8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur interne", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Scroll automatique vers le bas du RecyclerView
     */
    private void scrollToBottom() {
        rvMessages.post(() -> rvMessages.scrollToPosition(messages.size() - 1));
    }

    /**
     * Charge l'historique des messages pour la sidebar (liste titres)
     */
    private void loadHistory() {
        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_MESSAGES,
                null,
                response -> {
                    try {
                        historyTitles.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject msg = response.getJSONObject(i);
                            String title = msg.optString("content", "Message " + (i + 1));
                            if (title.length() > 20) title = title.substring(0, 20) + "...";
                            historyTitles.add(title);
                        }
                        historyAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur parsing historique", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erreur réseau historique", Toast.LENGTH_SHORT).show()
        );

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(getRequest);
    }

    /**
     * Recharge tous les messages, ou filtre selon le titre si besoin
     */
    private void loadMessagesForTitle(String title) {
        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_MESSAGES,
                null,
                response -> {
                    try {
                        messages.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject msgObj = response.getJSONObject(i);
                            String sender = msgObj.getString("sender");
                            String content = msgObj.getString("content");
                            messages.add(new Message(sender, content));
                        }
                        messageAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur chargement messages", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erreur réseau messages", Toast.LENGTH_SHORT).show()
        );

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(getRequest);
    }

    /**
     * Gestion bouton retour toolbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
