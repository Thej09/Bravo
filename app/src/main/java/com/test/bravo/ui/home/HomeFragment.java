package com.test.bravo.ui.home;

import com.test.bravo.ui.home.DateSelector.*;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.test.bravo.R;

import com.test.bravo.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private LinearLayout categoryContainer;
    private Button createCategoryButton;

    private final String[] suggestedCategories = {
            "Push", "Pull", "Legs", "Biceps", "Triceps",
            "Quadriceps", "Hamstrings", "Core", "Chest",
            "Shoulders", "Back"
    };

    private final int[] availableColors = {
            Color.parseColor("#f57629"),
            Color.parseColor("#F44336"),
            Color.parseColor("#E91E62"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#673AB7"),
            Color.parseColor("#b9f64c"),
            Color.parseColor("#38F387"),
            Color.parseColor("#31EADF"),
            Color.parseColor("#B393f1"),
            Color.parseColor("#c92ef5")
    };

    private String selectedCategoryName;
    private int selectedColor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set the date to the banner text
        setActionBarTitleWithDate();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize UI elements
        categoryContainer = root.findViewById(R.id.category_container);
        createCategoryButton = root.findViewById(R.id.create_category_button);

//         Set up button listener
        createCategoryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateCategoryDialog();
            }
        });

        return root;
    }

    private void setActionBarTitleWithDate() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            int day = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(new Date()));
            String dayWithSuffix = DateSelector.getDayWithSuffix(day);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            String currentDate = dayWithSuffix + " " + sdf.format(new Date());
            activity.getSupportActionBar().setTitle(currentDate);
        }
    }

    private void showCreateCategoryDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_create_category, null);
//
//        // Initialize dialog views
        Spinner categorySpinner = dialogView.findViewById(R.id.category_name_spinner);
        GridLayout colorGridLayout = dialogView.findViewById(R.id.color_grid_layout);
        Button doneButton = dialogView.findViewById(R.id.done_button);

//         Set up the category spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, suggestedCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Initialize selected values
        selectedCategoryName = suggestedCategories[0];
        selectedColor = availableColors[0];
////
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                selectedCategoryName = suggestedCategories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                // Default selection
                selectedCategoryName = suggestedCategories[0];
            }
        });
//
//        // Set up color selection
        for (int i = 0; i < colorGridLayout.getChildCount(); i++) {
            View colorView = colorGridLayout.getChildAt(i);
            final int color = availableColors[i];
            colorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedColor = color;
                    // Optionally, highlight the selected color
                    highlightSelectedColor(colorGridLayout, color);
                }
            });
        }
//
//         Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

//        // Handle the done button
        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addCategoryToUI(selectedCategoryName, selectedColor);

            }
        });

        dialog.show();
    }

    private void highlightSelectedColor(GridLayout colorGridLayout, int selectedColor) {
        // Iterate through all child views and update their appearance based on selection
        for (int i = 0; i < colorGridLayout.getChildCount(); i++) {
            View colorView = colorGridLayout.getChildAt(i);
            if (availableColors[i] == selectedColor) {
                colorView.setAlpha(0.4f);
            } else {
                colorView.setAlpha(1.0f);
            }
        }
    }

    private void addCategoryToUI(String categoryName, int color) {
        // Create a parent LinearLayout for the category item
        LinearLayout categoryLayout = new LinearLayout(getContext());
        categoryLayout.setOrientation(LinearLayout.VERTICAL);
        categoryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        categoryLayout.setPadding(8, 8, 8, 8);
        categoryLayout.setBackgroundColor(Color.TRANSPARENT);

        // Create a ConstraintLayout for the colored rectangle and its contents
        ConstraintLayout colorRectangleLayout = new ConstraintLayout(getContext());
        LinearLayout.LayoutParams rectParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Match parent width
                100); // Height can remain fixed or be adjusted
        colorRectangleLayout.setLayoutParams(rectParams);
        colorRectangleLayout.setBackgroundColor(color);
        colorRectangleLayout.setPadding(8, 8, 8, 16);

        // Create and configure the TextView
        TextView categoryText = new TextView(getContext());
        categoryText.setText(categoryName);
        categoryText.setTextSize(18);
        categoryText.setTextColor(Color.WHITE);
        categoryText.setId(View.generateViewId()); // Give an ID to position it in ConstraintLayout

        // Create and configure the Button
        Button addButton = new Button(getContext());
        addButton.setText("+");
        addButton.setOnClickListener(v -> showAddExerciseDialog(categoryLayout, color));
        addButton.setTextColor(Color.WHITE);
        addButton.setId(View.generateViewId()); // Give an ID to position it in ConstraintLayout
        addButton.setBackgroundColor(color);

        // Add the TextView and Button to the ConstraintLayout
        colorRectangleLayout.addView(categoryText);
        colorRectangleLayout.addView(addButton);

        // Set up constraints for categoryText
        ConstraintLayout.LayoutParams textParams = (ConstraintLayout.LayoutParams) categoryText.getLayoutParams();
        textParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.leftMargin = 16; // Optional: Add some left margin for better spacing
        categoryText.setLayoutParams(textParams);

        // Set up constraints for addButton
        ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams) addButton.getLayoutParams();
        buttonParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.rightMargin = 16; // Optional: Add some right margin for better spacing
        addButton.setLayoutParams(buttonParams);

        // Add the ConstraintLayout to the parent categoryLayout
        categoryLayout.addView(colorRectangleLayout);

        // Finally, add the categoryLayout to the category container
        categoryContainer.addView(categoryLayout, categoryContainer.getChildCount() - 1);; // Add to top
    }

    private void showAddExerciseDialog(LinearLayout categoryLayout, int color) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_exercise, null);

        EditText exerciseNameInput = dialogView.findViewById(R.id.exercise_name_input);
        Spinner setTypeSpinner = dialogView.findViewById(R.id.set_type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.set_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setTypeSpinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setPositiveButton("Done", (dialog, which) -> {
            String exerciseName = exerciseNameInput.getText().toString();
            String setType = setTypeSpinner.getSelectedItem().toString();
            addExerciseToCategory(categoryLayout, exerciseName, setType, color);
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private int tapCounter = 1;

    private void addExerciseToCategory(LinearLayout categoryLayout, String exerciseName, String setType, int categoryColor) {
        // Create the horizontal LinearLayout for the exercise
        LinearLayout exerciseLayout = new LinearLayout(getContext());
        exerciseLayout.setOrientation(LinearLayout.HORIZONTAL);
        exerciseLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        exerciseLayout.setPadding(8, 16, 8, 0);

        // Create the container for the exercise name with rounded corners
        TextView exerciseText = new TextView(getContext());
        exerciseText.setText(exerciseName + "\n(" + setType + ")\n");
        exerciseText.setTextSize(16);
        exerciseText.setTextColor(Color.WHITE);
        exerciseText.setGravity(Gravity.CENTER_VERTICAL);
        exerciseText.setPadding(16, 16, 16, 16);

        // Set the background color and rounded corners for the exerciseText
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(categoryColor); // Use the same color as the category
        backgroundDrawable.setCornerRadius(16); // Set the corner radius for rounded corners
        exerciseText.setBackground(backgroundDrawable);

        // Set the layout parameters for the exerciseText
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f); // Weight = 1 to take up remaining space
        exerciseText.setLayoutParams(textParams);

        // Create the CheckBox
        CheckBox exerciseCheckBox = new CheckBox(getContext());
        exerciseCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Set an OnCheckedChangeListener to change the alpha when the CheckBox is checked
        exerciseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                exerciseCheckBox.setText(String.valueOf(tapCounter));
                backgroundDrawable.setAlpha(128); // Set alpha to 0.5 (128 out of 255)
                tapCounter++;
            } else {
                backgroundDrawable.setAlpha(255); // Set alpha back to 1.0
                tapCounter--;
            }
            exerciseText.setBackground(backgroundDrawable);
        });

        // Add the exerciseText and CheckBox to the exerciseLayout
        exerciseLayout.addView(exerciseText);
        exerciseLayout.addView(exerciseCheckBox);

        // Add the exerciseLayout under the category layout
        categoryLayout.addView(exerciseLayout);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

