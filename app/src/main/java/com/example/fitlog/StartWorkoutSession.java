package com.example.fitlog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.fitlog.model.Template;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StartWorkoutSession extends Fragment {

    private static final String TAG = "StartWorkoutSession";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start_workout_session, container, false);

        MaterialButton btnStartEmptyWorkout = view.findViewById(R.id.btnStartEmptyWorkout);
        btnStartEmptyWorkout.setOnClickListener(v -> {

            Toast.makeText(requireContext(), "Starting an empty workout", Toast.LENGTH_SHORT).show();
        });

        List<Template> templates = getMockTemplates();
        LinearLayout templateContainer = view.findViewById(R.id.templateContainer);

        if (templateContainer == null) {
            Log.e(TAG, "Template container is null");
            return view;
        }

        // Get the device width
        DisplayMetrics displayMetrics = requireContext().getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;

        for (Template template : templates) {
            View templateView = LayoutInflater.from(requireContext()).inflate(R.layout.template_item, templateContainer, false);

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

            menuIcon.setOnClickListener(v -> showPopupMenu(v, template));

            templateContainer.addView(templateView);
        }

        List<Template> exampleTemplates = getExampleTemplates();
        GridLayout exampleTemplateContainer = view.findViewById(R.id.exampleTemplateContainer);

        if (exampleTemplateContainer == null) {
            Log.e(TAG, "Example template container is null");
            return view;
        }

        for (Template template : exampleTemplates) {
            View templateView = LayoutInflater.from(requireContext()).inflate(R.layout.template_item, exampleTemplateContainer, false);

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

            menuIcon.setOnClickListener(v -> showPopupMenu(v, template));

            exampleTemplateContainer.addView(templateView);
        }

        return view;
    }

    private List<Template> getMockTemplates() {
        List<Template> templates = new ArrayList<>();
        Template upperBody = new Template(1, 1, "Upper Body Workout", "Chest, shoulders, and arms exercises", "private", LocalDateTime.now().minusDays(3));
        upperBody.setLastUsed(LocalDateTime.now().minusDays(3));
        templates.add(upperBody);

        Template lowerBody = new Template(2, 1, "Lower Body Workout", "Legs and glutes exercises", "private", LocalDateTime.now().minusWeeks(1));
        lowerBody.setLastUsed(LocalDateTime.now().minusWeeks(1));
        templates.add(lowerBody);

        return templates;
    }

    private List<Template> getExampleTemplates() {
        List<Template> templates = new ArrayList<>();
        Template fullBody = new Template(3, 0, "Full Body Workout", "Complete body workout for beginners", "public", LocalDateTime.now().minusDays(5));
        fullBody.setLastUsed(LocalDateTime.now().minusDays(5));
        templates.add(fullBody);

        Template hiit = new Template(4, 0, "HIIT Cardio", "High-intensity interval training", "public", LocalDateTime.now().minusDays(2));
        hiit.setLastUsed(LocalDateTime.now().minusDays(2));
        templates.add(hiit);

        Template core = new Template(5, 0, "Core Strength", "Abdominal and lower back exercises", "public", LocalDateTime.now().minusDays(1));
        core.setLastUsed(LocalDateTime.now().minusDays(1));
        templates.add(core);

        Template yoga = new Template(6, 0, "Yoga Flow", "Relaxing yoga session", "public", LocalDateTime.now().minusDays(4));
        yoga.setLastUsed(LocalDateTime.now().minusDays(4));
        templates.add(yoga);

        return templates;
    }

    private void showPopupMenu(View view, Template template) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.template_item_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                Toast.makeText(requireContext(), "Edit " + template.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_duplicate) {
                Toast.makeText(requireContext(), "Duplicate " + template.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_delete) {
                Toast.makeText(requireContext(), "Delete " + template.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
}