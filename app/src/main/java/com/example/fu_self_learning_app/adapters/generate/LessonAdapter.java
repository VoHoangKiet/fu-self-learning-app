package com.example.fu_self_learning_app.adapters.generate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.generate.GeneratedLesson;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    Context context;
    List<GeneratedLesson> lessonList;

    public LessonAdapter(Context context, List<GeneratedLesson> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView textLessonTitle, textLessonDescription;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textLessonTitle = itemView.findViewById(R.id.textLessonTitle);
            textLessonDescription = itemView.findViewById(R.id.textLessonDescription);
        }
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.instructor_lesson_item, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        GeneratedLesson lesson = lessonList.get(position);
        holder.textLessonTitle.setText(lesson.getTitle());
        holder.textLessonDescription.setText(lesson.getDescription());
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }
}
