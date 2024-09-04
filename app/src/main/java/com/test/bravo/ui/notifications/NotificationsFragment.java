package com.test.bravo.ui.notifications;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.test.bravo.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView heading = binding.textHeading;
        TextView subheading = binding.textSubheading;
        EditText weightInput = binding.editWeight;
        EditText repsInput = binding.editReps;
        Button calculateButton = binding.buttonCalculate;
        TextView resultText = binding.textResult;
        TableLayout tableRepsMaxes = binding.tableRepsMaxes;


        calculateButton.setOnClickListener(v -> {
            try {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                double weight = Double.parseDouble(weightInput.getText().toString());
                int reps = Integer.parseInt(repsInput.getText().toString());

                double oneRepMax = weight/getOneRepMaxFactor(reps);
                if (oneRepMax < 0){
                    resultText.setText("Please enter a lift with 20 or fewer reps.");
                    tableRepsMaxes.removeAllViews();
                } else {
                    resultText.setText(String.format("Estimated 1RM: %.2fkg", oneRepMax));
                    populateMaxesTable(tableRepsMaxes, weight, reps);
                }
            } catch (NumberFormatException e) {
                resultText.setText("Invalid input. Please enter valid numbers.");
            }
        });

        return root;
    }

    private double getOneRepMaxFactor(int reps) {
        if (reps > 20){
            return -1;
        }

        double factor;
        switch (reps) {
            case 1:factor = 1.0; break;
            case 2:factor = 0.97; break;
            case 3:factor = 0.94; break;
            case 4:factor = 0.92; break;
            case 5:factor = 0.89; break;
            case 6:factor = 0.86; break;
            case 7:factor = 0.83; break;
            case 8:factor = 0.81; break;
            case 9:factor = 0.78; break;
            case 10:factor = 0.75; break;
            case 11:factor = 0.73; break;
            case 12:factor = 0.71; break;
            case 13:factor = 0.7; break;
            case 14:factor = 0.68; break;
            case 15:factor = 0.67; break;
            case 16:factor = 0.65; break;
            case 17:factor = 0.64; break;
            case 18:factor = 0.63; break;
            case 19:factor = 0.61; break;
            case 20:factor = 0.60; break;
            default:factor = 1.0; // For cases where reps > 5 or unknown reps
                break;

        }
        return factor;
    }

    private void populateMaxesTable(TableLayout table, double weight, int max_reps) {
        // Clear any previous rows
        table.removeAllViews();

        // Create a header row
        TableRow headerRow = new TableRow(getContext());
        TextView headerReps = new TextView(getContext());
        TextView headerMax = new TextView(getContext());
        TextView headerPercentage = new TextView(getContext());

        headerReps.setText("Repetitions");
        headerMax.setText("Max Weight (kg)");
        headerPercentage.setText("Percentage of 1RM");

        headerMax.setPadding(32,32, 0, 12);
        headerPercentage.setPadding(32,32, 0, 12);


        headerRow.addView(headerReps);
        headerRow.addView(headerMax);
        headerRow.addView(headerPercentage);

        table.addView(headerRow);




        double oneRepMax = weight/getOneRepMaxFactor(max_reps);

        // Populate the table with maxes for 1 to 10 reps
        for (int reps = 1; reps <= 10; reps++) {
            TableRow row = new TableRow(getContext());

            TextView repsText = new TextView(getContext());
            TextView maxText = new TextView(getContext());
            TextView percentageText = new TextView(getContext());

            headerReps.setTypeface(null, Typeface.BOLD);
            headerMax.setTypeface(null, Typeface.BOLD);
            headerPercentage.setTypeface(null, Typeface.BOLD);

            repsText.setText(String.valueOf(reps));
            maxText.setText(String.format("%.2f", oneRepMax * getOneRepMaxFactor(reps)));
            percentageText.setText(String.format("%.2f%%", 100 * getOneRepMaxFactor(reps)));

            repsText.setPadding(16, 4,0,0);
            maxText.setPadding(48, 4,0,0);
            percentageText.setPadding(48, 4,0,0);

            row.addView(repsText);
            row.addView(maxText);
            row.addView(percentageText);

            table.addView(row);
        }

        // Make the table visible
        table.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}