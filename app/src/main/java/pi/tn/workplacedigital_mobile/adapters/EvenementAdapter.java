package pi.tn.workplacedigital_mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.model.Evenement;

public class EvenementAdapter extends RecyclerView.Adapter<EvenementAdapter.EvenementViewHolder> {

    private final List<Evenement> evenementList;

    public EvenementAdapter(List<Evenement> evenementList) {
        this.evenementList = evenementList;
    }

    @NonNull
    @Override
    public EvenementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evenement, parent, false);
        return new EvenementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EvenementViewHolder holder, int position) {
        Evenement evenement = evenementList.get(position);

        holder.titre.setText(evenement.getTitre());
        holder.description.setText(evenement.getDescription());

        // Formatage des dates
        holder.dateDebut.setText("Début : " + formatDateTime(evenement.getDateDebut()));
        holder.dateFin.setText("Fin : " + formatDateTime(evenement.getDateFin()));
    }

    @Override
    public int getItemCount() {
        return evenementList.size();
    }

    // Méthode utilitaire pour formater une date String en format lisible français
    private String formatDateTime(String dateTimeRaw) {
        if (dateTimeRaw == null || dateTimeRaw.isEmpty()) {
            return "N/A";
        }

        // Format d'entrée attendu (exemple ISO 8601 sans fuseau horaire)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        // Format de sortie souhaité (ex: 15 juillet 2025 à 16:00)
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy 'à' HH:mm", Locale.FRANCE);

        try {
            Date date = inputFormat.parse(dateTimeRaw);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Si format invalide, retourne la chaîne brute (au moins on affiche quelque chose)
            return dateTimeRaw;
        }
    }

    static class EvenementViewHolder extends RecyclerView.ViewHolder {
        TextView titre, description, dateDebut, dateFin;

        EvenementViewHolder(View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.textTitre);
            description = itemView.findViewById(R.id.textDescription);
            dateDebut = itemView.findViewById(R.id.textDateDebut);
            dateFin = itemView.findViewById(R.id.textDateFin);
        }
    }
}
