package pi.tn.workplacedigital_mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.model.Discussion;

public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.DiscussionViewHolder> {

    public interface OnDiscussionClickListener {
        void onDiscussionClick(Discussion discussion);
    }

    private final List<Discussion> discussionList;
    private final OnDiscussionClickListener listener;

    public DiscussionListAdapter(List<Discussion> discussionList, OnDiscussionClickListener listener) {
        this.discussionList = discussionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscussionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discussion, parent, false);
        return new DiscussionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionViewHolder holder, int position) {
        Discussion discussion = discussionList.get(position);

        // Affiche titre et dernier message
        holder.textDiscussionTitle.setText("📩 Discussion #" + discussion.getId() + " - " + discussion.getTitle());
        holder.textLastMessage.setText(discussion.getLastMessage() != null ? discussion.getLastMessage() : "");

        // Gère le clic sur l'item
        holder.itemView.setOnClickListener(v -> listener.onDiscussionClick(discussion));
    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }

    static class DiscussionViewHolder extends RecyclerView.ViewHolder {
        TextView textDiscussionTitle;
        TextView textLastMessage;

        public DiscussionViewHolder(@NonNull View itemView) {
            super(itemView);
            textDiscussionTitle = itemView.findViewById(R.id.textDiscussionTitle);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
        }
    }
}
