package pi.tn.workplacedigital_mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.activities.MediaFullscreenActivity;
import pi.tn.workplacedigital_mobile.model.UserProfile;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.UserProfileViewHolder> {

    private final Context context;
    private final List<UserProfile> profiles;

    // URL de base pour les images stockées sur le backend
    private static final String BASE_IMAGE_URL = "http://172.19.3.134:8086/api/user-profiles/images/";

    // Constructeur avec contexte et liste des profils
    public UserProfileAdapter(Context context, List<UserProfile> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate le layout d'un item de profil utilisateur
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_profile, parent, false);
        return new UserProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        UserProfile user = profiles.get(position);

        holder.textUsername.setText(user.getUsername());
        holder.textEmail.setText(user.getEmail());
        holder.textBio.setText(user.getBio());

        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            String imageUrl = user.getImageUrl();

            // Si imageUrl est une URL complète (http ou https), on la garde telle quelle
            // Sinon, on ajoute la base
            if (!(imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
                imageUrl = BASE_IMAGE_URL + imageUrl;
            }

            final String finalImageUrl = imageUrl;

            Glide.with(context)
                    .load(finalImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.profileImageView);

            holder.profileImageView.setVisibility(View.VISIBLE);

            holder.profileImageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MediaFullscreenActivity.class);
                intent.putExtra(MediaFullscreenActivity.EXTRA_MEDIA_URL, finalImageUrl);
                intent.putExtra(MediaFullscreenActivity.EXTRA_IS_VIDEO, false);
                context.startActivity(intent);
            });

        } else {
            holder.profileImageView.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return profiles.size();
    }

    // ViewHolder interne pour recycler les vues
    public static class UserProfileViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername, textEmail, textBio;
        ImageView profileImageView;

        public UserProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            textBio = itemView.findViewById(R.id.textBio);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
