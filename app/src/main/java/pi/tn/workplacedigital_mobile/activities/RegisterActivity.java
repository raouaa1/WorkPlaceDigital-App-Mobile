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
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.api.ApiClient;
import pi.tn.workplacedigital_mobile.api.ApiService;
import pi.tn.workplacedigital_mobile.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // Déclaration des composants UI
    private EditText firstname, lastname, email, phone, password;
    private Spinner roleSpinner;
    private Button registerBtn;
    private TextView loginLink;

    private ImageView showPasswordBtn;   // bouton œil mot de passe
    private boolean isPasswordVisible = false; // état actuel (masqué par défaut)

    private ApiService apiService;
    private String selectedRole; // Code du rôle sélectionné

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Liaison des vues XML
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerBtn = findViewById(R.id.registerBtn);
        loginLink = findViewById(R.id.loginLink);
        showPasswordBtn = findViewById(R.id.showPasswordBtn); // bouton œil

        // Initialisation de l’API Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        // Configuration du spinner pour la sélection des rôles
        setupRoleSpinner();

        // Configuration du texte cliquable "Already have an account? Log in"
        setupClickableLoginText();

        // Écouteur bouton inscription
        registerBtn.setOnClickListener(v -> registerUser());

        // Action bouton œil pour afficher/masquer mot de passe
        showPasswordBtn.setOnClickListener(v -> togglePasswordVisibility());

        // Démarrer animation bulles (si présentes)
        animateBubbles();
    }

    /**
     * Bascule entre affichage et masquage du mot de passe
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // remettre en mode mot de passe masqué
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showPasswordBtn.setImageResource(R.drawable.ic_visibility_off);
            isPasswordVisible = false;
        } else {
            // passer en mode texte visible
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            showPasswordBtn.setImageResource(R.drawable.ic_visibility);
            isPasswordVisible = true;
        }
        password.setSelection(password.getText().length()); // pour remettre le curseur à la fin
    }

    /**
     * Configure le texte "Log in" en rouge, cliquable, sans soulignement.
     */
    private void setupClickableLoginText() {
        String text = "Already have an account? Log in";
        SpannableString spannableString = new SpannableString(text);

        int start = text.indexOf("Log in");
        int end = start + "Log in".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.red));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginLink.setText(spannableString);
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());
        loginLink.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * Valide un numéro de téléphone tunisien Ooredoo.
     */
    private boolean isValidTunisianOoredooPhone(String phone) {
        String normalizedPhone = phone.replaceAll("\\s+", "");
        String regex = "^2[0-9]\\d{6}$";
        return normalizedPhone.matches(regex);
    }

    /**
     * Configuration du spinner de sélection de rôle.
     */
    private void setupRoleSpinner() {
        class RoleItem {
            String code;
            String label;

            RoleItem(String code, String label) {
                this.code = code;
                this.label = label;
            }

            @Override
            public String toString() {
                return label;
            }
        }

        List<RoleItem> roleItems = new ArrayList<>();
        roleItems.add(new RoleItem("", "Select a role 😊"));
        roleItems.add(new RoleItem("EMPLOYEE", "Employee"));
        roleItems.add(new RoleItem("COLLABORATOR", "Collaborator"));
        roleItems.add(new RoleItem("MANAGER", "Manager"));
        roleItems.add(new RoleItem("ADMIN", "Admin"));

        ArrayAdapter<RoleItem> adapter = new ArrayAdapter<RoleItem>(
                this, android.R.layout.simple_spinner_item, roleItems) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    tv.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = (position > 0) ? roleItems.get(position).code : null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = null;
            }
        });
    }

    /**
     * Anime les bulles d’arrière-plan.
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
     * Validation des champs et appel API pour inscription.
     */
    private void registerUser() {
        String firstnameText = firstname.getText().toString().trim();
        String lastnameText = lastname.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String passwordText = password.getText().toString();

        if (firstnameText.isEmpty() || lastnameText.isEmpty() || emailText.isEmpty()
                || phoneText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidTunisianOoredooPhone(phoneText)) {
            Toast.makeText(this, "Numéro Ooredoo invalide (20 à 29 suivi de 6 chiffres)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRole == null || selectedRole.isEmpty()) {
            Toast.makeText(this, "Sélectionnez un rôle", Toast.LENGTH_SHORT).show();
            return;
        }

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        if (!emailPattern.matcher(emailText).matches()) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailText.endsWith("@ooredoo.tn")) {
            Toast.makeText(this, "Email doit contenir '@ooredoo.tn'", Toast.LENGTH_SHORT).show();
            return;
        }

        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
        if (!passwordPattern.matcher(passwordText).matches()) {
            Toast.makeText(this, "Mot de passe trop faible : 8+, maj, min, chiffre et symbole", Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User();
        user.setFirstname(firstnameText);
        user.setLastname(lastnameText);
        user.setEmail(emailText);
        user.setPhone(phoneText);
        user.setPassword(passwordText);

        List<String> roles = new ArrayList<>();
        roles.add(selectedRole);
        user.setRole(roles);

        Call<User> call = apiService.register(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Erreur d'inscription", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
