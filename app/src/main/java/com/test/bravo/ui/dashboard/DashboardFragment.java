package com.test.bravo.ui.dashboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.test.bravo.R;
import com.test.bravo.database.DatabaseHelper;
import com.test.bravo.databinding.FragmentDashboardBinding;
import com.test.bravo.model.Exercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set the month title
        TextView monthTitle = root.findViewById(R.id.month_title);
        String currentMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        monthTitle.setText(currentMonth);


        GridLayout calendarGrid = binding.calendarGrid;

        // Get data from your DailyActivity table
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Calendar calendar = Calendar.getInstance();
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Sunday = 1, Monday = 2, etc.

        calendar.add(Calendar.DAY_OF_YEAR, -27 - todayDayOfWeek);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dpToPx = (int) (getResources().getDisplayMetrics().density + 0.5f);
        int cellWidth = (screenWidth - 18 * dpToPx) / 7;
        int cellHeight = (screenHeight - 48 * dpToPx) / 6;
        int margin = (int) (1 * getResources().getDisplayMetrics().density + 0.5f);

        List<String> columnLabels = Arrays.asList("Su.", "Mo.", "Tu.", "We.", "Th.", "Fr.", "Sa.");

        for (String label : columnLabels) {
            TextView labelCell = new TextView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            params.setMargins(margin, margin, margin, margin);
            params.width = cellWidth;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;;

            labelCell.setLayoutParams(params);
            labelCell.setPadding(4, 8, 4, 8); // Add some padding for readability
            labelCell.setText(label);
            labelCell.setTextSize(14); // Slightly larger for labels
            labelCell.setTextColor(Color.WHITE);
            labelCell.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // Add the label cell to the GridLayout
            calendarGrid.addView(labelCell);
        }

        for (int week = 1; week <= 5; week++) {
            for (int day = 1; day <= 7; day++) {
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(calendar.getTime());

                // Create a layout for the day cell
                LinearLayout dayCellLayout = new LinearLayout(getContext());
                dayCellLayout.setOrientation(LinearLayout.VERTICAL);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                params.setMargins(margin, margin, margin, margin);
                params.width = cellWidth;
                params.height = cellHeight;

                dayCellLayout.setLayoutParams(params);
                dayCellLayout.setPadding(8, 8, 8, 8); // Add padding for readability
                dayCellLayout.setBackgroundResource(R.drawable.cell_background); // Use a drawable for rounded corners

                // Add the date label
                TextView dateLabel = new TextView(getContext());
                String dayLabel = new SimpleDateFormat("d", Locale.getDefault()).format(calendar.getTime());
                dateLabel.setText(dayLabel);
                dateLabel.setTextSize(14);
                dateLabel.setTextColor(Color.WHITE);
                dateLabel.setGravity(View.TEXT_ALIGNMENT_CENTER);


                // Highlight the current day
                if (formattedDate.equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date()))) {
                    dayCellLayout.setBackgroundResource(R.drawable.cell_background_today);

                    // Get the primary color from the theme
                    TypedValue typedValue = new TypedValue();
                    Context context = getContext();
                    if (context != null && context.getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)) {
                        int primaryColor = typedValue.data;
                        dateLabel.setTextColor(primaryColor); // Set the text color to primary
                        Typeface boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
                        dateLabel.setTypeface(boldTypeface);
                    }
                }
                dayCellLayout.addView(dateLabel);
                // Add exercises
                List<Exercise> exercises = databaseHelper.getDailyActivity(formattedDate);
                if (exercises != null) {
                    for (Exercise exercise : exercises) {
                        // Create a TextView for each exercise
                        TextView exerciseView = new TextView(getContext());

                        // Truncate exercise name to fit within one line
                        String exerciseName = exercise.getName();
                        int maxTextWidth = cellWidth - (int) (0 * getResources().getDisplayMetrics().density); // Account for padding

                        // Use TextPaint to calculate text width
                        TextPaint textPaint = new TextPaint();
                        textPaint.setTextSize(exerciseView.getTextSize());
                        CharSequence ellipsizedName = TextUtils.ellipsize(
                                exerciseName, textPaint, maxTextWidth, TextUtils.TruncateAt.END);

                        exerciseView.setText(ellipsizedName);
                        exerciseView.setTextSize(12);
                        exerciseView.setTextColor(Color.WHITE); // Set text color to white for contrast

                        // Set the background color based on the exercise's category
//                        int categoryColor = exercise.getCategory().getColor(); // Assuming Exercise has a getCategory() method
                        int categoryColor = exercise.getCategory().getColor();

                        GradientDrawable background = new GradientDrawable();
                        background.setColor(availableColors[categoryColor]);
                        background.setCornerRadius(4 * getResources().getDisplayMetrics().density); // Rounded corners
                        exerciseView.setBackground(background);

                        // Add margin and padding
                        LinearLayout.LayoutParams exerciseParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        exerciseParams.setMargins(0, 0, 0, 0); // Add margin between exercises
                        exerciseView.setLayoutParams(exerciseParams);
                        exerciseView.setPadding(0, 0, 0, 0); // Add padding inside the box

                        dayCellLayout.addView(exerciseView);
                    }
                }

                dayCellLayout.setOnClickListener(v -> showExercisePopup(formattedDate, exercises));

                calendarGrid.addView(dayCellLayout);

                calendar.add(Calendar.DAY_OF_YEAR, 1); // Move to the next day
            }
        }

        return root;
    }

    private void showExercisePopup(String date, List<Exercise> exercises) {
        // Create a ScrollView for the popup
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout popupLayout = new LinearLayout(getContext());
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setPadding(32, 32, 32, 32);

        // Add a title to the popup
        TextView title = new TextView(getContext());
        title.setText("Exercises on " + date);
        title.setTextSize(18);
        TypedValue typedValue = new TypedValue();
        Context context = getContext();
        Typeface boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
        title.setTypeface(boldTypeface);
        title.setPadding(16, 16, 16, 16);

        context.getTheme().resolveAttribute(R.attr.SecondaryTextColor, typedValue, true);
        int primaryColor = typedValue.data;
        title.setTextColor(primaryColor);

        popupLayout.addView(title);

        // Loop through the exercises
        for (Exercise exercise : exercises) {
            // Create container for the exercise
            LinearLayout exerciseContainer = new LinearLayout(getContext());
            exerciseContainer.setOrientation(LinearLayout.VERTICAL);
            exerciseContainer.setPadding(8, 8, 8, 8);

            // Set the background color and rounded corners for the exercise container
            GradientDrawable backgroundDrawable = new GradientDrawable();
            int categoryColor = exercise.getCategory().getColor(); // Assuming category has a color
            backgroundDrawable.setColor(availableColors[categoryColor]); // Use the category color
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

            // Add exercise container to the popup layout
            popupLayout.addView(exerciseContainer);
        }

        // Add ScrollView to the popup layout
        scrollView.addView(popupLayout);

        // Create a dialog for the popup
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setView(scrollView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
