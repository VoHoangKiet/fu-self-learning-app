package com.example.fu_self_learning_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private List<Topic> topics;
    private OnTopicClickListener onTopicClickListener;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic, int position);
    }

    public TopicAdapter(List<Topic> topics) {
        this.topics = topics;
    }

    public void setOnTopicClickListener(OnTopicClickListener listener) {
        this.onTopicClickListener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.bind(topic, position);
    }

    @Override
    public int getItemCount() {
        return topics != null ? topics.size() : 0;
    }

    public void updateTopics(List<Topic> newTopics) {
        this.topics = newTopics;
        notifyDataSetChanged();
    }

    class TopicViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTopicNumber;
        private TextView textViewTopicTitle;
        private TextView textViewTopicDescription;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTopicNumber = itemView.findViewById(R.id.textViewTopicNumber);
            textViewTopicTitle = itemView.findViewById(R.id.textViewTopicTitle);
            textViewTopicDescription = itemView.findViewById(R.id.textViewTopicDescription);

            itemView.setOnClickListener(v -> {
                if (onTopicClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTopicClickListener.onTopicClick(topics.get(position), position);
                    }
                }
            });
        }

        public void bind(Topic topic, int position) {
            // Topic number (position + 1)
            textViewTopicNumber.setText(String.valueOf(position + 1));
            
            // Topic title và description
            textViewTopicTitle.setText(topic.getTitle() != null ? topic.getTitle() : "Topic " + (position + 1));
            textViewTopicDescription.setText(topic.getDescription() != null ? topic.getDescription() : "No description available");
        }
    }
} 