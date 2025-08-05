package pi.tn.workplacedigital_mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.activities.CommentairesActivity;
import pi.tn.workplacedigital_mobile.activities.MediaFullscreenActivity;
import pi.tn.workplacedigital_mobile.models.Publication;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder> {

    public interface OnPublicationActionListener {
        void onLike(Long publicationId, int position);
        void onDelete(Long publicationId, int position);
        void onEdit(Publication publication, int position);
    }

    private Context context;
    private List<Publication> publications;
    private OnPublicationActionListener listener;

    private static final String BASE_IMAGE_URL = "http://172.19.3.134:8086/images/images/";
    private static final String BASE_VIDEO_URL = "http://172.19.3.134:8086/images/videos/";

    public PublicationAdapter(Context context, List<Publication> publications, OnPublicationActionListener listener) {
        this.context = context;
        this.publications = publications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ⚠️ Corrigé : on doit d'abord "inflater" la vue
        View view = LayoutInflater.from(context).inflate(R.layout.item_publication, parent, false);
        return new PublicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicationViewHolder holder, int position) {
        Publication pub = publications.get(position);

        holder.textAuteur.setText("Auteur: " + pub.getAuteurId());
        holder.textDate.setText(pub.getDatePublication());
        holder.textContenu.setText(pub.getContenu());
        holder.textLikes.setText("❤️ " + pub.getNbLikes());

        // ➤ Affichage de l'image si elle existe
        if (pub.getImage() != null && !pub.getImage().isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            String imageUrl = BASE_IMAGE_URL + pub.getImage();
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.logoooredoo)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MediaFullscreenActivity.class);
                intent.putExtra(MediaFullscreenActivity.EXTRA_MEDIA_URL, imageUrl);
                intent.putExtra(MediaFullscreenActivity.EXTRA_IS_VIDEO, false);
                context.startActivity(intent);
            });
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        // ➤ Affichage de la vidéo si elle existe
        if (pub.getVideo() != null && !pub.getVideo().isEmpty()) {
            holder.videoView.stopPlayback();
            holder.videoContainer.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.VISIBLE);

            String videoUrl = BASE_VIDEO_URL + pub.getVideo();
            holder.videoView.setVideoURI(Uri.parse(videoUrl));

            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.videoView);
            holder.videoView.setMediaController(mediaController);

            holder.videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                holder.videoView.seekTo(1);
            });

            holder.videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e("VideoView", "Erreur lecture vidéo: what=" + what + ", extra=" + extra);
                holder.videoView.setVisibility(View.GONE);
                holder.videoContainer.setVisibility(View.GONE);
                Toast.makeText(context, "Impossible de lire la vidéo", Toast.LENGTH_SHORT).show();
                return true;
            });

            holder.videoView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MediaFullscreenActivity.class);
                intent.putExtra(MediaFullscreenActivity.EXTRA_MEDIA_URL, videoUrl);
                intent.putExtra(MediaFullscreenActivity.EXTRA_IS_VIDEO, true);
                context.startActivity(intent);
            });
        } else {
            holder.videoView.stopPlayback();
            holder.videoView.setVisibility(View.GONE);
            holder.videoContainer.setVisibility(View.GONE);
        }

        // ➤ Actions des boutons
        holder.btnLike.setOnClickListener(v -> {
            if (listener != null) listener.onLike(pub.getId(), position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(pub.getId(), position);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(pub, position);
        });

        // ✅ Bouton pour afficher les commentaires
        holder.btnCommentaires.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentairesActivity.class);
            intent.putExtra("publicationId", pub.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return publications.size();
    }

    public static class PublicationViewHolder extends RecyclerView.ViewHolder {
        TextView textAuteur, textDate, textContenu, textLikes;
        ImageView imageView;
        VideoView videoView;
        FrameLayout videoContainer;
        Button btnLike, btnDelete, btnEdit, btnCommentaires;

        public PublicationViewHolder(@NonNull View itemView) {
            super(itemView);
            textAuteur = itemView.findViewById(R.id.textAuteur);
            textDate = itemView.findViewById(R.id.textDate);
            textContenu = itemView.findViewById(R.id.textContenu);
            textLikes = itemView.findViewById(R.id.textLikes);
            imageView = itemView.findViewById(R.id.imageView);
            videoView = itemView.findViewById(R.id.videoView);
            videoContainer = itemView.findViewById(R.id.videoContainer);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCommentaires = itemView.findViewById(R.id.btnCommentaires); // ✅ AJOUTÉ ICI
        }
    }
}
