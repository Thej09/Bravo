package com.test.bravo.ui.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.test.bravo.R;
import com.test.bravo.databinding.FragmentProgressBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize the ViewModel
        ProgressViewModel progressViewModel =
                new ViewModelProvider(this).get(ProgressViewModel.class);

        // Inflate the layout
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Access UI components via binding
        TextView textWeekStreak = binding.textWeekStreak;
        ImageView imageMuscleMap = binding.imageMuscleMap;

        // Observe data from ViewModel and update the TextView
        progressViewModel.getText().observe(getViewLifecycleOwner(), textWeekStreak::setText);

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


        setExerciseAlpha("Chest", muscleImageMap, 0.7f, root);
        setExerciseAlpha("Shoulders", muscleImageMap, 0.3f, root);
        setExerciseAlpha("Triceps", muscleImageMap, 0.5f, root);
        setExerciseAlpha("Lats", muscleImageMap, 0.7f, root);
        setExerciseAlpha("Biceps", muscleImageMap, 1.0f, root);
        setExerciseAlpha("Upper Back", muscleImageMap, 0.5f, root);

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}