package pi.tn.workplacedigital_mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.model.Collaborateur;

public class CollaborateurAdapter extends RecyclerView.Adapter<CollaborateurAdapter.CollaborateurViewHolder> {

    private final List<Collaborateur> collaborateurList;

    public CollaborateurAdapter(List<Collaborateur> collaborateurList) {
        this.collaborateurList = collaborateurList;
    }

    @NonNull
    @Override
    public CollaborateurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collaborateur, parent, false);
        return new CollaborateurViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaborateurViewHolder holder, int position) {
        Collaborateur c = collaborateurList.get(position);
        holder.nom.setText(c.getNom());
        holder.poste.setText(c.getPoste());
    }

    @Override
    public int getItemCount() {
        return collaborateurList.size();
    }

    static class CollaborateurViewHolder extends RecyclerView.ViewHolder {
        TextView nom, poste;

        CollaborateurViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.textNom);
            poste = itemView.findViewById(R.id.textPoste);
        }
    }
}
