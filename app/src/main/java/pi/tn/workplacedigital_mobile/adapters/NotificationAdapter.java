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
import pi.tn.workplacedigital_mobile.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notificationList;

    // Constructeur
    public NotificationAdapter(List<Notification> notifications) {
        this.notificationList = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        // Afficher le message
        holder.messageText.setText(notification.getMessage());

        // Afficher le statut "Lu" ou "Non lu"
        holder.vueText.setText(notification.isVue() ? "✅ Lu" : "🆕 Non lu");

        // Formatage de la date avant affichage
        String formattedDate = formatDate(notification.getDateEnvoi());
        holder.dateText.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // ViewHolder pour recyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, vueText;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);
            vueText = itemView.findViewById(R.id.vueText);
        }
    }

    // Méthode utilitaire pour formater la date ISO en "dd/MM/yyyy HH:mm"
    private String formatDate(String dateString) {
        try {
            // Format ISO : 2024-07-14T16:30:00
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = isoFormat.parse(dateString);

            // Format souhaité : 14/07/2024 16:30
            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return desiredFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            // Si erreur : on renvoie la date brute sans plantage
            return dateString;
        }
    }
}
