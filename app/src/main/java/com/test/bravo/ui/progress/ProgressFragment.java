package com.test.bravo.ui.progress;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.test.bravo.R;
import com.test.bravo.database.DatabaseHelper;
import com.test.bravo.databinding.FragmentProgressBinding;
import com.test.bravo.model.Exercise;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;
    private DatabaseHelper dbHelper;
    private float maxExerciseScore = 24;
    private int streakWeekThreshold = 3;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize the ViewModel
        ProgressViewModel progressViewModel =
                new ViewModelProvider(this).get(ProgressViewModel.class);

        // Inflate the layout
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dbHelper = new DatabaseHelper(getContext());

        // Access UI components via binding
        TextView textWeekStreak = binding.textWeekStreak;
        ImageView imageMuscleMap = binding.imageMuscleMap;

        // Observe data from ViewModel and update the TextView
//        progressViewModel.getText().observe(getViewLifecycleOwner(), textWeekStreak::setText);

        // Set the images for the ImageViews
        imageMuscleMap.setImageResource(R.drawable.musclemap);

        Map<String, List<Integer>> muscleImageMap = new HashMap<>();
        muscleImageMap.put("Upper Abs", Arrays.asList(R.id.image_upper_abs, R.drawable.upperabs));
        muscleImageMap.put("Lower Abs", Arrays.asList(R.id.image_lower_abs, R.drawable.lowerabs));
        muscleImageMap.put("Chest", Arrays.asList(R.id.image_chest, R.drawable.chest));
        muscleImageMap.put("Biceps", Arrays.asList(R.id.image_biceps, R.drawable.biceps));
        muscleImageMap.put("Triceps", Arrays.asList(R.id.image_triceps, R.drawable.triceps));
        muscleImageMap.put("Quads", Arrays.asList(R.id.image_quads, R.drawable.quads));
        muscleImageMap.put("Hamstrings", Arrays.asList(R.id.image_hamstrings, R.drawable.hamstrings));
        muscleImageMap.put("Glutes", Arrays.asList(R.id.image_glutes, R.drawable.glutes));
        muscleImageMap.put("Traps", Arrays.asList(R.id.image_traps, R.drawable.traps));
        muscleImageMap.put("Obliques", Arrays.asList(R.id.image_obliques, R.drawable.obliques));
        muscleImageMap.put("Lower Back", Arrays.asList(R.id.image_lower_back, R.drawable.lowerback));
        muscleImageMap.put("Upper Back", Arrays.asList(R.id.image_upper_back, R.drawable.upperback));
        muscleImageMap.put("Lats", Arrays.asList(R.id.image_lats, R.drawable.lats));
        muscleImageMap.put("Shoulders", Arrays.asList(R.id.image_shoulders, R.drawable.shoulders));
        muscleImageMap.put("Calves", Arrays.asList(R.id.image_calves, R.drawable.calves));
        muscleImageMap.put("Forearms", Arrays.asList(R.id.image_forearms, R.drawable.forearms));
        muscleImageMap.put("Adductors", Arrays.asList(R.id.image_adductors, R.drawable.adductors));


        Map<String, Integer> muscleGroupScore = new HashMap<>();
        for (String key : muscleImageMap.keySet()) {
            muscleGroupScore.put(key, 0);
        }

// Access UI components via binding or findViewById
        for (Map.Entry<String, List<Integer>> entry : muscleImageMap.entrySet()) {
            int imageViewId = entry.getValue().get(0);
            int drawableId = entry.getValue().get(1);

            ImageView imageView = root.findViewById(imageViewId);
            if (imageView != null) {
                imageView.setImageResource(drawableId);
                imageView.setAlpha(0.0f); // Adjust alpha value if needed
            }

        }


        Map<String, List<String>> exercise_muscles = readJsonFromAssets("exercises.json");
        List<Exercise> thisWeekExercises = getLastWeekExercises();


        for (Exercise curExercise : thisWeekExercises) {

            List<String> exerciseList = new ArrayList<>();
            if (exercise_muscles.containsKey(curExercise.getName())) {
//                Log.e("SysOut", "Exercise found in exercise_muscles: " + curExercise.getName());
                // Additional processing if needed
                exerciseList = exercise_muscles.get(curExercise.getName());
            }

            for (int i = 0; i < exerciseList.size(); i++) {
                int scoreToAdd = Math.max(1, 3 - i);
                String exerciseName = exerciseList.get(i);

                if (muscleGroupScore.containsKey(exerciseName)) {
                    muscleGroupScore.put(exerciseName,
                            muscleGroupScore.get(exerciseName) + scoreToAdd * curExercise.getWeightReps().size());
                }

            }
        }

        for (String key : muscleImageMap.keySet()) {
            float completionRate = min(muscleGroupScore.get(key), maxExerciseScore)/maxExerciseScore;
            setExerciseAlpha(key, muscleImageMap, completionRate, root);
        }



        int[] result = dbHelper.getConsecutiveWeeksWithActivity(streakWeekThreshold);

        int streakWeeks = result[0];
        int activeDays = result[1];

        updateExerciseStreak(root, streakWeeks, activeDays);

        float total_score = 0;
        float max_total_score = 0;

        for (String key : muscleImageMap.keySet()) {
            total_score += muscleGroupScore.get(key);
            max_total_score += maxExerciseScore;
        }

        Log.e("sysOut", String.valueOf(total_score));
        Log.e("sysOut", String.valueOf(max_total_score));

        TextView completion_percentage = root.findViewById(R.id.completion_percentage);
        String completion_percentage_text = (round(100*(total_score/max_total_score)) + "% Complete" );
        completion_percentage.setText(completion_percentage_text);

        return root;
    }

    public void setExerciseAlpha(String exerciseName, Map<String, List<Integer>> muscleImageMap, float alphaValue, View root) {
        // Check if the map contains the exercise name
        if (muscleImageMap.containsKey(exerciseName)) {
            // Get the resource IDs for the ImageView and drawable
            List<Integer> resourceIds = muscleImageMap.get(exerciseName);
            if (resourceIds != null && resourceIds.size() >= 1) {
                int imageViewId = resourceIds.get(0); // ImageView ID

                // Find the ImageView using the root view
                ImageView imageView = root.findViewById(imageViewId);
                if (imageView != null) {
                    // Set the alpha value for the ImageView
                    imageView.setAlpha(alphaValue);
                }
            }
        } else {
            // Log or handle the case where the exercise name doesn't exist in the map
            System.out.println("Exercise not found: " + exerciseName);
        }
    }

    public Map<String, List<String>> readJsonFromAssets(String fileName) {
        Map<String, List<String>> exerciseMap = new HashMap<>();
        try {
            // Access the assets manager
            AssetManager assetManager = requireContext().getAssets();
            InputStream inputStream = assetManager.open(fileName);

            // Read the file content
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            // Parse the JSON
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                List<String> muscleGroups = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    muscleGroups.add(jsonArray.getString(i));
                }
                exerciseMap.put(key, muscleGroups);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exerciseMap;
    }

    public List<Exercise> getLastWeekExercises() {

        List<Exercise> lastWeekExercises = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get today's date
        Calendar calendar = Calendar.getInstance();

        // Find the most recent Sunday
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysSinceSunday = todayDayOfWeek - Calendar.SUNDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysSinceSunday); // Go to the most recent Sunday

        // Loop from Sunday to today's date
        while (true) {
            String date = dateFormat.format(calendar.getTime());
            lastWeekExercises.addAll(dbHelper.getDailyActivity(date));

            // If it's the current day, stop the loop
            if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) &&
                    calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                break;
            }

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return lastWeekExercises;
    }

    private void updateExerciseStreak(View root, int numWeeks, int numDays) {
        // Update text_week_streak
        TextView textWeekStreak = root.findViewById(R.id.text_week_streak);
        String weekStreakText = numWeeks + "-week exercise streak" + (numWeeks == 0 ? "" : "!");
        textWeekStreak.setText(weekStreakText);

        // Update text_workouts_count
        TextView textWorkoutsCount = root.findViewById(R.id.text_workouts_count);
        String workoutText = "Made up of " + numDays +
                " workout day" + (numDays == 1 ? "" : "s") + (numDays > 5 ? "!" : "");
        textWorkoutsCount.setText(workoutText);

//        // Update muscles worked text
//        TextView musclesWorked = root.findViewById(R.id.muscles_worked);
//        musclesWorked.setText("Muscles worked out this week:");
//
//        // Update completion percentage
//        TextView completionPercentage = root.findViewById(R.id.completion_percentage);
//        int completion = (int) ((double) numDays / (numWeeks * 7) * 100); // Example: Assuming 1 workout/day is perfect
//        completionPercentage.setText(completion + "% Complete");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}