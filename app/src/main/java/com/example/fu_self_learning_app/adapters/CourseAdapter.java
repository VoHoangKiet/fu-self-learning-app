package com.example.fu_self_learning_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.Course;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    
    private List<Course> courseList;
    private Context context;
    private OnCourseClickListener onCourseClickListener;

    // Interface để handle click events
    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseAdapter(Context context) {
        this.context = context;
        this.courseList = new ArrayList<>();
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
        notifyDataSetChanged();
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.onCourseClickListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView imageViewThumbnail;
        private ImageView imageViewInstructorAvatar;
        private TextView textViewTitle;
        private TextView textViewInstructor;
        private TextView textViewCategory;
        private TextView textViewLevel;
        private TextView textViewRating;
        private TextView textViewEnrolled;
        private TextView textViewDurationOverlay;
        private TextView textViewPrice;
        private TextView textViewOriginalPrice;
        private TextView textViewBestseller;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageViewThumbnail = itemView.findViewById(R.id.imageViewCourseThumbnail);
            imageViewInstructorAvatar = itemView.findViewById(R.id.imageViewInstructorAvatar);
            textViewTitle = itemView.findViewById(R.id.textViewCourseTitle);
            textViewInstructor = itemView.findViewById(R.id.textViewCourseInstructor);
            textViewCategory = itemView.findViewById(R.id.textViewCourseCategory);
            textViewLevel = itemView.findViewById(R.id.textViewCourseLevel);
            textViewRating = itemView.findViewById(R.id.textViewCourseRating);
            textViewEnrolled = itemView.findViewById(R.id.textViewCourseEnrolled);
            textViewDurationOverlay = itemView.findViewById(R.id.textViewDurationOverlay);
            textViewPrice = itemView.findViewById(R.id.textViewCoursePrice);
            textViewOriginalPrice = itemView.findViewById(R.id.textViewCourseOriginalPrice);
            textViewBestseller = itemView.findViewById(R.id.textViewBestseller);

            // Set click listener cho toàn bộ item
            itemView.setOnClickListener(v -> {
                if (onCourseClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onCourseClickListener.onCourseClick(courseList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Course course) {
            textViewTitle.setText(course.getTitle());
            textViewInstructor.setText(course.getInstructorName());
            textViewCategory.setText(course.getCategory());
            textViewLevel.setText(getLevelText(course.getLevel()));
            
            // Format rating
            textViewRating.setText(String.format(Locale.getDefault(), "%.1f", course.getRating()));
            
            // Format enrolled count with parentheses
            textViewEnrolled.setText(String.format(Locale.getDefault(), "(%,d)", course.getEnrolledCount()));
            
            // Format duration overlay
            int hours = course.getDuration() / 60;
            int minutes = course.getDuration() % 60;
            if (hours > 0) {
                textViewDurationOverlay.setText(String.format(Locale.getDefault(), "%dh %dm", hours, minutes));
            } else {
                textViewDurationOverlay.setText(String.format(Locale.getDefault(), "%dm", minutes));
            }
            
            // Format price in Vietnamese Dong
            if (course.getPrice() == 0) {
                textViewPrice.setText("Free");
                textViewPrice.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                textViewOriginalPrice.setVisibility(View.GONE);
            } else {
                textViewPrice.setText(String.format(Locale.getDefault(), "₫%,.0f", course.getPrice()));
                textViewPrice.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                
                // Show original price if there's a discount (mock logic)
                if (course.getPrice() < 1000000) { // If price is less than 1M, show original price
                    double originalPrice = course.getPrice() * 1.5;
                    textViewOriginalPrice.setText(String.format(Locale.getDefault(), "₫%,.0f", originalPrice));
                    textViewOriginalPrice.setVisibility(View.VISIBLE);
                    // Add strikethrough effect
                    textViewOriginalPrice.setPaintFlags(textViewOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    textViewOriginalPrice.setVisibility(View.GONE);
                }
            }
            
            // Show bestseller badge for highly rated courses
            if (course.getRating() >= 4.5 && course.getEnrolledCount() > 1000) {
                textViewBestseller.setVisibility(View.VISIBLE);
            } else {
                textViewBestseller.setVisibility(View.GONE);
            }

            // Load course thumbnail using Picasso
            String imageUrl = course.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(imageViewThumbnail);
            } else {
                imageViewThumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Load instructor avatar
            loadInstructorAvatar(course);
        }

        private String getLevelText(String level) {
            switch (level.toLowerCase()) {
                case "beginner":
                    return "Cơ bản";
                case "intermediate":
                    return "Trung bình";
                case "advanced":
                    return "Nâng cao";
                default:
                    return level;
            }
        }

        private void loadInstructorAvatar(Course course) {
            if (course.getInstructor() != null && 
                course.getInstructor().getAvatarUrl() != null && 
                !course.getInstructor().getAvatarUrl().isEmpty()) {
                
                String avatarUrl = course.getInstructor().getAvatarUrl();
                
                // Load avatar using Picasso
                Picasso.get()
                    .load(avatarUrl)
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(imageViewInstructorAvatar);
            } else {
                // Show default avatar if no URL available
                imageViewInstructorAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            }
        }
    }
} 