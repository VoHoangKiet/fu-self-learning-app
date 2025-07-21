package com.example.fu_self_learning_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.Lesson;
import com.example.fu_self_learning_app.models.Topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExpandableTopicAdapter extends RecyclerView.Adapter<ExpandableTopicAdapter.TopicViewHolder> {

    private List<Topic> topics;
    private OnLessonClickListener lessonClickListener;

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson, int position);
    }

    public ExpandableTopicAdapter(List<Topic> topics) {
        this.topics = topics;
    }

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.lessonClickListener = listener;
    }

    public void updateTopics(List<Topic> newTopics) {
        // Sort topics by ID before updating
        this.topics = sortTopicsById(newTopics);
        notifyDataSetChanged();
    }

    private List<Topic> sortTopicsById(List<Topic> topics) {
        List<Topic> sortedList = new ArrayList<>(topics);
        
        Collections.sort(sortedList, new Comparator<Topic>() {
            @Override
            public int compare(Topic topic1, Topic topic2) {
                // Sort by ID ascending (1, 2, 3, ...)
                return Integer.compare(topic1.getId(), topic2.getId());
            }
        });
        
        return sortedList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_expandable, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.bind(topic);
    }

    @Override
    public int getItemCount() {
        return topics != null ? topics.size() : 0;
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout topicHeader;
        private ImageView imageViewExpandArrow;
        private TextView textViewTopicTitle;
        private TextView textViewTopicDuration;
        private RecyclerView recyclerViewLessons;
        
        private LessonAdapter lessonAdapter;
        private boolean isExpanded = false;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            
            topicHeader = itemView.findViewById(R.id.topicHeader);
            imageViewExpandArrow = itemView.findViewById(R.id.imageViewExpandArrow);
            textViewTopicTitle = itemView.findViewById(R.id.textViewTopicTitle);
            textViewTopicDuration = itemView.findViewById(R.id.textViewTopicDuration);
            recyclerViewLessons = itemView.findViewById(R.id.recyclerViewLessons);

            // Setup nested RecyclerView for lessons
            recyclerViewLessons.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerViewLessons.setNestedScrollingEnabled(false);

            topicHeader.setOnClickListener(v -> toggleExpansion());
        }

        public void bind(Topic topic) {
            textViewTopicTitle.setText(topic.getTitle());
            
            // Format duration
            String duration = formatTopicDuration(topic);
            textViewTopicDuration.setText(duration);

            // Setup lessons adapter
            if (topic.getLessons() != null && !topic.getLessons().isEmpty()) {
                // Sort lessons by ID before displaying
                List<Lesson> sortedLessons = sortLessonsById(topic.getLessons());
                
                if (lessonAdapter == null) {
                    lessonAdapter = new LessonAdapter(sortedLessons);
                    lessonAdapter.setOnLessonClickListener((lesson, position) -> {
                        if (lessonClickListener != null) {
                            lessonClickListener.onLessonClick(lesson, position);
                        }
                    });
                    recyclerViewLessons.setAdapter(lessonAdapter);
                } else {
                    lessonAdapter.updateLessons(sortedLessons);
                }
            }

            // Reset expansion state
            isExpanded = false;
            recyclerViewLessons.setVisibility(View.GONE);
            imageViewExpandArrow.setRotation(0);
        }

        private void toggleExpansion() {
            isExpanded = !isExpanded;
            
            // Animate arrow rotation
            float fromRotation = isExpanded ? 0f : 180f;
            float toRotation = isExpanded ? 180f : 0f;
            
            RotateAnimation rotateAnimation = new RotateAnimation(
                    fromRotation, toRotation,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f
            );
            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            imageViewExpandArrow.startAnimation(rotateAnimation);

            // Show/hide lessons
            recyclerViewLessons.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        private String formatTopicDuration(Topic topic) {
            if (topic.getTotalDuration() <= 0) {
                return "0s";
            }

            int totalMinutes = topic.getTotalDuration();
            if (totalMinutes >= 60) {
                int hours = totalMinutes / 60;
                int minutes = totalMinutes % 60;
                if (minutes > 0) {
                    return hours + "h " + minutes + "m";
                } else {
                    return hours + "h";
                }
            } else {
                // For topics, show as seconds for consistency with UI (small durations)
                return totalMinutes + "s";
            }
        }

        private List<Lesson> sortLessonsById(List<Lesson> lessons) {
            List<Lesson> sortedList = new ArrayList<>(lessons);
            
            Collections.sort(sortedList, new Comparator<Lesson>() {
                @Override
                public int compare(Lesson lesson1, Lesson lesson2) {
                    // Sort by ID ascending (1, 2, 3, ...)
                    return Integer.compare(lesson1.getId(), lesson2.getId());
                }
            });
            
            return sortedList;
        }
    }
} 