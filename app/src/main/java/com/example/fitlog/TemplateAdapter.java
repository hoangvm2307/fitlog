package com.example.fitlog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.model.Template;

import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    private List<Template> templates;
    private Context context;
    private OnTemplateClickListener listener;

    public interface OnTemplateClickListener {
      void onTemplateClick(Template template);
    }

    public TemplateAdapter(List<Template> templates, OnTemplateClickListener listener) {
        this.templates = templates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        Template template = templates.get(position);
        holder.bind(template);
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public void updateTemplates(List<Template> newTemplates) {
        this.templates.clear();
        this.templates.addAll(newTemplates);
        notifyDataSetChanged();
    }

    class TemplateViewHolder extends RecyclerView.ViewHolder {
        ImageView templateImage;
        TextView templateName;
        TextView bodyPart;
        TextView templateDetails;

        TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            templateImage = itemView.findViewById(R.id.templateImage);
            templateName = itemView.findViewById(R.id.templateName);
            bodyPart = itemView.findViewById(R.id.bodyPart);
            templateDetails = itemView.findViewById(R.id.templateDetails);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTemplateClick(templates.get(position));
                }
            });
        }

        void bind(Template template) {
            // Lấy ID của hình ảnh từ tên file trong thư mục drawable
            int resourceId = itemView.getContext().getResources().getIdentifier(
                template.getImageName(), "drawable", itemView.getContext().getPackageName());
            if (resourceId != 0) {
                templateImage.setImageResource(resourceId);
            } else {
                templateImage.setImageResource(R.drawable.shrug);
            }
            
            templateName.setText(template.getName());
            bodyPart.setText(template.getBodypart());
            
            // Đặt thông tin chi tiết về bài tập (ví dụ: trọng lượng và số lần lặp lại)
            // Bạn cần triển khai logic để lấy thông tin này
            templateDetails.setText("5 kg (x16)");
        }
    }
}