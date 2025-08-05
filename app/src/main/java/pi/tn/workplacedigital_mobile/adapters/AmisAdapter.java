package pi.tn.workplacedigital_mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.model.ListItem;
import pi.tn.workplacedigital_mobile.model.User;

/**
 * Adapter RecyclerView pour afficher une liste mixte : titres et utilisateurs.
 * Permet d'ajouter ou supprimer un ami selon le statut estAmi.
 */
public class AmisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<ListItem> items;
    private final long currentUserId;
    private final OnAmiActionListener listener;

    public interface OnAmiActionListener {
        void onAddAmi(User user);
        void onRemoveAmi(User user);
    }

    public AmisAdapter(Context context, List<ListItem> items, long currentUserId, OnAmiActionListener listener) {
        this.context = context;
        this.items = items;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    private static final int TYPE_TITLE = ListItem.TYPE_TITLE;
    private static final int TYPE_USER = ListItem.TYPE_USER;

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_TITLE) {
            View view = inflater.inflate(R.layout.item_title, parent, false);
            return new TitleViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_ami, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);

        if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).titleText.setText(item.getTitle());
        } else if (holder instanceof UserViewHolder) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            User user = item.getUser();

            userHolder.nomText.setText(user.getFirstname() + " " + user.getLastname());

            // Si c'est un ami : afficher seulement bouton Supprimer
            if (item.isAmi()) {
                userHolder.btnAjouter.setVisibility(View.GONE);
                userHolder.btnSupprimer.setVisibility(View.VISIBLE);
            }
            // Sinon : afficher seulement bouton Ajouter ou Supprimer
            else {
                userHolder.btnAjouter.setVisibility(View.VISIBLE);
                userHolder.btnSupprimer.setVisibility(View.VISIBLE);
            }

            // Clic Ajouter
            userHolder.btnAjouter.setOnClickListener(v -> listener.onAddAmi(user));

            // Clic Supprimer
            userHolder.btnSupprimer.setOnClickListener(v -> listener.onRemoveAmi(user));
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        TitleViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nomText;
        Button btnAjouter, btnSupprimer;

        UserViewHolder(View itemView) {
            super(itemView);
            nomText = itemView.findViewById(R.id.textViewAmiNom);
            btnAjouter = itemView.findViewById(R.id.btnAjouter);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimer);
        }
    }
}
