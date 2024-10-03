package com.example.fitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.model.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private Context context;
    private OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
      void onExerciseClick(Exercise exercise);
  }
    public ExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void updateExercises(List<Exercise> newExercises) {
        this.exercises = newExercises;
        notifyDataSetChanged();
    }

class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView exerciseImage;
        TextView exerciseName;
        TextView bodyPart;
        TextView exerciseDetails;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.exerciseImage);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            bodyPart = itemView.findViewById(R.id.bodyPart);
            exerciseDetails = itemView.findViewById(R.id.exerciseDetails);

            itemView.setOnClickListener(v -> {
              int position = getAdapterPosition();
              if (position != RecyclerView.NO_POSITION) {
                  listener.onExerciseClick(exercises.get(position));
              }
          });
        }

        void bind(Exercise exercise) {
            // Lấy ID của hình ảnh từ tên file trong thư mục drawable
            int resourceId = itemView.getContext().getResources().getIdentifier(
                exercise.getImageName(), "drawable", itemView.getContext().getPackageName());
            if (resourceId != 0) {
                exerciseImage.setImageResource(resourceId);
            } else {
                exerciseImage.setImageResource(R.drawable.shrug);
            }
            
            exerciseName.setText(exercise.getName());
            bodyPart.setText(exercise.getBodypart());
            
            // Đặt thông tin chi tiết về bài tập (ví dụ: trọng lượng và số lần lặp lại)
            // Bạn cần triển khai logic để lấy thông tin này
            exerciseDetails.setText("5 kg (x16)");
        }
    }
}