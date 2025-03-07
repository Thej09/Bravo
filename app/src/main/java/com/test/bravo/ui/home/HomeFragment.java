package com.test.bravo.ui.home;

import static android.content.Context.MODE_PRIVATE;

import com.test.bravo.services.TimerReceiver;
import com.test.bravo.services.TimerService;
import com.test.bravo.ui.home.DateSelector.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.test.bravo.model.Category;
import com.test.bravo.model.Exercise;
import com.test.bravo.database.DatabaseHelper;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Lifecycle;
import androidx.core.view.MenuProvider;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Calendar;

import com.test.bravo.R;

import com.test.bravo.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private LinearLayout categoryContainer;
    private LinearLayout todoContainer;
    private Button createCategoryButton;
    private Map<String, Category> categoryMap = new HashMap<>();
    private DatabaseHelper databaseHelper;
    private List<Exercise> completedExercises;
    private String currentDateGlobal;
    private String selectedDateGlobal;

    private final String[] suggestedCategories = {
            "Push", "Pull", "Legs", "Biceps", "Triceps",
            "Quadriceps", "Hamstrings", "Core", "Chest",
            "Shoulders", "Back"
    };

    private final int[] availableColors = {
            Color.parseColor("#FD4500"),
            Color.parseColor("#FF0033"),
            Color.parseColor("#FF007F"),
            Color.parseColor("#9B30FF"),
            Color.parseColor("#6B10FF"),
            Color.parseColor("#0039A6"),
            Color.parseColor("#0057FF"),
            Color.parseColor("#20C68F"),
            Color.parseColor("#00C851"),
            Color.parseColor("#00985E")
    };

    private String selectedCategoryName;
    private int selectedColor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        databaseHelper = new DatabaseHelper(getContext());

//          HERE
        categoryMap = databaseHelper.loadDataFromDatabase();


//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        TextView dateText = toolbar.findViewById(R.id.date_text);
        ImageView dateSelectorButton = toolbar.findViewById(R.id.date_selector_button);


        Calendar calendar = Calendar.getInstance();
        String dayWithOrdinal = getDayWithOrdinal(calendar.get(Calendar.DAY_OF_MONTH));
        String mmm = new SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.getTime());
        String formattedDate = mmm + " " + dayWithOrdinal + ", " + calendar.get(Calendar.YEAR);
        dateText.setText(formattedDate);

//        Typeface boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
//        dateText.setTypeface(boldTypeface);

        // Set a click listener for the date selector button
        dateSelectorButton.setOnClickListener(v -> {
            // Get the current date

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Update the date text with the selected date
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);

                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        String mmm_ = new SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.getTime());
                        String formattedDate_ = mmm_ + " " + getDayWithOrdinal(selectedDay) + ", " + selectedYear;

                        dateText.setText(formattedDate_);
//                        dateText.setTypeface(boldTypeface);


                        // Perform an action based on the selected date
                        onDateSelected(selectedDate);
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.show();
        });


        completedExercises = getCompletedExercises();

        dateText.setOnClickListener(v -> {
            dateText.setTypeface(null, Typeface.BOLD_ITALIC); // Make it bold and italic
            dateText.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> {
                dateText.animate().scaleX(1f).scaleY(1f).setDuration(100); // Reset size
//                dateText.setTypeface(boldTypeface);
            });
            dateText.setTypeface(null, Typeface.NORMAL);
            showCompletedExercisesPopup();
        });


        ImageView collapseAllButton = toolbar.findViewById(R.id.collapse_all_button);
        collapseAllButton.setOnClickListener(v -> collapseAllCategories());

        ImageView expandAllButton = toolbar.findViewById(R.id.expand_all_button);
        expandAllButton.setOnClickListener(v ->expandAllCategories());

        // Initialize UI elements
        categoryContainer = root.findViewById(R.id.category_container);
        createCategoryButton = root.findViewById(R.id.create_category_button);
        todoContainer = root.findViewById(R.id.exercises_todo);

        if (categoryMap.isEmpty()) {
//            System.out.println("The categoryMap is empty.");
            loadCategoriesFromJSON();
        } else {
            loadViewFromMap();
        }


        // Get the current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        currentDateGlobal = currentDate;
        selectedDateGlobal = currentDate;

//        onNewDay();

        // Retrieve the last opened date from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String lastOpenedDate = sharedPreferences.getString("lastOpenedDate", "");
        Log.e("DateTag",currentDate);
        Log.e("DateTag",lastOpenedDate);

        if (!currentDate.equals(lastOpenedDate)) {
            onNewDay();
            Log.e("DateTag","Diff Date");
            saveLastOpenedDate(currentDate); // Save the new date
        }


        fillTodoExercises();


//         Set up button listener
        createCategoryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateCategoryDialog();
            }
        });

        ImageView todaysExercisesMenu = root.findViewById(R.id.todays_exercises_menu);
        todaysExercisesMenu.setOnClickListener(v -> {
            // Create a PopupMenu

            if (!(isDateGreater(currentDateGlobal, selectedDateGlobal))) {

                PopupMenu popupMenu = new PopupMenu(requireContext(), todaysExercisesMenu);

                // Add menu items
                popupMenu.getMenu().add("Add Exercise");
                popupMenu.getMenu().add("Clear Exercises");
                popupMenu.getMenu().add("Save Routine");
                popupMenu.getMenu().add("Load Routine");
                popupMenu.getMenu().add("Delete Routine");

                // Set a click listener for menu item clicks
                popupMenu.setOnMenuItemClickListener(item -> {
                    String selectedOption = item.getTitle().toString();

                    switch (selectedOption) {
                        case "Add Exercise":
                            // Call the method to handle adding an exercise
                            //showAddExerciseDialog(null, 0); // Pass appropriate arguments
                            Toast.makeText(requireContext(), "Add Exercise", Toast.LENGTH_SHORT).show();
                            break;

                        case "Clear Exercises":
                            // Call the method to clear all exercises
                            clearExercises();
//                            Toast.makeText(requireContext(), "All exercises cleared!", Toast.LENGTH_SHORT).show();
                            break;

                        case "Save Routine":
                            // Call the method to save the routine
                            showSaveRoutineDialog();
                            break;

                        case "Load Routine":
                            // Call the method to load a routine
                            //loadRoutine();
                            List<String> routineNames = databaseHelper.getAllRoutineNames();
                            if (routineNames.isEmpty()) {
                                Toast.makeText(requireContext(), "There are no saved routines!", Toast.LENGTH_SHORT).show();
                            } else {
                                showLoadRoutineDialog(routineNames);
                            }
                            break;

                        case "Delete Routine":
                            // Call the method to delete a routine
                            //deleteRoutine();
                            deleteRoutine();
//                            Toast.makeText(requireContext(), "Deleted Routine!", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            return false;
                    }

                    return true;
                });

                // Show the menu
                popupMenu.show();
            }
        });

        return root;
    }

    private void onNewDay() {
        // Get today's date in "yyyy-MM-dd" format
        resetAllExercises();
        databaseHelper.deletePlannedExercisesBeforeDate(currentDateGlobal);
        List<Exercise> plannedExercises = databaseHelper.getPlannedExercises(currentDateGlobal);
        completedExercises = getFullExercises(plannedExercises);
        setCheckBoxes(completedExercises);
        databaseHelper.saveDataToDatabase(categoryMap);

    }

    private void saveLastOpenedDate(String currentDate) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastOpenedDate", currentDate);
        editor.apply();
    }

    private void resetAllExercises() {
        // Reset completedExercises list
        for (Exercise exercise : completedExercises) {
            exercise.setCompleted(false);
            exercise.setCompletionTime(0);
        }
        completedExercises.clear();

        // Reset all exercises in categoryMap
        for (Category category : categoryMap.values()) {
            for (Exercise exercise : category.getExercises()) {
                exercise.setDoneToday(false);
                exercise.setDoneTime(0);
            }
        }

        // Save the updated data back to the database
        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }

        loadViewFromMap();
        Log.i("ResetAllExercises", "All exercises have been reset.");
    }

    private void fillTodoExercises(){
        if (completedExercises.size() == 0){
            TextView noExercisesTextView;

            if (isDateGreater(currentDateGlobal, selectedDateGlobal)){
                Log.e("LogicTag", "Outer");
                if (databaseHelper.getDailyActivity(selectedDateGlobal).size() == 0){
                    Log.e("LogicTag", "Inner");
                    noExercisesTextView= noExercisesText();
                    todoContainer.addView(noExercisesTextView, 1);
                }
            } else {

                noExercisesTextView= noExercisesText();
                todoContainer.addView(noExercisesTextView, 1);
            }

        } else{
            for (Exercise exercise: completedExercises){
                addExerciseToParent(exercise, todoContainer);
            }
        }
    }

    private void deleteRoutine() {
        // Get all routine names from the database
        List<String> routineNames = databaseHelper.getAllRoutineNames();

        if (routineNames.isEmpty()) {
            Toast.makeText(requireContext(), "No routines available to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select a Routine to Delete");

        CharSequence[] routinesArray = routineNames.toArray(new CharSequence[0]);

        builder.setItems(routinesArray, (dialog, which) -> {
            // Get the selected routine name
            String selectedRoutine = routineNames.get(which);

            // Show a confirmation dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Routine")
                    .setMessage("Are you sure you want to delete the routine \"" + selectedRoutine + "\"?")
                    .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                        // Delete the selected routine
                        databaseHelper.deleteRoutine(selectedRoutine);
                        Toast.makeText(requireContext(), "Routine \"" + selectedRoutine + "\" deleted.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void saveRoutine(String routineName){
        List<String[]> formattedExercises = convertExercisesToList(completedExercises);
        databaseHelper.saveRoutine(routineName, formattedExercises);
        for (String[] pair : formattedExercises) {
            Log.d("PrintTAG", "[" + pair[0] + ", " + pair[1] + "]");
        }
    }

    private void showSaveRoutineDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Save Routine");

        // Create an EditText for the user to input the routine name
        FrameLayout frameLayout = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 0, 10, 0); // Add margins to center it more
        params.gravity = Gravity.CENTER_HORIZONTAL; // Align horizontally in the dialog
        final EditText input = new EditText(requireContext());
        input.setHint("Enter routine name");
        input.setLayoutParams(params);

        // Add the EditText to the FrameLayout
        frameLayout.addView(input);
        builder.setView(frameLayout);

        // Set "OK" button behavior
        builder.setPositiveButton("OK", (dialog, which) -> {
            String routineName = input.getText().toString().trim();

            if (routineName.isEmpty()) {
                Toast.makeText(requireContext(), "Routine name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if a routine with the same name exists

            boolean routineExists = databaseHelper.checkIfRoutineExists(routineName);

            if (routineExists) {
                // Show confirmation to overwrite
                new AlertDialog.Builder(requireContext())
                        .setTitle("Routine Already Exists")
                        .setMessage("A routine with this name already exists. Would you like to overwrite it?")
                        .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                            saveRoutine(routineName);
                            Toast.makeText(requireContext(), "Routine overwritten successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                // Save the new routine
                saveRoutine(routineName);
                Toast.makeText(requireContext(), "Routine saved successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set "Cancel" button behavior
        builder.setNegativeButton("Cancel", null);

        // Show the dialog
        builder.show();
    }

    private void showLoadRoutineDialog(List<String> routineNames) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Load Routine");

        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        // Create the dialog here so it's accessible inside the OnClickListener
        AlertDialog dialog = builder.create();

        for (String routineName : routineNames) {
            TextView routineTextView = new TextView(requireContext());
            routineTextView.setText(routineName);
            routineTextView.setTextSize(16);
            routineTextView.setPadding(10, 10, 10, 10);
            routineTextView.setOnClickListener(v -> {
                // On selecting a routine
                dialog.dismiss();
                 loadRoutine(routineName);
                Toast.makeText(requireContext(), "Loaded Routine: " + routineName, Toast.LENGTH_SHORT).show();
            });
            layout.addView(routineTextView);
        }
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setNegativeButton("Cancel", null);
        dialog.setView(scrollView); // Set the scrollView content
        dialog.show();
    }

    private void loadRoutine(String routineName) {
        // Get the routine exercises from the database
        List<String[]> routineExercises = databaseHelper.loadRoutine(routineName);

        if (routineExercises.isEmpty()) {
            Toast.makeText(requireContext(), "Routine not found!", Toast.LENGTH_SHORT).show();
            return;
        }


        // Loop through the routine's exercises
        for (String[] exerciseCategory : routineExercises) {
            String exerciseName = exerciseCategory[0]; // Exercise name
            String categoryName = exerciseCategory[1]; // Category name

            // Check if the category exists in the category map
            if (categoryMap.containsKey(categoryName)) {
                Category category = categoryMap.get(categoryName);

                Exercise curExercise = category.getExercise(exerciseName);
                if (curExercise != null){
                    if (!curExercise.getDoneToday()){
                        completedExercises.add(curExercise);
                        curExercise.setDoneToday(true);
                        databaseHelper.savePlannedExercise(selectedDateGlobal, curExercise);
                    }
                }
            }
        }


        refreshTodoList(todoContainer, completedExercises);
        loadViewFromMap();
        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }
    }

    private void clearExercises() {

        if (completedExercises.isEmpty()) {
            Toast.makeText(requireContext(), "No completed exercises to clear.", Toast.LENGTH_SHORT).show();
            return;
        }


        for (Exercise exercise : completedExercises) {
            // Set doneToday to false
            exercise.setDoneToday(false);


            databaseHelper.deletePlannedExercise(selectedDateGlobal, exercise.getName());
        }


        completedExercises.clear();


        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }


        refreshTodoList(todoContainer, completedExercises);
        loadViewFromMap();

        // Notify the user
        Toast.makeText(requireContext(), "Exercises cleared successfully!", Toast.LENGTH_SHORT).show();
    }

    private void addExerciseToParent(Exercise exercise, LinearLayout parent) {

        LinearLayout exerciseLayout = new LinearLayout(getContext());
        exerciseLayout.setOrientation(LinearLayout.HORIZONTAL);
        exerciseLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        exerciseLayout.setPadding(8, 16, 8, 0);
        exerciseLayout.setTag(exercise.getName());

        LinearLayout exerciseContainer = new LinearLayout(getContext());
        exerciseContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        exerciseContainer.setLayoutParams(containerParams);
        exerciseContainer.setTag(exercise.getName());

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(availableColors[exercise.getCategoryClr()]); // Use the category color
        backgroundDrawable.setCornerRadius(16); // Rounded corners
        exerciseContainer.setBackground(backgroundDrawable);

        TextView exerciseText = new TextView(getContext());
        exerciseText.setText(exercise.getName());
        exerciseText.setTextSize(16);
        exerciseText.setTextColor(Color.parseColor("#EDEDED"));
        exerciseText.setGravity(Gravity.CENTER_VERTICAL);
        exerciseText.setPadding(16, 16, 16, 0);

        View whiteLine = new View(getContext());
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                3 // Height in dp
        );
        lineParams.setMargins(16, 0, 16, 16);
        whiteLine.setLayoutParams(lineParams);
        whiteLine.setBackgroundColor(Color.parseColor("#FFFFFF"));

        // Table stuff
        TableLayout tableLayout = new TableLayout(getContext());
        tableLayout.setTag("exerciseTable");
        tableLayout.setStretchAllColumns(false);

        String setType = exercise.getSetType();

        if (setType.equals("Weight x Reps")) {
            // Create the first row (Weight)
            TableRow weightRow = new TableRow(getContext());
            TextView weightLabel = new TextView(getContext());
            weightLabel.setText("Weight");
            weightLabel.setTextColor(Color.parseColor("#EDEDED"));
            weightRow.addView(weightLabel);

            if (exercise.getWeightReps().size() > 0){
                for (int i = 0; i < exercise.getWeightReps().size(); i++) {
                    TextView weightCell = new TextView(getContext());
                    weightCell.setText(padToFourCharacters(String.valueOf((Object) exercise.getWeightReps().get(i)[0])));
                    weightCell.setTextColor(Color.parseColor("#EDEDED"));
                    weightCell.setPadding(64, 0, 0, 0);
                    weightRow.addView(weightCell);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    TextView weightCell = new TextView(getContext());
                    weightCell.setText(padToFourCharacters(String.valueOf("0")));
                    weightCell.setTextColor(Color.parseColor("#EDEDED"));
                    weightCell.setPadding(64, 0, 0, 0);
                    weightRow.addView(weightCell);
                }
            }



            // Create the second row (Reps)
            TableRow repsRow = new TableRow(getContext());
            TextView repsLabel = new TextView(getContext());
            repsLabel.setText("Reps");
            repsLabel.setTextColor(Color.parseColor("#EDEDED"));
            repsRow.addView(repsLabel);

            if (exercise.getWeightReps().size() > 0){
                for (int i = 0; i < exercise.getWeightReps().size(); i++) {
                    TextView repsCell = new TextView(getContext());
                    repsCell.setText(String.valueOf((Object) exercise.getWeightReps().get(i)[1]));
                    repsCell.setTextColor(Color.parseColor("#EDEDED"));
                    repsCell.setPadding(64, 0, 0, 0);
                    repsRow.addView(repsCell);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    TextView repsCell = new TextView(getContext());
                    repsCell.setText(padToFourCharacters(String.valueOf("0")));
                    repsCell.setTextColor(Color.parseColor("#EDEDED"));
                    repsCell.setPadding(64, 0, 0, 0);
                    repsRow.addView(repsCell);
                }
            }

            // Add both rows to the table
            tableLayout.addView(weightRow);
            tableLayout.addView(repsRow);

        } else if (setType.equals("Duration")) {
            // Create a single row for Duration
            TableRow durationRow = new TableRow(getContext());
            TextView durationLabel = new TextView(getContext());
            durationLabel.setText("Duration");
            durationLabel.setTextColor(Color.parseColor("#EDEDED"));
            durationRow.addView(durationLabel);


            if (exercise.getDurations().size() > 0){
                for (int i = 0; i < exercise.getDurations().size(); i++) {
                    TextView durationCell = new TextView(getContext());
                    durationCell.setText(String.format(String.valueOf((Object) exercise.getDurations().get(i))));
                    durationCell.setTextColor(Color.parseColor("#EDEDED"));
                    durationCell.setPadding(64, 0, 0, 0);
                    durationRow.addView(durationCell);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    TextView durationCell = new TextView(getContext());
                    durationCell.setText(padToFourCharacters(String.valueOf("0")));
                    durationCell.setTextColor(Color.parseColor("#EDEDED"));
                    durationCell.setPadding(64, 0, 0, 0);
                    durationRow.addView(durationCell);
                }
            }

            TableRow durationRow2 = new TableRow(getContext());
            TextView durationLabel2 = new TextView(getContext());
            durationLabel2.setText("(Seconds)");
            durationLabel2.setTextColor(Color.parseColor("#EDEDED"));
            durationRow2.addView(durationLabel2);

            // Add the row to the table
            tableLayout.addView(durationRow);
            tableLayout.addView(durationRow2);
        }

        tableLayout.setGravity(Gravity.CENTER_VERTICAL);
        tableLayout.setPadding(32, 0, 32, 24);

        // Add table logic

        exerciseContainer.addView(exerciseText);
        exerciseContainer.addView(whiteLine);
        exerciseContainer.addView(tableLayout);

        // Check box logic
        CheckBox exerciseCheckBox = new CheckBox(getContext());
        exerciseCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (exercise.getCompleted()) {
            backgroundDrawable.setAlpha(192);
            exerciseCheckBox.setChecked(true);
        } else {
            backgroundDrawable.setAlpha(255);
            exerciseCheckBox.setChecked(false);
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        exerciseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (currentDateGlobal.equals(selectedDateGlobal)) {
                    backgroundDrawable.setAlpha(192);
                    exercise.setCompleted(true);
                    databaseHelper.saveDailyActivity(currentDate, exercise, exercise.getCategoryName(), exercise.getCategoryClr());
                } else {
                    databaseHelper.savePlannedExercise(selectedDateGlobal, exercise);
                    exerciseCheckBox.setChecked(false);
                }
            } else {
                if (currentDateGlobal.equals(selectedDateGlobal)) {
                    backgroundDrawable.setAlpha(255);
                    exercise.setCompleted(false);
                    databaseHelper.deleteDailyActivity(currentDate, exercise.getName());
                }
            }
            exerciseContainer.setBackground(backgroundDrawable);
            if (currentDateGlobal.equals(selectedDateGlobal)) {
                databaseHelper.saveDataToDatabase(categoryMap);
            }
        });

        // Create the option icon (ImageView)
        ImageView optionIcon = new ImageView(getContext());
        optionIcon.setImageResource(android.R.drawable.ic_menu_revert); // Use a built-in Android icon or your custom icon
        int iconSize = (int) (36 * getResources().getDisplayMetrics().density); // Example: 24dp size
        optionIcon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));

        optionIcon.setPadding(0, 16, 12, 16);
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            optionIcon.setColorFilter(Color.WHITE); // Set color filter to white for dark mode
        } else {
            optionIcon.setColorFilter(null); // Remove color filter for light mode (or set to default color if needed)
        }

        optionIcon.setOnClickListener(v -> {
            String exerciseName = exercise.getName(); // Assuming you have the `exercise` object available
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Remove Exercise")
                    .setMessage("Remove " + exerciseName + " from today's exercises?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Remove the exercise from today's list

                        databaseHelper.deletePlannedExercise(selectedDateGlobal, exerciseName);



                        exercise.setDoneToday(false);
                        completedExercises.remove(exercise);
                        refreshTodoList(todoContainer, completedExercises);
                        dialog.dismiss();
                        loadViewFromMap();
                        if (currentDateGlobal.equals(selectedDateGlobal)) {
                            databaseHelper.saveDataToDatabase(categoryMap);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Dismiss the dialog
                        dialog.dismiss();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

            // Align buttons to bottom right
            dialog.getWindow().setGravity(Gravity.CENTER);
        });


        LinearLayout verticalLayout = new LinearLayout(getContext());
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        verticalLayout.addView(exerciseCheckBox);
        verticalLayout.addView(optionIcon);

        exerciseContainer.setOnClickListener(v -> showExerciseDetailsPopup(exercise, exercise.getCategoryName(), exercise.getCategoryClr()));
        exerciseLayout.addView(exerciseContainer);
        exerciseLayout.addView(verticalLayout);


        // Add the exerciseLayout to the parent
        TextView allExercisesTitle = parent.findViewById(R.id.all_exercise_title);

        if (allExercisesTitle != null) {
            int index = parent.indexOfChild(allExercisesTitle);
            parent.addView(exerciseLayout, index);
        } else {
            parent.addView(exerciseLayout);
        }

    }

    private void refreshTodoList(LinearLayout todoContainer, List<Exercise> completedExercises) {
        // Remove all exercise layouts in the todoContainer
        for (int i = todoContainer.getChildCount() - 1; i >= 0; i--) {
            View child = todoContainer.getChildAt(i);

            // Check if the view is a LinearLayout (exercise layout) and has a tag (set in addExerciseToParent)
            if (child instanceof LinearLayout && child.getTag() != null) {
                todoContainer.removeViewAt(i);
            }

            if (child instanceof TextView && child.getTag() != null) {
                todoContainer.removeViewAt(i);
            }
        }

        if (completedExercises.size() == 0) {
            // Add a centered TextView saying "No exercises added yet"
            TextView noExercisesTextView;

            if (isDateGreater(currentDateGlobal, selectedDateGlobal)){
                Log.e("LogicTag", "Outer");
                if (databaseHelper.getDailyActivity(selectedDateGlobal).size() == 0){
                    Log.e("LogicTag", "Inner");
                    noExercisesTextView= noExercisesText();
                    todoContainer.addView(noExercisesTextView, 1);
                }
            } else {

                noExercisesTextView= noExercisesText();
                todoContainer.addView(noExercisesTextView, 1);
            }

        } else {
            // Add each exercise in completedExercises to the todoContainer
            for (Exercise exercise : completedExercises) {
                addExerciseToParent(exercise, todoContainer);
            }
        }
    }

    private TextView noExercisesText(){
        TextView noExercisesTextView = new TextView(getContext());

        if (!currentDateGlobal.equals(selectedDateGlobal)){
            if (isDateGreater(selectedDateGlobal, currentDateGlobal)) {
                noExercisesTextView.setText("No exercises planned for " + selectedDateGlobal);
            } else {
                noExercisesTextView.setText("No exercises completed on " + selectedDateGlobal);
            }
        } else {
            noExercisesTextView.setText("No exercises added for today");
        }
        noExercisesTextView.setTextSize(16);
        noExercisesTextView.setTextColor(Color.GRAY);
        noExercisesTextView.setGravity(Gravity.CENTER);
        noExercisesTextView.setTag("NoExercises");
        noExercisesTextView.setPadding(0,46,0,0);

        // Set layout parameters to center the TextView within the parent
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.gravity = Gravity.CENTER;

        noExercisesTextView.setLayoutParams(layoutParams);
        return noExercisesTextView;
    }

    private String getDayWithOrdinal(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1:
                return day + "st";
            case 2:
                return day + "nd";
            case 3:
                return day + "rd";
            default:
                return day + "th";
        }
    }

    private void onDateSelected(String selectedDate) {
        // Display a toast with the selected date
//        Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

        // Get the current date in the same format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        selectedDateGlobal = convertDateFormat(selectedDate);
        LinearLayout exercisesTodoLayout = getView().findViewById(R.id.exercises_todo);
        TextView allExerciseTitle = getView().findViewById(R.id.all_exercise_title);
        TextView exerciseTitle = getView().findViewById(R.id.todays_exercises_title);

        // Compare the selected date with the current date
        if (selectedDate.equals(currentDate)) {
            // If the selected date is the current date, make categoryContainer visible
            categoryContainer.setVisibility(View.VISIBLE);
//            exercisesTodoLayout.setVisibility(View.VISIBLE);
            allExerciseTitle.setText("All Exercises");
            exerciseTitle.setText("Today's Exercises");
            categoryMap = databaseHelper.loadDataFromDatabase();
            loadViewFromMap();
            completedExercises = getCompletedExercises();
            refreshTodoList(todoContainer, completedExercises);
        } else {
            // Otherwise, hide the categoryContainer

            if (isDateGreater(selectedDateGlobal, currentDateGlobal)) {
                categoryContainer.setVisibility(View.VISIBLE);
//                exercisesTodoLayout.setVisibility(View.VISIBLE);
                List<Exercise> plannedExercises = databaseHelper.getPlannedExercises(selectedDateGlobal);
                setCheckBoxes(plannedExercises);
                exerciseTitle.setText("Planned Exercises");
                allExerciseTitle.setText("All Exercises");
                completedExercises = getFullExercises(plannedExercises);
                loadViewFromMap();
                refreshTodoList(todoContainer, completedExercises);
            }
            else {

//                exercisesTodoLayout.setVisibility(View.INVISIBLE);
                allExerciseTitle.setText("");
                exerciseTitle.setText("Completed Exercises");
                categoryContainer.setVisibility(View.INVISIBLE);
                completedExercises = new ArrayList<>();
                refreshTodoList(todoContainer, completedExercises);
                addExerciseContainersToLayout(databaseHelper.getDailyActivity(selectedDateGlobal), exercisesTodoLayout);
            }
        }
    }

    private String convertDateFormat(String inFormat){

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String outDate = "";
        try {
            Date date = inputFormat.parse(inFormat);
            outDate = outputFormat.format(date);
        } catch (ParseException e) {
            System.err.println("Invalid date format: " + inFormat);
        }
        return outDate;
    }

    public boolean isDateGreater(String date1, String date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse the dates
            Date parsedDate1 = dateFormat.parse(date1);
            Date parsedDate2 = dateFormat.parse(date2);

            // Compare the dates
            if (parsedDate1 != null && parsedDate2 != null) {
                return parsedDate1.after(parsedDate2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false; // Return false in case of an error
    }

    private List<Exercise> getCompletedExercises() {
        List<Exercise> completedList = new ArrayList<>();
        for (Category category : categoryMap.values()) {
            for (Exercise exercise : category.getExercises()) {
                if (exercise.getDoneToday()) {
                    completedList.add(exercise);
                }
            }
        }

        // Sort by timestamp to preserve the order of completion
        Collections.sort(completedList, (e1, e2) -> Long.compare(e1.getDoneTime(), e2.getDoneTime()));
        return completedList;
    }

    private List<Exercise> getFullExercises(List<Exercise> inputExercises) {
        List<Exercise> completeExercises = new ArrayList<>();

        // Loop through the input exercises
        for (Exercise inputExercise : inputExercises) {
            // Extract the exercise name
            String exerciseName = inputExercise.getName();

            // Loop through the categoryMap
            for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
                Category category = entry.getValue();

                // Check if the category contains the exercise
                if (category.hasExercise(exerciseName)) {
                    // Add the matching exercise to the complete list
                    completeExercises.add(category.getExercise(exerciseName));
                    category.getExercise(exerciseName).setSetType(inputExercise.getSetType());
                    category.getExercise(exerciseName).setWeightReps(inputExercise.getWeightReps());
                    category.getExercise(exerciseName).setDurations(inputExercise.getDurations());

                    break; // Break out of the loop once the exercise is found
                }
            }
        }
        Collections.sort(completeExercises, Comparator.comparingLong(Exercise::getCompletionTime));
        return completeExercises;
    }

    private void setCheckBoxes(List<Exercise> inputExercises) {

        Set<String> inputExerciseNames = new HashSet<>();
        for (Exercise inputExercise : inputExercises) {
            inputExerciseNames.add(inputExercise.getName());
        }


        for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
            Category category = entry.getValue();
            for (Exercise exercise : category.getExercises()) {

                if (inputExerciseNames.contains(exercise.getName())) {
                    exercise.setDoneToday(true);
                } else {
                    exercise.setDoneToday(false);
                    exercise.setDoneTime(0);
                }
                exercise.setCompleted(false);
                exercise.setCompletionTime(0);
            }
        }
    }


    private void collapseAllCategories() {
        for (Category category : categoryMap.values()) {
            category.setIsCollapsed(true); // Update state
        }
        loadViewFromMap();
        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }
    }

    private void expandAllCategories() {
        for (Category category : categoryMap.values()) {
            category.setIsCollapsed(false); // Update state
        }
        loadViewFromMap(); // Refresh UI
        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }
    }

    private void showCompletedExercisesPopup() {
        if (completedExercises.isEmpty()) {
            Toast.makeText(getContext(), "No exercises completed yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a popup view
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        TypedValue typedValue = new TypedValue();
        Context context = getContext();
        context.getTheme().resolveAttribute(R.attr.SecondaryTextColor, typedValue, true);
        int primaryColor = typedValue.data;

        // Create a custom TextView for the title
        TextView titleView = new TextView(getContext());
        titleView.setText("Completed Exercises");
        titleView.setTextSize(20); // Set desired text size
        titleView.setTextColor(primaryColor); // Set text color to primary
        titleView.setPadding(20, 20, 20, 20); // Optional: Add padding
        titleView.setGravity(Gravity.CENTER); // Optional: Center the title
        Typeface boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
        titleView.setTypeface(boldTypeface);

// Set the custom title view
        builder.setCustomTitle(titleView);

        // Create a container for the exercises
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Add each completed exercise as a numbered TextView
        for (int i = 0; i < completedExercises.size(); i++) {
            Exercise exercise = completedExercises.get(i);

            TextView exerciseView = new TextView(getContext());
            exerciseView.setText((i + 1) + ". " + exercise.getName());
            exerciseView.setTextSize(16);
            exerciseView.setPadding(32, 16, 0, 0);

            exerciseView.setTextColor(primaryColor);
            layout.addView(exerciseView);
        }

        scrollView.addView(layout);

        // Set the scrollable layout as the dialog content
        builder.setView(scrollView);

        // Add a dismiss button
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = requireContext().getAssets().open("preload.json"); // Make sure you add your JSON file in the assets folder
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void loadCategoriesFromJSON() {
        try {
            // Load the JSON data as a string
            String jsonData = loadJSONFromAsset();
            assert jsonData != null;
//            Log.i("myTag", jsonData);
//            Log.i("myTag", "HI");

            // First, parse the root JSON object
            JSONObject jsonObject = new JSONObject(jsonData);

            // Then, get the "categories" array from the JSON object
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
//            Log.i("myTag", "HI after parsing categories array");

            // Loop through the categories array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject categoryObject = jsonArray.getJSONObject(i);
                String categoryName = categoryObject.getString("name");
                int color = Integer.parseInt(categoryObject.getString("color"));
//                Log.i("myTag", "Category: " + categoryName);
                // Add category to UI (you can call your method here)
                LinearLayout categoryLayout = addCategoryToUI(categoryName, color, false);

                // Load exercises within this category
                JSONArray exercisesArray = categoryObject.getJSONArray("exercises");
                for (int j = 0; j < exercisesArray.length(); j++) {
                    JSONObject exerciseObject = exercisesArray.getJSONObject(j);
                    String exerciseName = exerciseObject.getString("name");
                    String setType = exerciseObject.getString("setType");

                    // Add exercise to the category UI
//                    Log.i("myTag", "Exercise: " + exerciseName + ", Set Type: " + setType);
                    addExerciseToCategory(categoryLayout, exerciseName, setType, color);
                }
            }

        } catch (JSONException e) {
            Log.e("myTag", "JSON exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadViewFromMap() {
        assert !categoryMap.isEmpty();

//        categoryContainer.removeAllViews();
        // Remove all children of categoryContainer except the last one (e.g., "Create Category" button)
        int childCount = categoryContainer.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            categoryContainer.removeViewAt(0); // Always remove the first view until the last remains
        }


        // Convert the category names (keys) to a list and sort it alphabetically
        List<String> sortedCategoryNames = new ArrayList<>(categoryMap.keySet());

        // Sort the list based on the color value of each category
        Collections.sort(sortedCategoryNames, (name1, name2) -> {
            Category category1 = categoryMap.get(name1);
            Category category2 = categoryMap.get(name2);

            if (category1 != null && category2 != null) {
                return Integer.compare(category1.getColor(), category2.getColor());
            } else {
                // Handle potential null categories (if unlikely, you can skip this check)
                return 0;
            }
        });

        // Iterate through the sorted category names
        for (String categoryName : sortedCategoryNames) {
            Category category = categoryMap.get(categoryName);
            assert category != null;
            int color = category.getColor();
            Log.i("myTag", categoryName);
            Log.i("myTag", String.valueOf(color));


            boolean isCollapsed = category.getIsCollapsed();

            // Add category to the UI
            LinearLayout categoryLayout = addCategoryToUI(categoryName, color, isCollapsed);


            // Load exercises within this category
            List<Exercise> exercises = category.getExercises();
            for (int j = 0; j < exercises.size(); j++) {
                Exercise curExercise = exercises.get(j);
                String exerciseName = curExercise.getName();
                String setType = curExercise.getSetType();

                // Add exercise to the category UI
                addExerciseToCategory(categoryLayout, exerciseName, setType, color);
            }

            View colorRectangleLayout = categoryLayout.findViewWithTag("colorRectangleLayout");
            ImageView collapseExpandButton = categoryLayout.findViewWithTag("collapseExpandButton");

//            Log.i("myTag2", "Category: " + categoryName + ", IsCollapsed: " + category.getIsCollapsed());
            for (Exercise exercise : category.getExercises()) {
                Log.i("myTag2", "Exercise Name: " + exercise.getName() + " Collapsed: " + category.getIsCollapsed());
            }

            // Show/hide the exercise views based on collapse state
            for (int i = 0; i < categoryLayout.getChildCount(); i++) {
                View child = categoryLayout.getChildAt(i);



                // Skip the color rectangle header (where the button is)
                if (child != colorRectangleLayout) {
                    child.setVisibility(category.getIsCollapsed() ? View.GONE : View.VISIBLE);
                }
            }

            collapseExpandButton.setImageResource(category.getIsCollapsed() ? android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float);


        }
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
        selectedColor = 0;
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
            final int color = i;
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

                if (categoryMap.containsKey(selectedCategoryName)) {
                    // Category already exists, handle this case (e.g., show a toast or alert)
                    Toast.makeText(getContext(), "Category already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    // Category does not exist, proceed to add
                    addCategoryToUI(selectedCategoryName, selectedColor, false);
                }

                loadViewFromMap();

            }
        });

        dialog.show();
    }

    private void highlightSelectedColor(GridLayout colorGridLayout, int selectedColor) {
        // Iterate through all child views and update their appearance based on selection
        for (int i = 0; i < colorGridLayout.getChildCount(); i++) {
            View colorView = colorGridLayout.getChildAt(i);
            if (availableColors[i] == availableColors[selectedColor]) {
                colorView.setAlpha(0.4f);
            } else {
                colorView.setAlpha(1.0f);
            }
        }
    }

    private LinearLayout addCategoryToUI(String categoryName, int color, boolean isCollapsed_) {

        // Add to category map
        Category newCategory;



        // Add to category map
        if (!categoryMap.containsKey(categoryName)) {
            newCategory = new Category(categoryName, color); // Declare newCategory here
            Log.i("myTag", "Values Added to Map: " + categoryName + ", " + String.valueOf(color));
            categoryMap.put(categoryName, newCategory);
        } else {
            newCategory = categoryMap.get(categoryName);
        }


        // Create a parent LinearLayout for the category item
        LinearLayout categoryLayout = new LinearLayout(getContext());
        categoryLayout.setOrientation(LinearLayout.VERTICAL);
        categoryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        categoryLayout.setPadding(8, 8, 8, 8);
        categoryLayout.setBackgroundColor(Color.TRANSPARENT);

        // Set Tag
        categoryLayout.setTag(categoryName);

        // Create a ConstraintLayout for the colored rectangle and its contents
        ConstraintLayout colorRectangleLayout = new ConstraintLayout(getContext());
        LinearLayout.LayoutParams rectParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Match parent width
                100); // Height can remain fixed or be adjusted
        colorRectangleLayout.setLayoutParams(rectParams);

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(availableColors[color]); // Use category color
        backgroundDrawable.setCornerRadius(16); // Set corner radius
        colorRectangleLayout.setBackground(backgroundDrawable);

        colorRectangleLayout.setPadding(8, 8, 8, 16);
        colorRectangleLayout.setId(View.generateViewId());
        colorRectangleLayout.setTag("colorRectangleLayout");

        // Create and configure the TextView
        TextView categoryText = new TextView(getContext());
        categoryText.setText(categoryName);
        categoryText.setTextSize(18);
        categoryText.setTextColor(Color.WHITE);
        Typeface boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
        categoryText.setTypeface(boldTypeface);

        categoryText.setId(View.generateViewId()); // Give an ID to position it in ConstraintLayout

        // Create and configure the Button
        ImageView addButton = new ImageView(getContext());
        addButton.setImageResource(android.R.drawable.ic_input_add); // Use built-in Android "+" icon
        addButton.setColorFilter(Color.WHITE);
        addButton.setOnClickListener(v -> showAddExerciseDialog(categoryLayout, color));
        addButton.setId(View.generateViewId()); // Give an ID to position it in ConstraintLayout

        // Menu Button
        ImageView ellipsisMenuButton = new ImageView(getContext());
        ellipsisMenuButton.setImageResource(android.R.drawable.ic_menu_edit); // Use built-in Android "more" icon
        ellipsisMenuButton.setColorFilter(Color.WHITE);
        ellipsisMenuButton.setPadding(16, 20, 8, 20);
        ellipsisMenuButton.setId(View.generateViewId());
        ellipsisMenuButton.setTag("ellipsisMenuButton");

        // Add a click listener for the ellipsis menu button
        ellipsisMenuButton.setOnClickListener(v -> {
            // Show a menu or perform actions here
            PopupMenu popupMenu = new PopupMenu(getContext(), ellipsisMenuButton);
            popupMenu.getMenu().add("Rename Category");
            popupMenu.getMenu().add("Recolour Category");
            popupMenu.getMenu().add("Delete Category");
            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedOption = item.getTitle().toString();
                if (selectedOption.equals("Rename Category")) {
                    renameCategory(categoryName);
                } else if (selectedOption.equals("Recolour Category")) {
                    recolourCategory(categoryName);
                } else if (selectedOption.equals("Delete Category")) {
                    deleteCategory(categoryName);
                }
                return true;
            });
            popupMenu.show();
        });


        // Collapse/Expand Button
        ImageView collapseExpandButton = new ImageView(getContext());
        collapseExpandButton.setImageResource(android.R.drawable.arrow_up_float); // Use down arrow for expanded state
        collapseExpandButton.setColorFilter(Color.WHITE);
        collapseExpandButton.setPadding(16, 20, 16, 20);
        collapseExpandButton.setId(View.generateViewId());
        collapseExpandButton.setTag("collapseExpandButton");

        newCategory.setIsCollapsed(isCollapsed_);
        collapseExpandButton.setImageResource(isCollapsed_ ? android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float);

        // Handle collapse/expand functionality
        collapseExpandButton.setOnClickListener(v -> {
            boolean isCollapsed = newCategory.getIsCollapsed(); // Get the current state

            // Toggle the collapsed state
            newCategory.setIsCollapsed(!isCollapsed);
            Log.i("myTag3", "Collapsed: " + newCategory.getIsCollapsed());


            // Show/hide the exercise views based on collapse state
            for (int i = 0; i < categoryLayout.getChildCount(); i++) {
                View child = categoryLayout.getChildAt(i);

                // Skip the color rectangle header (where the button is)
                if (child != colorRectangleLayout) {
                    child.setVisibility(newCategory.getIsCollapsed() ? View.GONE : View.VISIBLE);
                }
            }

            // Change the icon based on the state
            collapseExpandButton.setImageResource(newCategory.getIsCollapsed() ? android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float);
        });

        // Add the TextView and Button to the ConstraintLayout
        colorRectangleLayout.addView(categoryText);
        colorRectangleLayout.addView(addButton);
        colorRectangleLayout.addView(collapseExpandButton);
        colorRectangleLayout.addView(ellipsisMenuButton);

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
//        buttonParams.rightToLeft  = collapseExpandButton.getId();
        buttonParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.rightMargin = 16; // Optional: Add some right margin for better spacing
        addButton.setLayoutParams(buttonParams);

        // Set up constraints for collapseExpandButton
        ConstraintLayout.LayoutParams collapseExpandParams = (ConstraintLayout.LayoutParams) collapseExpandButton.getLayoutParams();
//        collapseExpandParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        collapseExpandParams.rightToLeft = addButton.getId(); // Position next to the categoryText
        collapseExpandParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        collapseExpandParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        collapseExpandParams.rightMargin = 16; // Optional
        collapseExpandButton.setLayoutParams(collapseExpandParams);

        ConstraintLayout.LayoutParams ellipsisParams = (ConstraintLayout.LayoutParams) ellipsisMenuButton.getLayoutParams();
        ellipsisParams.rightToLeft = collapseExpandButton.getId(); // Position to the left of collapseExpandButton
        ellipsisParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        ellipsisParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        ellipsisParams.rightMargin = 16; // Optional: Add some right margin for spacing
        ellipsisMenuButton.setLayoutParams(ellipsisParams);


        // Add the ConstraintLayout to the parent categoryLayout
        categoryLayout.addView(colorRectangleLayout);

        // Finally, add the categoryLayout to the category container
        categoryContainer.addView(categoryLayout, categoryContainer.getChildCount() - 1);; // Add to top
        return categoryLayout;
    }

    private void renameCategory(String categoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rename Category");

        final EditText input = new EditText(getContext());
        input.setText(categoryName);
        input.setPadding(64, 16, 64, 16);
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newCategoryName = input.getText().toString().trim();
            if (!newCategoryName.isEmpty() && !categoryMap.containsKey(newCategoryName)) {
                Category category = categoryMap.remove(categoryName);
                categoryMap.put(newCategoryName, category);
                category.setName(newCategoryName);
                if (currentDateGlobal.equals(selectedDateGlobal)) {
                    databaseHelper.saveDataToDatabase(categoryMap);
                }
                loadViewFromMap(); // Refresh the UI
            } else {
                Toast.makeText(getContext(), "Invalid or duplicate name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteCategory(String categoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (categoryMap.containsKey(categoryName)) {
                        categoryMap.remove(categoryName);
                        loadViewFromMap(); // Refresh the UI
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void recolourCategory(String categoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Recolour Category");

        GridLayout colorPicker = new GridLayout(getContext());
        colorPicker.setColumnCount(5);
        colorPicker.setPadding(64, 16, 64, 16);

//        for (int i = 0; i < newExercise.getWeightReps().size(); i++)
        for (int i = 0; i < availableColors.length; i++) {
            int color = availableColors[i];
            final int colour_idx = i;
            View colorView = new View(getContext());
            colorView.setBackgroundColor(color);
            colorView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            colorView.setPadding(2, 2, 2, 2);
            colorView.setOnClickListener(v -> {
                Category category = categoryMap.get(categoryName);
                if (category != null) {
                    category.setColor(colour_idx);
                    loadViewFromMap(); // Refresh the UI
                }
            });
            colorPicker.addView(colorView);
        }

        builder.setView(colorPicker);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
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

            // Get the categoryName from the categoryLayout's tag or other associated data
            String categoryName = (String) categoryLayout.getTag();

            // Retrieve the Category object
            Category category = categoryMap.get(categoryName);

            // Check if the exercise already exists in the category's exercise list
            if (category != null && category.hasExercise(exerciseName)) {
                // Exercise already exists, handle this case (e.g., show a toast or alert)
                Toast.makeText(getContext(), "Exercise already exists in this category!", Toast.LENGTH_SHORT).show();
            } else {
                // If exercise doesn't exist, add it
                addExerciseToCategory(categoryLayout, exerciseName, setType, color);
                if (currentDateGlobal.equals(selectedDateGlobal)) {
                    databaseHelper.saveDataToDatabase(categoryMap);
                }
            }

        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }


    private void addExerciseToCategory(LinearLayout categoryLayout, String exerciseName, String setType, int categoryColor) {

        // Add exercise in category map
        Category category = categoryMap.get(categoryLayout.getTag());


        if (category != null) {
            // Check if the exercise already exists in the category
            if (!category.hasExercise(exerciseName)) {
                category.addExercise(new Exercise(exerciseName, category, setType));
                Log.i("myTag", "Exercise added: " + exerciseName);
            } else {
                Log.i("myTag", "Exercise already exists in category: " + exerciseName);
            }
        }

        Exercise newExercise = category.getExercise(exerciseName);

        newExercise.setCategoryClr(categoryColor);
        newExercise.setCategoryName((String) categoryLayout.getTag());

        // Create the horizontal LinearLayout for the exercise
        LinearLayout exerciseLayout = new LinearLayout(getContext());
        exerciseLayout.setOrientation(LinearLayout.HORIZONTAL);
        exerciseLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        exerciseLayout.setPadding(8, 16, 8, 0);
        exerciseLayout.setTag(exerciseName);

        // Container for exercise name and table
        LinearLayout exerciseContainer = new LinearLayout(getContext());
        exerciseContainer.setOrientation(LinearLayout.VERTICAL);
        exerciseContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
//        exerciseContainer.setPadding(8, 16, 8, 0);
        exerciseContainer.setTag(exerciseName);


        // Set the background color and rounded corners for the exercise container
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(availableColors[categoryColor]); // Use the same color as the category
        backgroundDrawable.setCornerRadius(16); // Set the corner radius for rounded corners
        exerciseContainer.setBackground(backgroundDrawable);

        // Set the layout parameters for the exerciseContainer
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f); // Weight = 1 to take up remaining space
        exerciseContainer.setLayoutParams(textParams);


        // Create text view for exercise
        TextView exerciseText = new TextView(getContext());
        exerciseText.setText(exerciseName); // Exercise Name only on the first line
        exerciseText.setTextSize(16);
        exerciseText.setTextColor(Color.parseColor("#EDEDED"));
        exerciseText.setGravity(Gravity.CENTER_VERTICAL);
        exerciseText.setPadding(16, 16, 16, 0);

        // Create the white line (divider)
        View whiteLine = new View(getContext());

        // Set the layout parameters with margin
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Full width
                3 // Height in dp (1-2 dp should be enough for a line)
        );
        lineParams.setMargins(16, 0, 16, 16); // Add margin (left, top, right, bottom)

        whiteLine.setLayoutParams(lineParams);
        whiteLine.setBackgroundColor(Color.parseColor("#FFFFFF"));


        // Create the table based on setType
        TableLayout tableLayout = new TableLayout(getContext());
        tableLayout.setTag("exerciseTable");
        tableLayout.setStretchAllColumns(false); // Make all columns fill the table's width

        if (setType.equals("Weight x Reps")) {
            // Create the first row (Weight)
            TableRow weightRow = new TableRow(getContext());
            TextView weightLabel = new TextView(getContext());
            weightLabel.setText("Weight");
            weightLabel.setTextColor(Color.parseColor("#EDEDED"));
            weightRow.addView(weightLabel);

            if (newExercise.getWeightReps().size() > 0){
                for (int i = 0; i < newExercise.getWeightReps().size(); i++) {
                    TextView weightCell = new TextView(getContext());
                    weightCell.setText(padToFourCharacters(String.valueOf((Object) newExercise.getWeightReps().get(i)[0])));
                    weightCell.setTextColor(Color.parseColor("#EDEDED"));
                    weightCell.setPadding(64, 0, 0, 0);
                    weightRow.addView(weightCell);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    TextView weightCell = new TextView(getContext());
                    weightCell.setText(padToFourCharacters(String.valueOf("0")));
                    weightCell.setTextColor(Color.parseColor("#EDEDED"));
                    weightCell.setPadding(64, 0, 0, 0);
                    weightRow.addView(weightCell);
                }
            }



            // Create the second row (Reps)
            TableRow repsRow = new TableRow(getContext());
            TextView repsLabel = new TextView(getContext());
            repsLabel.setText("Reps");
            repsLabel.setTextColor(Color.parseColor("#EDEDED"));
            repsRow.addView(repsLabel);

            if (newExercise.getWeightReps().size() > 0){
                for (int i = 0; i < newExercise.getWeightReps().size(); i++) {
                    TextView repsCell = new TextView(getContext());
                    repsCell.setText(String.valueOf((Object) newExercise.getWeightReps().get(i)[1]));
                    repsCell.setTextColor(Color.parseColor("#EDEDED"));
                    repsCell.setPadding(64, 0, 0, 0);
                    repsRow.addView(repsCell);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    TextView repsCell = new TextView(getContext());
                    repsCell.setText(padToFourCharacters(String.valueOf("0")));
                    repsCell.setTextColor(Color.parseColor("#EDEDED"));
                    repsCell.setPadding(64, 0, 0, 0);
                    repsRow.addView(repsCell);
                }
            }

            // Add both rows to the table
            tableLayout.addView(weightRow);
            tableLayout.addView(repsRow);

        } else if (setType.equals("Duration")) {
            // Create a single row for Duration
            TableRow durationRow = new TableRow(getContext());
            TextView durationLabel = new TextView(getContext());
            durationLabel.setText("Duration");
            durationLabel.setTextColor(Color.parseColor("#EDEDED"));
            durationRow.addView(durationLabel);


            if (newExercise.getDurations().size() > 0){
                for (int i = 0; i < newExercise.getDurations().size(); i++) {
                    TextView durationCell = new TextView(getContext());
                    durationCell.setText(String.format(String.valueOf((Object) newExercise.getDurations().get(i))));
                    durationCell.setTextColor(Color.parseColor("#EDEDED"));
                    durationCell.setPadding(64, 0, 0, 0);
                    durationRow.addView(durationCell);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    TextView durationCell = new TextView(getContext());
                    durationCell.setText(padToFourCharacters(String.valueOf("0")));
                    durationCell.setTextColor(Color.parseColor("#EDEDED"));
                    durationCell.setPadding(64, 0, 0, 0);
                    durationRow.addView(durationCell);
                }
            }

            TableRow durationRow2 = new TableRow(getContext());
            TextView durationLabel2 = new TextView(getContext());
            durationLabel2.setText("(Seconds)");
            durationLabel2.setTextColor(Color.parseColor("#EDEDED"));
            durationRow2.addView(durationLabel2);

            // Add the row to the table
            tableLayout.addView(durationRow);
            tableLayout.addView(durationRow2);
        }

        tableLayout.setGravity(Gravity.CENTER_VERTICAL);
        tableLayout.setPadding(32, 0, 32, 24);

        exerciseContainer.addView(exerciseText);
        exerciseContainer.addView(whiteLine);
        exerciseContainer.addView(tableLayout);
        exerciseContainer.setOnClickListener(v -> showExerciseDetailsPopup(newExercise, category.getName(), categoryColor));


        // Create a vertical LinearLayout for the CheckBox and the option icon
        LinearLayout verticalLayout = new LinearLayout(getContext());
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create the CheckBox
        CheckBox exerciseCheckBox = new CheckBox(getContext());
        exerciseCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));


        exerciseCheckBox.setChecked(newExercise.getDoneToday());
        if (newExercise.getDoneToday()){
            backgroundDrawable.setAlpha(128);
        } else {
            backgroundDrawable.setAlpha(255);
        }

        // Set an OnCheckedChangeListener to change the alpha when the CheckBox is checked
        exerciseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String categoryName = (String) categoryLayout.getTag();

            if (isChecked) {
                backgroundDrawable.setAlpha(128); // Set alpha to 0.5 (128 out of 255)
                newExercise.setDoneToday(true);

                if (!completedExercises.contains(newExercise)) {
                    completedExercises.add(newExercise);
                }
                databaseHelper.savePlannedExercise(selectedDateGlobal, newExercise);

            } else {
                backgroundDrawable.setAlpha(255); // Set alpha back to 1.0
                newExercise.setDoneToday(false);

                completedExercises.remove(newExercise);
                databaseHelper.deletePlannedExercise(selectedDateGlobal, newExercise.getName());
            }
//            displayCompletedExercises();
            if (currentDateGlobal.equals(selectedDateGlobal)) {
                databaseHelper.saveDataToDatabase(categoryMap);
            }
            exerciseContainer.setBackground(backgroundDrawable);
            refreshTodoList(todoContainer, completedExercises);
        });

        // Create the option icon (ImageView)
        ImageView optionIcon = new ImageView(getContext());
        optionIcon.setImageResource(R.drawable.ic_hamburger_menu); // Use a built-in Android icon or your custom icon
        optionIcon.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        optionIcon.setPadding(10, 16, 16, 16);
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            optionIcon.setColorFilter(Color.WHITE); // Set color filter to white for dark mode
        } else {
            optionIcon.setColorFilter(null); // Remove color filter for light mode (or set to default color if needed)
        }

        // Add an OnClickListener for the option button to show the dialog
        optionIcon.setOnClickListener(v -> showExerciseOptionsDialog(exerciseLayout, exerciseName, categoryLayout));

        // Add the CheckBox and optionIcon to the vertical layout
        verticalLayout.addView(exerciseCheckBox);
        verticalLayout.addView(optionIcon);

        // Add the exerciseText and verticalLayout (CheckBox + Icon) to the exerciseLayout
        exerciseLayout.addView(exerciseContainer);
        exerciseLayout.addView(verticalLayout);

        // Add the exerciseLayout under the category layout
        categoryLayout.addView(exerciseLayout);
    }

    private void showExerciseOptionsDialog(LinearLayout exerciseLayout, String exerciseName, LinearLayout categoryLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Options for " + exerciseName);

        String[] options = {"Delete", "Move to another category"};

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Delete the exercise
                deleteExercise(exerciseLayout, exerciseName, categoryLayout);
                Toast.makeText(getContext(), exerciseName + " deleted!", Toast.LENGTH_SHORT).show();
            } else if (which == 1) {
                // Move the exercise to another category
                showMoveExerciseDialog(exerciseName, categoryLayout);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void deleteExercise(LinearLayout exerciseLayout, String exerciseName, LinearLayout categoryLayout) {
        // Remove the exercise from the UI
        categoryLayout.removeView(exerciseLayout);

        // Remove the exercise from the category map
        String categoryName = (String) categoryLayout.getTag();
        Category category = categoryMap.get(categoryName);

        if (category != null) {
            // Find the exercise object in the category's exercise list
            Exercise exerciseToRemove = null;
            for (Exercise exercise : category.getExercises()) {  // Assuming getExercises() returns a list of Exercise objects
                if (exercise.getName().equals(exerciseName)) {
                    exerciseToRemove = exercise;
                    break;
                }
            }

            // If the exercise is found, remove it
            if (exerciseToRemove != null) {
                category.removeExercise(exerciseToRemove);  // Remove the Exercise object
            } else {
                Toast.makeText(getContext(), "Exercise not found!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showMoveExerciseDialog(String exerciseName, LinearLayout currentCategoryLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Move " + exerciseName + " to another category");

        String setType = categoryMap.get(currentCategoryLayout.getTag()).getExercise(exerciseName).getSetType();
        Log.i("myTag", "SETTYPE:" + setType);

        // List the available categories
        String[] categoryNames = categoryMap.keySet().toArray(new String[0]);

        builder.setItems(categoryNames, (dialog, which) -> {
            String newCategoryName = categoryNames[which];

            if (newCategoryName.equals(currentCategoryLayout.getTag())) {
                Toast.makeText(getContext(), "Exercise is already in this category!", Toast.LENGTH_SHORT).show();
            } else {
                Category newCategory = categoryMap.get(newCategoryName);
                Category curCategory = categoryMap.get(currentCategoryLayout.getTag());
                Exercise exToMove = curCategory.getExercise(exerciseName);

                // Remove from current category
                deleteExercise(currentCategoryLayout.findViewWithTag(exerciseName), exerciseName, currentCategoryLayout);
                newCategory.addExercise(exToMove);

                // Add to the new category
                LinearLayout newCategoryLayout = (LinearLayout) categoryContainer.findViewWithTag(newCategoryName);
                addExerciseToCategory(newCategoryLayout, exerciseName, setType, categoryMap.get(newCategoryName).getColor());

            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void showExerciseDetailsPopup(Exercise exercise, String categoryName, int categoryColor) {

        boolean isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int backgroundColor = isNightMode ? Color.parseColor("#444444") : Color.parseColor("#EEEEEE");

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popupView = inflater.inflate(R.layout.popup_exercise_details, null);

        // Timer stuff
        // Timer initialization
        NumberPicker minutePicker = popupView.findViewById(R.id.minute_picker);
        NumberPicker secondPicker = popupView.findViewById(R.id.second_picker);
        Button startButton = popupView.findViewById(R.id.start_button);
        LinearLayout timerSetupLayout = popupView.findViewById(R.id.timer_setup);
        LinearLayout timerLayout = popupView.findViewById(R.id.timer_layout);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        NumberPicker.Formatter formatter = value -> String.format("%02d", value);
        minutePicker.setFormatter(formatter);
        secondPicker.setFormatter(formatter);

        minutePicker.setValue(exercise.getTimerMins());
        secondPicker.setValue(exercise.getTimerSecs());

        minutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            exercise.setTimerMins(newVal); // Update timerMins in the exercise object
        });

        secondPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            exercise.setTimerSecs(newVal); // Update timerSecs in the exercise object
        });

        // Timer logic v2:

        TextView countdownTextView = new TextView(getContext());
        countdownTextView.setTextSize(24);
        countdownTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        countdownTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        final long[] remainingTime = {minutePicker.getValue() * 60 + secondPicker.getValue()};

        final boolean[] isTimerRunning = {false};
        final boolean[] isTimerPaused = {false};
        CountDownTimer[] timer = {null};

        startButton.setOnClickListener(v -> {

            boolean isTimerSetupActive = timerSetupLayout.isAttachedToWindow();

            // Stop any existing timer
            if (timer[0] != null) {
                timer[0].cancel();
                timer[0] = null;
                isTimerRunning[0] = false;
            }

            // Stop the foreground service
            Intent stopServiceIntent = new Intent(getContext(), TimerService.class);
            getContext().stopService(stopServiceIntent);


            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(1); // Use the same ID as in the TimerService
            }

            if (isTimerSetupActive) {


                if (timer[0] == null) {
                    // Replace timerSetupLayout with countdownTextView
                    timerLayout.removeView(timerSetupLayout);
                    timerLayout.addView(countdownTextView, 0);
                    remainingTime[0] = exercise.getTimerMins() * 60 + exercise.getTimerSecs();
                    startButton.setText("Reset");

                    // Start the TimerService as a foreground service
                    Intent serviceIntent = new Intent(getContext(), TimerService.class);
                    serviceIntent.putExtra("timeInSeconds", remainingTime[0]);
                    ContextCompat.startForegroundService(getContext(), serviceIntent);

                    // Initialize and start CountDownTimer for UI updates
                    timer[0] = new CountDownTimer(remainingTime[0] * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainingTime[0] = millisUntilFinished / 1000;
                            int minutes = (int) (remainingTime[0] / 60);
                            int seconds = (int) (remainingTime[0] % 60);
                            countdownTextView.setText(String.format("%02d:%02d", minutes, seconds));
                        }

                        @Override
                        public void onFinish() {
                            countdownTextView.setText("00:00");
                            Toast.makeText(getContext(), "Timer Finished!", Toast.LENGTH_SHORT).show();
                            isTimerRunning[0] = false;
                            timer[0] = null;

                            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            if (notificationManager != null) {
                                notificationManager.cancel(1);
                            }

                            // Stop the foreground service
                            Intent stopServiceIntent = new Intent(getContext(), TimerService.class);
                            getContext().stopService(stopServiceIntent);

                            // Play sound even when the phone is in silent or vibrate mode
                            MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.ringer_sound); // Replace with your sound file in res/raw folder
                            if (mediaPlayer != null) {
                                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

                                // Check the current audio mode and force alarm stream
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mediaPlayer.setAudioAttributes(
                                            new AudioAttributes.Builder()
                                                    .setUsage(AudioAttributes.USAGE_ALARM) // Set usage to alarm
                                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                    .build()
                                    );
                                } else {
                                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM); // Fallback for older versions
                                }

                                // Start playing the sound
                                mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Release resources after playback
                                mediaPlayer.start();
                            }
                        }
                    };
                    timer[0].start();
                    isTimerRunning[0] = true;
                }
            } else {
                // Remove countdownTextView and show the timer setup layout
                timerLayout.removeView(countdownTextView);
                timerLayout.addView(timerSetupLayout, 0);

                // Reset the remaining time and button text
                remainingTime[0] = exercise.getTimerMins() * 60 + exercise.getTimerSecs();
                startButton.setText("Start");

            }
        });


        // Get references to views in the layout
        LinearLayout header = popupView.findViewById(R.id.header);
        TextView exerciseNameView = popupView.findViewById(R.id.exercise_name);
        TextView categoryNameView = popupView.findViewById(R.id.category_name);
        ImageView closeButton = popupView.findViewById(R.id.close_button);
        TableLayout tableLayout = popupView.findViewById(R.id.mutable_table);
        Button addColumnButton = popupView.findViewById(R.id.add_column_button);
        Button removeColumnButton = popupView.findViewById(R.id.remove_column_button);

        // Initialize notes field
        EditText notesField = popupView.findViewById(R.id.notes_field);
        notesField.setText(exercise.getExerciseNotes());


        String exerciseName = exercise.getName();
        String setType = exercise.getSetType();

        // Set values for the header
        exerciseNameView.setText(exerciseName);
        categoryNameView.setText(categoryName);

        LinearLayout popupLayout = popupView.findViewById(R.id.popup_root); // Use popupView instead of view
        popupLayout.setBackgroundColor(backgroundColor);

        TypedValue typedValue = new TypedValue();
        Context context = getContext();

        context.getTheme().resolveAttribute(R.attr.SecondaryTextColor, typedValue, true);
        int primaryColor = typedValue.data;

        // Set the background color of the popup
        exerciseNameView.setTextColor(primaryColor);
        categoryNameView.setTextColor(primaryColor);
        closeButton.setColorFilter(primaryColor);


        int[] columnCount = {4};

        if (Objects.equals(exercise.getSetType(), "Duration") && !exercise.getDurations().isEmpty()){
            columnCount[0] = exercise.getDurations().size();
        } else if (Objects.equals(exercise.getSetType(), "Weight x Reps") && !exercise.getWeightReps().isEmpty()) {
            columnCount[0] = exercise.getWeightReps().size();
        }

        initializeTable(tableLayout, setType, columnCount[0], exercise);


        addColumnButton.setOnClickListener(v -> {
            if (columnCount[0] < 7) {
                columnCount[0]++;
                initializeTable(tableLayout, setType, columnCount[0], exercise);
            }
        });

        removeColumnButton.setOnClickListener(v -> {
            if (columnCount[0] > 1) { // Ensure at least one column remains
                columnCount[0]--;
                initializeTable(tableLayout, setType, columnCount[0], exercise);
            }

        });

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Close the dialog when the close button is clicked
        closeButton.setOnClickListener(v -> {

            String notes = notesField.getText().toString().trim();
            exercise.setExerciseNotes(notes);

            dialog.dismiss();

            stopTimerAndNotification(timer, isTimerRunning);

            // Refresh the exerciseContainer to reflect updated values
            LinearLayout parentCategoryLayout = (LinearLayout) categoryContainer.findViewWithTag(categoryName);
            LinearLayout exerciseContainer = parentCategoryLayout.findViewWithTag(exercise.getName());
            refreshExerciseContainer(exerciseContainer, exercise, categoryColor);
            refreshTodoList(todoContainer, completedExercises);
        });

// Ensure the timer and notification are stopped if the dialog is dismissed by clicking outside
        dialog.setOnDismissListener(dialogInterface -> {
            String notes = notesField.getText().toString().trim();
            exercise.setExerciseNotes(notes);

            dialog.dismiss();

            stopTimerAndNotification(timer, isTimerRunning);

            // Refresh the exerciseContainer to reflect updated values
            LinearLayout parentCategoryLayout = (LinearLayout) categoryContainer.findViewWithTag(categoryName);
            LinearLayout exerciseContainer = parentCategoryLayout.findViewWithTag(exercise.getName());
            refreshExerciseContainer(exerciseContainer, exercise, categoryColor);
            refreshTodoList(todoContainer, completedExercises);
        });

        // Show the dialog
        dialog.show();
    }

    private void stopTimerAndNotification(CountDownTimer[] timer, final boolean[] isTimerRunning) {
        // Cancel the CountDownTimer if it's running
        if (timer[0] != null) {
            timer[0].cancel();
            timer[0] = null;
            isTimerRunning[0] = false;
        }

        // Cancel the notification
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(1); // Use the same ID as in TimerService
        }

        // Stop the foreground service
        Intent stopServiceIntent = new Intent(getContext(), TimerService.class);
        getContext().stopService(stopServiceIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "timer_channel",
                    "Timer Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows the remaining timer countdown");
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void updateNotification(String timeRemaining) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "timer_channel")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Replace with your timer icon
                .setContentTitle("Timer Running")
                .setContentText("Remaining Time: " + timeRemaining)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true); // Makes the notification non-dismissible

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }



    private void initializeTable(TableLayout tableLayout, String setType, int columnCount, Exercise exercise) {
        // Clear the existing table contents
        tableLayout.removeAllViews();

        // Define default cell width
        int cellWidth = 100; // Adjust as needed for doubled width
        int dpToPx = (int) (getResources().getDisplayMetrics().density + 0.5f);

        if (setType.equals("Weight x Reps")) {

            // Ensure weightReps list has enough columns
            while (exercise.getWeightReps().size() < columnCount) {
                exercise.getWeightReps().add(new int[]{0, 0}); // Default (weight=0, reps=0)
            }
            // Remove extra columns if the list is longer than the column count
            while (exercise.getWeightReps().size() > columnCount) {
                exercise.getWeightReps().remove(exercise.getWeightReps().size() - 1); // Remove the last element
            }

            TableRow weightRow = new TableRow(getContext());

            TextView weightLabel = new TextView(getContext());
            weightLabel.setText("Weight");
            weightLabel.setPadding(8, 8, 8, 8);
            weightLabel.setGravity(Gravity.CENTER_VERTICAL);

            TableRow.LayoutParams labelParams = new TableRow.LayoutParams(
                    60 * dpToPx,
                    TableRow.LayoutParams.MATCH_PARENT
            );
            labelParams.setMargins(8, 0, 8, 0);
            weightLabel.setLayoutParams(labelParams);

            weightRow.addView(weightLabel);

            for (int i = 0; i < columnCount; i++) {
                // Create a NumberPicker for Weight
                NumberPicker weightPicker = new NumberPicker(getContext());

                // Generate displayed values in reverse order
                List<String> valuesList = new ArrayList<>();
//                List<String> valuesList = new ArrayList<>(Arrays.asList("300", "295", "290", "285", "12", "11", "10", "2", "1", "0"));
                for (int j = 300; j >= 55; j -= 5) valuesList.add(String.valueOf(j));
                for (int j = 50; j >= 12; j -= 2) valuesList.add(String.valueOf(j));
                for (int j = 10; j >= 0; j -= 1) valuesList.add(String.valueOf(j));
                Collections.reverse(valuesList);
                String[] displayedValues = valuesList.toArray(new String[0]);
//                String[] displayedValues = {"300", "295", "290", "285", "12", "11", "10", "2", "1", "0"};

                // Set the min and max values based on the number of steps
                weightPicker.setMinValue(0);
                weightPicker.setMaxValue(displayedValues.length - 1);
                weightPicker.setDisplayedValues(displayedValues); // Set the custom values

                // Ensure the correct initial value is set
                int initialValue = exercise.getWeightReps().get(i)[0];
                int initialIndex = valuesList.indexOf(String.valueOf(initialValue));
                if (initialIndex == -1) initialIndex = 0; // Default to 0 if not found
                weightPicker.setValue(initialIndex);

                Log.e("numPicker", "Initial Value: " + initialValue);
                Log.e("numPicker", "Initial index: " + initialIndex);

                Log.e("numPicker", "Display int: " + Integer.parseInt(displayedValues[initialIndex]));
                Log.e("numPicker", "Display str: " + Arrays.toString(displayedValues));

                // Adjust layout parameters
                TableRow.LayoutParams cellParams = new TableRow.LayoutParams(cellWidth, 50 * dpToPx); // Height = 50dp
                cellParams.setMargins(8, 0, 8, 0); // Adjust margins
                weightPicker.setLayoutParams(cellParams);


                // Add listener to update weight
                final int columnIndex = i; // Capture column index
                weightPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                    int selectedWeight = Integer.parseInt(displayedValues[newVal]);
                    exercise.getWeightReps().get(columnIndex)[0] = selectedWeight;
                });

                // Add NumberPicker to the row
                weightRow.addView(weightPicker);
            }

            // Create the second row (Reps)
            TableRow repsRow = new TableRow(getContext());

            TextView repsLabel = new TextView(getContext());
            repsLabel.setText("Reps");
            repsLabel.setPadding(8, 8, 8, 8);
            repsLabel.setGravity(Gravity.CENTER_VERTICAL); // Center vertically within its row

            TableRow.LayoutParams repLabelParams = new TableRow.LayoutParams(
                    60 * dpToPx, // Set a fixed width for the label, or use WRAP_CONTENT
                    TableRow.LayoutParams.MATCH_PARENT
            );
            repLabelParams.setMargins(8, 0, 8, 0); // Add optional margins
            repsLabel.setLayoutParams(repLabelParams);

            repsRow.addView(repsLabel);

            for (int i = 0; i < columnCount; i++) {
                // Create a NumberPicker for Reps
                NumberPicker repsPicker = new NumberPicker(getContext());

                // Create the values for the NumberPicker
                int minValue = 0;
                int maxValue = 30;
                List<String> reversedValues = new ArrayList<>();

                // Reverse the order of values
                for (int j = maxValue; j >= minValue; j--) {
                    reversedValues.add(String.valueOf(j));
                }

                String[] displayedValues = reversedValues.toArray(new String[0]);

                // Set the min and max values
                repsPicker.setMinValue(0);
                repsPicker.setMaxValue(displayedValues.length - 1);
                repsPicker.setDisplayedValues(displayedValues); // Set the reversed values

                // Set the initial value
                int initialValue = exercise.getWeightReps().get(i)[1];
                int initialIndex = maxValue - initialValue; // Adjust for reversed order
                repsPicker.setValue(initialIndex);

                repsPicker.setWrapSelectorWheel(true);

                TableRow.LayoutParams cellParams = new TableRow.LayoutParams(cellWidth, 50 * dpToPx); // Height = 50dp
                cellParams.setMargins(8, 0, 8, 0); // Adjust margins
                repsPicker.setLayoutParams(cellParams);


                // Add listener to update reps
                final int columnIndex = i; // Capture column index
                repsPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                    int selectedReps = maxValue - newVal; // Adjust for reversed order
                    exercise.getWeightReps().get(columnIndex)[1] = selectedReps;
                });

                repsRow.addView(repsPicker);
            }

            tableLayout.addView(weightRow);
            tableLayout.addView(repsRow);

        } else if (setType.equals("Duration")) {
            // Ensure durations list has enough columns
            while (exercise.getDurations().size() < columnCount) {
                exercise.getDurations().add(0); // Default duration=0
            }

            // Create a single row for Duration
            TableRow durationRow = new TableRow(getContext());
            TextView durationLabel = new TextView(getContext());
            durationLabel.setText("Duration");
            durationLabel.setPadding(8, 8, 8, 8);
            durationRow.addView(durationLabel);

            for (int i = 0; i < columnCount; i++) {
                EditText durationCell = new EditText(getContext());
                durationCell.setText(String.valueOf(exercise.getDurations().get(i))); // Display duration
                durationCell.setPadding(8, 8, 8, 8);
                durationCell.setInputType(InputType.TYPE_CLASS_NUMBER);
                durationCell.setGravity(Gravity.CENTER);
                TableRow.LayoutParams cellParams = new TableRow.LayoutParams(cellWidth, TableRow.LayoutParams.WRAP_CONTENT);
                durationCell.setLayoutParams(cellParams);

                // Add TextWatcher for duration cell
                final int columnIndex = i; // Capture column index
                durationCell.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Update exercise durations for the specific column
                        try {
                            int duration = Integer.parseInt(s.toString());
                            exercise.getDurations().set(columnIndex, duration); // Update duration
                        } catch (NumberFormatException e) {
                            // Handle invalid input (e.g., empty text)
                            exercise.getDurations().set(columnIndex, 0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                durationRow.addView(durationCell);
            }

            // Add the row to the table
            tableLayout.addView(durationRow);
        }
    }

    private void refreshExerciseContainer(LinearLayout exerciseContainer, Exercise exercise, int categoryColor) {

        // Get the table layout from the exercise container
        TableLayout tableLayout = (TableLayout) exerciseContainer.findViewWithTag("exerciseTable");

        if (tableLayout != null) {

            // Update the table based on the exercise setType
            if (exercise.getSetType().equals("Weight x Reps")) {
                TableRow weightRow = (TableRow) tableLayout.getChildAt(0); // Weight row
                TableRow repsRow = (TableRow) tableLayout.getChildAt(1);   // Reps row

                // Remove all children except the first child (label)
                for (int i = weightRow.getChildCount() - 1; i > 0; i--) {
                    weightRow.removeViewAt(i);
                }
                for (int i = repsRow.getChildCount() - 1; i > 0; i--) {
                    repsRow.removeViewAt(i);
                }

                // Add new children for weight and reps
                for (int[] weightRep : exercise.getWeightReps()) {
                    // Add weight cell
                    TextView weightCell = new TextView(getContext());
                    weightCell.setText(padToFourCharacters(String.valueOf(weightRep[0])));
                    weightCell.setTextColor(Color.parseColor("#EDEDED"));
                    weightCell.setPadding(64, 0, 0, 0);
                    weightRow.addView(weightCell);

                    // Add reps cell
                    TextView repsCell = new TextView(getContext());
                    repsCell.setText(padToFourCharacters(String.valueOf(weightRep[1])));
                    repsCell.setTextColor(Color.parseColor("#EDEDED"));
                    repsCell.setPadding(64, 0, 0, 0);
                    repsRow.addView(repsCell);
                }

            } else if (exercise.getSetType().equals("Duration")) {
                TableRow durationRow = (TableRow) tableLayout.getChildAt(0); // Duration row

                for (int i = durationRow.getChildCount() - 1; i > 0; i--) {
                    durationRow.removeViewAt(i);
                }

                for (int duration : exercise.getDurations()) {
                    // Add weight cell
                    TextView durationCell = new TextView(getContext());
                    durationCell.setText(padToFourCharacters(String.valueOf(duration)));
                    durationCell.setTextColor(Color.parseColor("#EDEDED"));
                    durationCell.setPadding(64, 0, 0, 0);
                    durationRow.addView(durationCell);
                }
            }
        }
        Log.i("myTag4", "Calling Save Data");
        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }

    }

    private void addExerciseContainersToLayout(List<Exercise> exercises, LinearLayout exercisesTodoLayout) {
        for (Exercise exercise : exercises) {
            // Create container for the exercise
            LinearLayout exerciseContainer = new LinearLayout(getContext());
            exerciseContainer.setOrientation(LinearLayout.VERTICAL);
            exerciseContainer.setPadding(8, 8, 8, 8);

            // Set the background color and rounded corners for the exercise container
            GradientDrawable backgroundDrawable = new GradientDrawable();
            backgroundDrawable.setColor(availableColors[Math.max(0, exercise.getCategoryClr())]);
            backgroundDrawable.setCornerRadius(16); // Rounded corners
            exerciseContainer.setBackground(backgroundDrawable);

            // Add margins to create gaps between containers
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            containerParams.setMargins(0, 8, 0, 8); // Add margins (left, top, right, bottom)
            exerciseContainer.setLayoutParams(containerParams);

            // Create text view for exercise name
            TextView exerciseText = new TextView(getContext());
            exerciseText.setText(exercise.getName());
            exerciseText.setTextSize(16);
            exerciseText.setTextColor(Color.parseColor("#EDEDED"));
            exerciseText.setPadding(16, 8, 16, 0);

            // Add a divider
            View whiteLine = new View(getContext());
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    3
            );
            lineParams.setMargins(16, 0, 16, 16);
            whiteLine.setLayoutParams(lineParams);
            whiteLine.setBackgroundColor(Color.parseColor("#FFFFFF"));

            // Create the table based on setType
            TableLayout tableLayout = new TableLayout(getContext());
            tableLayout.setStretchAllColumns(false);
            tableLayout.setPadding(32, 0, 32, 0);

            if ("Weight x Reps".equals(exercise.getSetType())) {
                // Add weight row
                TableRow weightRow = new TableRow(getContext());
                TextView weightLabel = new TextView(getContext());
                weightLabel.setText("Weight");
                weightLabel.setTextColor(Color.parseColor("#EDEDED"));
                weightRow.addView(weightLabel);

                for (int[] weightReps : exercise.getWeightReps()) {
                    TextView weightCell = new TextView(getContext());
                    weightCell.setText(String.valueOf(weightReps[0]));
                    weightCell.setTextColor(Color.parseColor("#EDEDED"));
                    weightCell.setPadding(64, 0, 0, 0);
                    weightRow.addView(weightCell);
                }
                tableLayout.addView(weightRow);

                // Add reps row
                TableRow repsRow = new TableRow(getContext());
                TextView repsLabel = new TextView(getContext());
                repsLabel.setText("Reps");
                repsLabel.setTextColor(Color.parseColor("#EDEDED"));
                repsRow.addView(repsLabel);

                for (int[] weightReps : exercise.getWeightReps()) {
                    TextView repsCell = new TextView(getContext());
                    repsCell.setText(String.valueOf(weightReps[1]));
                    repsCell.setTextColor(Color.parseColor("#EDEDED"));
                    repsCell.setPadding(64, 0, 0, 0);
                    repsRow.addView(repsCell);
                }
                tableLayout.addView(repsRow);

            } else if ("Duration".equals(exercise.getSetType())) {
                // Add duration row
                TableRow durationRow = new TableRow(getContext());
                TextView durationLabel = new TextView(getContext());
                durationLabel.setText("Duration");
                durationLabel.setTextColor(Color.parseColor("#EDEDED"));
                durationRow.addView(durationLabel);

                for (int duration : exercise.getDurations()) {
                    TextView durationCell = new TextView(getContext());
                    durationCell.setText(String.valueOf(duration));
                    durationCell.setTextColor(Color.parseColor("#EDEDED"));
                    durationCell.setPadding(64, 0, 0, 0);
                    durationRow.addView(durationCell);
                }
                tableLayout.addView(durationRow);
            }

            // Add views to the container
            exerciseContainer.addView(exerciseText);
            exerciseContainer.addView(whiteLine);
            exerciseContainer.addView(tableLayout);
            exerciseContainer.setTag(exercise.getName());

            // Add exercise container as the second last child
            int insertPosition = Math.max(0, exercisesTodoLayout.getChildCount() - 1);
            exercisesTodoLayout.addView(exerciseContainer, insertPosition);
        }
    }
//

    private String padToFourCharacters(String input) {
        int paddingLength = 4 - input.length();
        if (paddingLength > 0) {
            return input + " ".repeat(paddingLength);
        }
        return input;
    }

    public List<String[]> convertExercisesToList(List<Exercise> exercises) {
        List<String[]> formattedExercises = new ArrayList<>();

        for (Exercise exercise : exercises) {
            String exerciseName = exercise.getName();
            String categoryName = exercise.getCategoryName();

            // Add the exercise name and category name as a String[] pair
            formattedExercises.add(new String[]{exerciseName, categoryName});
        }

        return formattedExercises;
    }





    @Override
    public void onPause() {
        super.onPause();
        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentDateGlobal.equals(selectedDateGlobal)) {
            databaseHelper.saveDataToDatabase(categoryMap);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

