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
        ImageView imageUpperAbs = binding.imageUpperAbs;

        // Observe data from ViewModel and update the TextView
        progressViewModel.getText().observe(getViewLifecycleOwner(), textWeekStreak::setText);

        // Set the images for the ImageViews
        imageMuscleMap.setImageResource(R.drawable.musclemap);
        imageUpperAbs.setImageResource(R.drawable.upperabs);

        // Adjust the alpha dynamically if needed
        imageUpperAbs.setAlpha(0.5f); // Adjust transparency (0.0f to 1.0f)

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}