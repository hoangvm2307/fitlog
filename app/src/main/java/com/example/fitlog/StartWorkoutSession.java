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

import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.model.Template;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StartWorkoutSession extends Fragment {

    private static final String TAG = "StartWorkoutSession";
    private DatabaseHelper dbHelper;
    private TemplateDAO templateDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());
        templateDAO = new TemplateDAO(dbHelper);
        
        // Insert example templates if they don't exist
        templateDAO.insertExampleTemplatesIfNotExist();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start_workout_session, container, false);

        MaterialButton btnStartEmptyWorkout = view.findViewById(R.id.btnStartEmptyWorkout);
        MaterialButton btnAddTemplate = view.findViewById(R.id.btnAddTemplate);

        List<Template> templates = getUserTemplates();
        LinearLayout templateContainer = view.findViewById(R.id.templateContainer);

        if (templateContainer == null) {
            Log.e(TAG, "Template container is null");
            return view;
        }

        btnAddTemplate.setOnClickListener(v -> openAddTemplate());

        // Get the device width
        DisplayMetrics displayMetrics = requireContext().getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;

        for (Template template : templates) {
            View templateView = inflater.inflate(R.layout.template_item, templateContainer, false);

            TextView title = templateView.findViewById(R.id.templateTitle);
            TextView description = templateView.findViewById(R.id.templateDescription);
            TextView lastUsed = templateView.findViewById(R.id.templateLastUsed);
            ImageView menuIcon = templateView.findViewById(R.id.menuIcon);

            if (title == null || lastUsed == null || menuIcon == null) {
                Log.e(TAG, "One of the template view components is null");
                continue;
            }

            title.setText(template.getTitle());
            description.setText(template.getDescription());

            LocalDateTime lastUsedDate = template.getLastUsed();
            if (lastUsedDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                lastUsed.setText("Last used: " + lastUsedDate.format(formatter));
            } else {
                lastUsed.setText("Never used");
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (deviceWidth * 0.45),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8); // Add margin between items
            templateView.setLayoutParams(params);

            menuIcon.setOnClickListener(v -> showPopupMenu(v, template));

            templateView.setOnClickListener(v -> openTemplateDetail(template.getId()));

            btnStartEmptyWorkout.setOnClickListener(v -> openSessionFragment(template.getId()));

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
            
            // Handle null lastUsed date
            LocalDateTime lastUsedDate = template.getLastUsed();
            if (lastUsedDate != null) {
                lastUsed.setText(lastUsedDate.toString());
            } else {
                lastUsed.setText("Never used");
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.setMargins(8, 8, 8, 8); // Add margin between items
            templateView.setLayoutParams(params);

            menuIcon.setOnClickListener(v -> showPopupMenu(v, template));

            templateView.setOnClickListener(v -> openTemplateDetail(template.getId()));

            exampleTemplateContainer.addView(templateView);
        }

        return view;
    }

    private void openTemplateDetail(int templateId) {
        Fragment fragment = TemplateDetail.newInstance(templateId);
        requireActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }

    private void openSessionFragment(int templateId) {
        Fragment fragment = SessionDetails.newInstance(templateId);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openAddTemplate() {
        Fragment fragment = CreateTemplate.newInstance();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openUpdateTemplate(int templateId) {
        Fragment fragment = TemplateUpdateFragment.newInstance(templateId);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private List<Template> getUserTemplates() {
        // Fetch user templates from the database
        return templateDAO.getTemplatesByUserId(1); // Assuming user ID 1 for now
    }

    private List<Template> getExampleTemplates() {
        // Fetch example templates from the database
        return templateDAO.getExampleTemplates();
    }

    private void showPopupMenu(View view, Template template) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.template_item_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                openUpdateTemplate(template.getId());
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
