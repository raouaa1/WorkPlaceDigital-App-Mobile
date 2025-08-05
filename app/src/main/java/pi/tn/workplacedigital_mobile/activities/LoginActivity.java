package pi.tn.workplacedigital_mobile.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.api.ApiClient;
import pi.tn.workplacedigital_mobile.api.ApiService;
import pi.tn.workplacedigital_mobile.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn;
    private TextView goRegister;
    private ApiService apiService;
    private ImageView showPasswordBtn;
    private boolean isPasswordVisible = false; // état de visibilité du mot de passe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });





        // Liaison des vues
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        goRegister = findViewById(R.id.goRegister);
        showPasswordBtn = findViewById(R.id.showPasswordBtn);

        // Initialisation API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Listener bouton login
        loginBtn.setOnClickListener(v -> loginUser());

        // Texte cliquable "Create one"
        setupClickableRegisterText();

        // Animation des bulles
        animateBubbles();

        // Gérer le bouton œil pour afficher / masquer le mot de passe
        setupPasswordToggle();
    }

    /**
     * Gère l'affichage / masquage du mot de passe avec le bouton œil
     */
    private void setupPasswordToggle() {
        showPasswordBtn.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Masquer le mot de passe
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordBtn.setImageResource(R.drawable.ic_visibility_off);
                isPasswordVisible = false;
            } else {
                // Afficher le mot de passe
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordBtn.setImageResource(R.drawable.ic_visibility);
                isPasswordVisible = true;
            }
            // Maintenir le curseur à la fin du texte
            password.setSelection(password.getText().length());
        });
    }

    /**
     * Configure le TextView goRegister pour que "Create one" soit en rouge et cliquable
     */
    private void setupClickableRegisterText() {
        String text = "Don't have an account? Create one";
        SpannableString spannableString = new SpannableString(text);

        int start = text.indexOf("Create one");
        int end = start + "Create one".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Ouvre l'activité d'inscription
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.red)); // Couleur rouge
                ds.setUnderlineText(false); // Pas de soulignement
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        goRegister.setText(spannableString);
        goRegister.setMovementMethod(LinkMovementMethod.getInstance());
        goRegister.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * Anime les bulles avec des mouvements verticaux
     */
    private void animateBubbles() {
        animateBubble(findViewById(R.id.bubble_1), 30f, 3000);
        animateBubble(findViewById(R.id.bubble_2), 40f, 4000);
        animateBubble(findViewById(R.id.bubble_3), 20f, 3500);
        animateBubble(findViewById(R.id.bubble_4), 25f, 3700);
        animateBubble(findViewById(R.id.bubble_5), 15f, 4200);
        animateBubble(findViewById(R.id.bubble_6), 35f, 3200);
    }

    private void animateBubble(View bubble, float translationY, long duration) {
        if (bubble == null) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(bubble, "translationY", 0f, translationY);
        animator.setDuration(duration);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    /**
     * Fonction de connexion avec validation des champs et email contenant @ooredoo
     */
    private void loginUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        // Vérification des champs
        if (emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifie que l'email contient @ooredoo
        if (!emailText.contains("@ooredoo.tn")) {
            Toast.makeText(this, "L'email doit contenir '@ooredoo.tn'", Toast.LENGTH_SHORT).show();
            return;
        }

        // Préparer l'objet User pour l'API
        User user = new User();
        user.setEmail(emailText);
        user.setPassword(passwordText);

        // Appel API login via Retrofit
        Call<User> call = apiService.login(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User loggedInUser = response.body();

                    // Afficher message fixe sans prénom
                    Toast.makeText(LoginActivity.this, "Bienvenue sur WorkPlace Digital Mobile", Toast.LENGTH_SHORT).show();


                    // Redirection vers DashboardActivity
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("firstname", loggedInUser.getFirstname());  // ⬅️ passer le prénom ici
                    startActivity(intent);
                    finish();


                } else {
                    Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
