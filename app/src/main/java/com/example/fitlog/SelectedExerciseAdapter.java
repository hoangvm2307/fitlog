package com.example.fitlog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.model.Exercise;

import java.util.List;

public class SelectedExerciseAdapter extends RecyclerView.Adapter<SelectedExerciseAdapter.ViewHolder> {
    private List<Exercise> selectedExercises;
    private OnRemoveClickListener removeListener;

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    public SelectedExerciseAdapter(List<Exercise> selectedExercises, OnRemoveClickListener listener) {
        this.selectedExercises = selectedExercises;
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template_exercise_create, parent, false); // Sử dụng layout của bạn
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = selectedExercises.get(position);

        // Set hình ảnh exercise
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                exercise.getImageName(), "drawable",
                holder.itemView.getContext().getPackageName());
        if (resourceId != 0) {
            holder.exerciseImage.setImageResource(resourceId);
        } else {
            holder.exerciseImage.setImageResource(R.drawable.shrug);
        }

        // Set tên và body part
        holder.exerciseName.setText(exercise.getName());
        holder.bodyPart.setText(exercise.getBodypart());

        // Xử lý sự kiện xóa
        holder.btnClose.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemoveClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedExercises != null ? selectedExercises.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView exerciseImage;
        TextView exerciseName;
        TextView bodyPart;
        AppCompatImageButton btnClose;

        ViewHolder(View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.exerciseImage);
            exerciseName = itemView.findViewById(R.id.exerciseNameTextView);
            bodyPart = itemView.findViewById(R.id.exerciseBodyPartTextView);
            btnClose = itemView.findViewById(R.id.closeButton);
        }
    }
}