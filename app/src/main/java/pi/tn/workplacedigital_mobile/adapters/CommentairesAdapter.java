package pi.tn.workplacedigital_mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.model.Commentaire;

public class CommentairesAdapter extends RecyclerView.Adapter<CommentairesAdapter.ViewHolder> {

    public interface OnCommentaireInteractionListener {
        void onModifier(Commentaire commentaire);
        void onSupprimer(Commentaire commentaire);
        void onEnregistrer(Commentaire commentaire, String nouveauContenu);
        void onAnnuler();
    }

    private List<Commentaire> commentaires;
    private OnCommentaireInteractionListener listener;
    private int commentaireEnEditionId = -1;

    public CommentairesAdapter(List<Commentaire> commentaires, OnCommentaireInteractionListener listener) {
        this.commentaires = commentaires;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commentaire, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Commentaire c = commentaires.get(position);

        holder.textUserId.setText("Utilisateur " + c.getUserId());
        holder.textDate.setText(c.getDateCreation());

        boolean enEdition = (c.getId() == commentaireEnEditionId);

        if (enEdition) {
            holder.textContenu.setVisibility(View.GONE);
            holder.editContenu.setVisibility(View.VISIBLE);
            holder.layoutBoutonsEdition.setVisibility(View.VISIBLE);
            holder.editContenu.setText(holder.editContenu.getText().toString().isEmpty() ? c.getContenu() : holder.editContenu.getText());
        } else {
            holder.textContenu.setVisibility(View.VISIBLE);
            holder.editContenu.setVisibility(View.GONE);
            holder.layoutBoutonsEdition.setVisibility(View.GONE);
            holder.textContenu.setText(c.getContenu());
        }

        // Bouton Modifier
        holder.btnModifier.setVisibility(enEdition ? View.GONE : View.VISIBLE);
        holder.btnModifier.setOnClickListener(v -> {
            commentaireEnEditionId = c.getId();
            notifyDataSetChanged();
            listener.onModifier(c);
        });

        // Bouton Supprimer
        holder.btnSupprimer.setOnClickListener(v -> listener.onSupprimer(c));

        // Bouton Enregistrer
        holder.btnEnregistrer.setOnClickListener(v -> {
            String nouveauContenu = holder.editContenu.getText().toString().trim();
            if (nouveauContenu.isEmpty()) {
                Toast.makeText(holder.itemView.getContext(), "⚠️ Le commentaire modifié ne peut pas être vide.", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onEnregistrer(c, nouveauContenu);
            commentaireEnEditionId = -1;
        });

        // Bouton Annuler
        holder.btnAnnuler.setOnClickListener(v -> {
            commentaireEnEditionId = -1;
            notifyDataSetChanged();
            listener.onAnnuler();
        });
    }

    @Override
    public int getItemCount() {
        return commentaires.size();
    }

    public void updateCommentaires(List<Commentaire> newComments) {
        this.commentaires = newComments;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textUserId, textContenu, textDate;
        Button btnModifier, btnSupprimer, btnEnregistrer, btnAnnuler;
        EditText editContenu;
        LinearLayout layoutBoutonsEdition;

        ViewHolder(View itemView) {
            super(itemView);
            textUserId = itemView.findViewById(R.id.textUserId);
            textContenu = itemView.findViewById(R.id.textContenu);
            textDate = itemView.findViewById(R.id.textDate);
            btnModifier = itemView.findViewById(R.id.btnModifier);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimer);
            btnEnregistrer = itemView.findViewById(R.id.btnEnregistrer);
            btnAnnuler = itemView.findViewById(R.id.btnAnnuler);
            editContenu = itemView.findViewById(R.id.editContenu);
            layoutBoutonsEdition = itemView.findViewById(R.id.layoutBoutonsEdition);
        }
    }
}