package pi.tn.workplacedigital_mobile.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.model.MessageDisc;

public class MessageDiscAdapter extends RecyclerView.Adapter<MessageDiscAdapter.MessageDiscViewHolder> {

    private final List<MessageDisc> messageList;
    private final Long currentUserId;

    public MessageDiscAdapter(List<MessageDisc> messageList, Long currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageDiscViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_discussion, parent, false);
        return new MessageDiscViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDiscViewHolder holder, int position) {
        MessageDisc message = messageList.get(position);

        // Affiche contenu + heure (format HH:mm)
        holder.contentTextView.setText(message.getContent());
        String timeOnly = formatTimeOnly(message.getTimestamp());
        holder.timestampTextView.setText(timeOnly);

        // Affichage de la date centrale au-dessus (format dd/MM/yyyy)
        String currentDate = formatDateOnly(message.getTimestamp());

        boolean showDate = false;
        if (position == 0) {
            // Toujours afficher pour le premier message
            showDate = true;
        } else {
            // Comparer la date avec le message précédent
            String previousDate = formatDateOnly(messageList.get(position - 1).getTimestamp());
            if (!currentDate.equals(previousDate)) {
                showDate = true;
            }
        }

        if (showDate) {
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(currentDate);
        } else {
            holder.dateTextView.setVisibility(View.GONE);
        }

        // Récupère les params layout pour la bulle
        LinearLayout.LayoutParams bubbleParams = (LinearLayout.LayoutParams) holder.messageContainer.getLayoutParams();

        // Gestion alignement + fond
        if (message.getSenderId().equals(currentUserId)) {
            // Message envoyé → alignement droite
            holder.messageContainer.setBackgroundResource(R.drawable.bubble_sent);
            bubbleParams.gravity = Gravity.END;
        } else {
            // Message reçu → alignement gauche
            holder.messageContainer.setBackgroundResource(R.drawable.bubble_received);
            bubbleParams.gravity = Gravity.START;
        }
        holder.messageContainer.setLayoutParams(bubbleParams);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageDiscViewHolder extends RecyclerView.ViewHolder {
        LinearLayout messageContainer;
        TextView contentTextView, timestampTextView, dateTextView;

        public MessageDiscViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.message_container);
            contentTextView = itemView.findViewById(R.id.textContent);
            timestampTextView = itemView.findViewById(R.id.textTimestamp);
            dateTextView = itemView.findViewById(R.id.textDate);
        }
    }

    private String formatTimeOnly(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return timeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timestamp;
        }
    }

    private String formatDateOnly(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
