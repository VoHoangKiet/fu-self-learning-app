package com.example.fu_self_learning_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.Lesson;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson, int position);
    }

    public LessonAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.listener = listener;
    }

    public void updateLessons(List<Lesson> newLessons) {
        this.lessons = newLessons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.bind(lesson);
    }

    @Override
    public int getItemCount() {
        return lessons != null ? lessons.size() : 0;
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewLessonIcon;
        private TextView textViewLessonTitle;
        private TextView textViewLessonDuration;
        private ImageView imageViewCompletionStatus;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageViewLessonIcon = itemView.findViewById(R.id.imageViewLessonIcon);
            textViewLessonTitle = itemView.findViewById(R.id.textViewLessonTitle);
            textViewLessonDuration = itemView.findViewById(R.id.textViewLessonDuration);
            imageViewCompletionStatus = itemView.findViewById(R.id.imageViewCompletionStatus);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onLessonClick(lessons.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }

        public void bind(Lesson lesson) {
            textViewLessonTitle.setText(lesson.getTitle());
            
            // Format duration
            String duration = formatLessonDuration(lesson.getDuration());
            textViewLessonDuration.setText(duration);

            // Set icon based on lesson type
            String lessonType = lesson.getLessonType();
            if ("video".equalsIgnoreCase(lessonType)) {
                imageViewLessonIcon.setImageResource(android.R.drawable.ic_media_play);
            } else if ("text".equalsIgnoreCase(lessonType)) {
                imageViewLessonIcon.setImageResource(android.R.drawable.ic_menu_edit);
            } else if ("quiz".equalsIgnoreCase(lessonType)) {
                imageViewLessonIcon.setImageResource(android.R.drawable.ic_menu_help);
            } else {
                imageViewLessonIcon.setImageResource(android.R.drawable.ic_media_play);
            }

            // Show completion status
            if (lesson.isCompleted()) {
                imageViewCompletionStatus.setVisibility(View.VISIBLE);
                imageViewCompletionStatus.setImageResource(android.R.drawable.ic_menu_agenda);
            } else {
                imageViewCompletionStatus.setVisibility(View.GONE);
            }
        }

        private String formatLessonDuration(int minutes) {
            if (minutes <= 0) {
                return "0s";
            }
            
            // Since API returns videoDuration in minutes, but display shows seconds format
            // Convert minutes to appropriate format
            if (minutes >= 60) {
                int hours = minutes / 60;
                int remainingMinutes = minutes % 60;
                if (remainingMinutes > 0) {
                    return hours + "h " + remainingMinutes + "m";
                } else {
                    return hours + "h";
                }
            } else if (minutes == 1) {
                // 1 minute = 60 seconds, show as seconds for consistency with UI
                return "60s";
            } else {
                // For small values, show as seconds (assuming most lessons are < 60 minutes)
                return minutes + "s";
            }
        }
    }
} 