package com.example.fu_self_learning_app.adapters.generate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.generate.GeneratedTopic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    Context context;
    List<GeneratedTopic> topicList;

    public TopicAdapter(Context context, List<GeneratedTopic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView textTopicTitle, textTopicDescription;
        RecyclerView recyclerViewLessons;
        LessonAdapter lessonAdapter;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            textTopicTitle = itemView.findViewById(R.id.textTopicTitle);
            textTopicDescription = itemView.findViewById(R.id.textTopicDescription);
            recyclerViewLessons = itemView.findViewById(R.id.recyclerViewLessons);
            recyclerViewLessons.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.instructor_topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        GeneratedTopic topic = topicList.get(position);
        holder.textTopicTitle.setText(topic.getTitle());
        holder.textTopicDescription.setText(topic.getDescription());
        holder.lessonAdapter = new LessonAdapter(holder.itemView.getContext(), topic.getLessons());
        holder.recyclerViewLessons.setAdapter(holder.lessonAdapter);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}
