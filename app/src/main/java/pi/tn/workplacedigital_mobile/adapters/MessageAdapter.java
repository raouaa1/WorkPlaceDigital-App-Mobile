package pi.tn.workplacedigital_mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.tn.workplacedigital_mobile.model.Message;
import pi.tn.workplacedigital_mobile.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvContent;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.tvSender.setText(msg.getSender());
        holder.tvContent.setText(msg.getContent());

        // Colorer en rouge foncé si c'est un message de l'utilisateur
        if ("User".equals(msg.getSender())) {
            holder.tvSender.setTextColor(0xFF8B0000);  // rouge foncé
        } else {
            holder.tvSender.setTextColor(0xFF000000);  // noir pour bot
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
