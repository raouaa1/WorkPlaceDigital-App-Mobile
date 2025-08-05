package pi.tn.workplacedigital_mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.model.Activite;

public class ActiviteAdapter extends RecyclerView.Adapter<ActiviteAdapter.ActiviteViewHolder> {

    private final List<Activite> activiteList;
    // Format de date désiré : jour/mois/année
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ActiviteAdapter(List<Activite> activiteList) {
        this.activiteList = activiteList;
    }

    @NonNull
    @Override
    public ActiviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activite, parent, false);
        return new ActiviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiviteViewHolder holder, int position) {
        Activite activite = activiteList.get(position);
        holder.nom.setText(activite.getNom());

        if (activite.getDateActivite() != null) {
            // Affiche la date formatée "dd/MM/yyyy"
            holder.date.setText(dateFormat.format(activite.getDateActivite()));
        } else {
            holder.date.setText("Date inconnue");
        }
    }

    @Override
    public int getItemCount() {
        return activiteList.size();
    }

    static class ActiviteViewHolder extends RecyclerView.ViewHolder {
        TextView nom, date;

        public ActiviteViewHolder(@NonNull View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.textViewNomActivite);
            date = itemView.findViewById(R.id.textViewDateActivite);
        }
    }
}
