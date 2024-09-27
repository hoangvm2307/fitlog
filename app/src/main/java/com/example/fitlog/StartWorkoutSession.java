package com.example.fitlog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class StartWorkoutSession extends AppCompatActivity {

    private static final String TAG = "StartWorkoutSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_workout_session);

        List<Template> templates = getMockTemplates();
        LinearLayout templateContainer = findViewById(R.id.templateContainer);

        if (templateContainer == null) {
            Log.e(TAG, "Template container is null");
            return;
        }

        // Get the device width
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;

        for (Template template : templates) {
            View templateView = LayoutInflater.from(this).inflate(R.layout.template_item, templateContainer, false);

            if (templateView == null) {
                Log.e(TAG, "Template view is null");
                continue;
            }

            TextView title = templateView.findViewById(R.id.templateTitle);
            TextView description = templateView.findViewById(R.id.templateDescription);
            TextView lastUsed = templateView.findViewById(R.id.templateLastUsed);
            ImageView menuIcon = templateView.findViewById(R.id.menuIcon);

            if (title == null || description == null || lastUsed == null || menuIcon == null) {
                Log.e(TAG, "One of the template view components is null");
                continue;
            }

            title.setText(template.getTitle());
            description.setText(template.getDescription());
            lastUsed.setText(template.getLastUsed());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (deviceWidth * 0.45),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8); // Add margin between items
            templateView.setLayoutParams(params);

//            menuIcon.setOnClickListener(v -> showPopupMenu(v, template));

            templateContainer.addView(templateView);
        }

        List<Template> exampleTemplates = getExampleTemplates();
        GridLayout exampleTemplateContainer = findViewById(R.id.exampleTemplateContainer);

        if (exampleTemplateContainer == null) {
            Log.e(TAG, "Example template container is null");
            return;
        }

        for (Template template : exampleTemplates) {
            View templateView = LayoutInflater.from(this).inflate(R.layout.template_item, exampleTemplateContainer, false);

            if (templateView == null) {
                Log.e(TAG, "Template view is null");
                continue;
            }

            TextView title = templateView.findViewById(R.id.templateTitle);
            TextView description = templateView.findViewById(R.id.templateDescription);
            TextView lastUsed = templateView.findViewById(R.id.templateLastUsed);
            ImageView menuIcon = templateView.findViewById(R.id.menuIcon);

            if (title == null || description == null || lastUsed == null || menuIcon == null) {
                Log.e(TAG, "One of the template view components is null");
                continue;
            }

            title.setText(template.getTitle());
            description.setText(template.getDescription());
            lastUsed.setText(template.getLastUsed());

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.setMargins(8, 8, 8, 8); // Add margin between items
            templateView.setLayoutParams(params);

//            menuIcon.setOnClickListener(v -> showPopupMenu(v, template));

            exampleTemplateContainer.addView(templateView);
        }
    }

//    private void showPopupMenu(View view, Template template) {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        popupMenu.inflate(R.menu.template_item_menu);
//        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, template));
//        popupMenu.show();
//    }

//    private boolean onMenuItemClick(MenuItem item, Template template) {
//        switch (item.getItemId()) {
//            case R.id.action_edit:
//                Toast.makeText(this, "Edit " + template.getTitle(), Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.action_duplicate:
//                Toast.makeText(this, "Duplicate " + template.getTitle(), Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.action_delete:
//                Toast.makeText(this, "Delete " + template.getTitle(), Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return false;
//        }
//    }

    private List<Template> getMockTemplates() {
        List<Template> templates = new ArrayList<>();
        templates.add(new Template("Upper workout", "Comprehensive upper body workout focusing on chest, shoulders, and arms. Includes both compound and isolation exercises for maximum muscle engagement.", "8 days ago"));
        templates.add(new Template("Lower workout", "Intense lower body session targeting quads, hamstrings, glutes, and calves. Incorporates both strength and endurance exercises for a well-rounded leg day.", "5 days ago"));
        return templates;
    }

    private List<Template> getExampleTemplates() {
        List<Template> templates = new ArrayList<>();
        templates.add(new Template("Full body", "Full body workout for beginners.", "3 days ago"));
        templates.add(new Template("Cardio workout", "Cardio workout for endurance.", "5 days ago"));
        templates.add(new Template("Strength training", "Strength training for muscle gain.", "7 days ago"));
        templates.add(new Template("Yoga session", "Yoga session for flexibility.", "2 days ago"));
        return templates;
    }

    private static class Template {
        private final String title;
        private final String description;
        private final String lastUsed;

        public Template(String title, String description, String lastUsed) {
            this.title = title;
            this.description = description;
            this.lastUsed = lastUsed;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getLastUsed() {
            return lastUsed;
        }
    }
}