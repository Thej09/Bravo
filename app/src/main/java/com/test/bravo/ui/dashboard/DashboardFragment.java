package com.test.bravo.ui.dashboard;

import static java.lang.Math.max;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private int changeMonth = 0;
    private GestureDetector gestureDetector;

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

        ImageView left_arrow = root.findViewById(R.id.left_arrow);
        left_arrow.setRotation(90);
        ImageView right_arrow = root.findViewById(R.id.right_arrow);
        right_arrow.setRotation(270);

        if (changeMonth == 0) {
            left_arrow.setColorFilter(Color.GRAY); // Set left arrow to grey
            right_arrow.setColorFilter(null); // Reset right arrow color
        } else {
            right_arrow.setColorFilter(Color.GRAY); // Set right arrow to grey
            left_arrow.setColorFilter(null); // Reset left arrow color
        }

        GridLayout calendarGrid = binding.calendarGrid;

        updateCalendar(calendarGrid);

        left_arrow.setOnClickListener(v -> {
            decrementMonth(calendarGrid, left_arrow, right_arrow);
        });

        right_arrow.setOnClickListener(v -> {
            incrementMonth(calendarGrid, left_arrow, right_arrow);
        });

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaX = e2.getX() - e1.getX();
                if (Math.abs(deltaX) > 100 && Math.abs(velocityX) > 200) {
                    if (deltaX > 0) {
                        Log.e("GestureTag", "Right swipe detected");
                        decrementMonth(binding.calendarGrid, binding.leftArrow, binding.rightArrow);
                    } else {
                        Log.e("GestureTag", "Left swipe detected");
                        incrementMonth(binding.calendarGrid, binding.leftArrow, binding.rightArrow);
                    }
                    return true; // Indicate swipe handled
                }
                return false; // Indicate no swipe detected
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // Allow simple tap to trigger clicks
                return false;
            }
        });

        calendarGrid.setOnTouchListener((v, event) -> {
            boolean gestureHandled = gestureDetector.onTouchEvent(event);
            if (gestureHandled) {
                return true; // GestureDetector handled the event
            }

            // Pass touch events to children if it's not a swipe
            return false;
        });

        return root;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateCalendar(GridLayout calendarGrid) {
        // Clear the grid before updating
        calendarGrid.removeAllViews();

        // Get data from your DailyActivity table
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Calendar calendar = Calendar.getInstance();
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Sunday = 1, Monday = 2, etc.

        calendar.add(Calendar.DAY_OF_YEAR, -27 + 28 * changeMonth - todayDayOfWeek);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dpToPx = (int) (getResources().getDisplayMetrics().density + 0.5f);
        int cellWidth = (screenWidth - 18 * dpToPx) / 7;
        int cellHeight = (screenHeight - 48 * dpToPx) / 6;
        int margin = (int) (1 * getResources().getDisplayMetrics().density + 0.5f);

        List<String> columnLabels = Arrays.asList("Su.", "Mo.", "Tu.", "We.", "Th.", "Fr.", "Sa.");

        // Add column labels
        for (String label : columnLabels) {
            TextView labelCell = new TextView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            params.setMargins(margin, margin, margin, margin);
            params.width = cellWidth;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            labelCell.setLayoutParams(params);
            labelCell.setPadding(4, 8, 4, 8); // Add padding for readability
            labelCell.setText(label);
            labelCell.setTextSize(14); // Slightly larger for labels
            labelCell.setTextColor(Color.WHITE);
            labelCell.setGravity(View.TEXT_ALIGNMENT_CENTER);

            calendarGrid.addView(labelCell);
        }

        // Add day cells
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
                dayCellLayout.setPadding(8, 8, 8, 8);
                dayCellLayout.setBackgroundResource(R.drawable.cell_background);

                // Add the date label
                TextView dateLabel = new TextView(getContext());
                String dayLabel = new SimpleDateFormat("d", Locale.getDefault()).format(calendar.getTime());
                dateLabel.setText(dayLabel);
                dateLabel.setTextSize(14);
                dateLabel.setTextColor(Color.WHITE);
                dateLabel.setGravity(View.TEXT_ALIGNMENT_CENTER);
                dateLabel.setFocusable(false);
                dateLabel.setClickable(false);

                // Highlight the current day
                if (formattedDate.equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date()))) {
                    dayCellLayout.setBackgroundResource(R.drawable.cell_background_today);

                    TypedValue typedValue = new TypedValue();
                    Context context = getContext();
                    if (context != null && context.getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)) {
                        int primaryColor = typedValue.data;
                        dateLabel.setTextColor(primaryColor);
                        Typeface boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
                        dateLabel.setTypeface(boldTypeface);
                    }
                }
                dayCellLayout.addView(dateLabel);

                // Handle exercises
                boolean afterDate = false;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date currentDate = dateFormat.parse(formattedDate);
                    Date actualDate = new Date();

                    if (currentDate != null && currentDate.after(actualDate)) {
                        afterDate = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                List<Exercise> exercises = !afterDate
                        ? databaseHelper.getDailyActivity(formattedDate)
                        : databaseHelper.getPlannedExercises(formattedDate);

                //if (!(afterDate && changeMonth == 0)) {
                if (exercises != null) {
                    for (Exercise exercise : exercises) {
                        TextView exerciseView = new TextView(getContext());
                        String exerciseName = exercise.getName();
                        int maxTextWidth = cellWidth;

                        TextPaint textPaint = new TextPaint();
                        textPaint.setTextSize(exerciseView.getTextSize());
                        CharSequence ellipsizedName = TextUtils.ellipsize(
                                exerciseName, textPaint, maxTextWidth, TextUtils.TruncateAt.END);

                        exerciseView.setText(ellipsizedName);
                        exerciseView.setTextSize(12);
                        exerciseView.setTextColor(Color.WHITE);

                        GradientDrawable background = new GradientDrawable();
                        background.setColor(availableColors[Math.max(0, exercise.getCategoryClr())]);
                        background.setCornerRadius(4 * getResources().getDisplayMetrics().density);
                        if (afterDate) {
                            background.setAlpha(64);
                        }
                        exerciseView.setBackground(background);

                        LinearLayout.LayoutParams exerciseParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        exerciseParams.setMargins(0, 0, 0, 0);
                        exerciseView.setLayoutParams(exerciseParams);

                        // Date label
                        exerciseView.setFocusable(false);
                        exerciseView.setClickable(false);

                        dayCellLayout.addView(exerciseView);
                    }
                }
                //}

                dayCellLayout.setOnTouchListener((v, event) -> {
                    boolean isSwipe = gestureDetector.onTouchEvent(event); // Detect swipe gestures

                    // Only trigger `performClick()` for simple taps (not swipes)
                    if (isSwipe) {
                        Log.d("GestureDebug", "Swipe detected on dayCellLayout");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.d("GestureDebug", "Tap detected on dayCellLayout");
                        Log.e("DayCell", "DayCell clicked for date: " + formattedDate);
//                        showExercisePopup(formattedDate, exercises);
                    }

                    return isSwipe; // Return true to consume swipe gestures, false for normal clicks
                });

                dayCellLayout.setOnClickListener(v -> {
                    Log.e("DayCell", "DayCell clicked for date: " + formattedDate);
                    showExercisePopup(formattedDate, exercises);
                });

                calendarGrid.addView(dayCellLayout);

                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
    }


    private void showExercisePopup(String date, List<Exercise> exercises) {
        // Create a ScrollView for the popup
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout popupLayout = new LinearLayout(getContext());
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setPadding(32, 32, 32, 32);

        boolean afterDate = false;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = dateFormat.parse(date);
            Date actualDate = new Date(); // Current date

            if (currentDate != null && currentDate.after(actualDate)) {
                // If the current date is greater than the actual date, skip further processing
                afterDate = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Add a title to the popup
        TextView title = new TextView(getContext());
        if (afterDate){
            title.setText("Exercises planned for " + date);
        } else {
            title.setText("Exercises on " + date);
        }
        title.setTextSize(18);
        TypedValue typedValue = new TypedValue();
        Context context = getContext();
//        Typeface boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
//        title.setTypeface(boldTypeface);
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
//            int categoryColor = exercise.getCategory().getColor(); // Assuming category has a color
            backgroundDrawable.setColor(availableColors[max(0,exercise.getCategoryClr())]);
            backgroundDrawable.setCornerRadius(16); // Rounded corners
            if (afterDate){
                backgroundDrawable.setAlpha(64);
            }
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

    private void incrementMonth(GridLayout calendarGrid, ImageView left_arrow, ImageView right_arrow) {
        if (changeMonth == 0) {
            changeMonth++;
            left_arrow.setColorFilter(null);
            right_arrow.setColorFilter(Color.GRAY);
            animateCalendarChange(calendarGrid, true);
        }
    }

    private void decrementMonth(GridLayout calendarGrid, ImageView left_arrow, ImageView right_arrow) {
        if (changeMonth == 1) {
            changeMonth--;
            right_arrow.setColorFilter(null);
            left_arrow.setColorFilter(Color.GRAY);
            animateCalendarChange(calendarGrid, false);
        }
    }

    private void animateCalendarChange(GridLayout calendarGrid, boolean isForward) {
        // Create translation animation values
        float translationX = isForward ? calendarGrid.getWidth() : -calendarGrid.getWidth();

        // Create a temporary copy of the calendar grid for the slide-out animation
        GridLayout tempCalendarGrid = new GridLayout(calendarGrid.getContext());
        tempCalendarGrid.setLayoutParams(calendarGrid.getLayoutParams());
        tempCalendarGrid.setId(View.generateViewId());
        tempCalendarGrid.setColumnCount(calendarGrid.getColumnCount());
        tempCalendarGrid.setRowCount(calendarGrid.getRowCount());

        // Copy child views from the original calendar grid to the temporary one
        for (int i = 0; i < calendarGrid.getChildCount(); i++) {
            View child = calendarGrid.getChildAt(i);
            if (child != null) {
                View clone = cloneView(child);
                tempCalendarGrid.addView(clone, child.getLayoutParams());
            }
        }

        // Add the temporary calendar grid to the parent layout
        ViewGroup parent = (ViewGroup) calendarGrid.getParent();
        parent.addView(tempCalendarGrid);

        // Align the vertical position of tempCalendarGrid with calendarGrid
//        tempCalendarGrid.setY(calendarGrid.getHeight()); // Align Y position
        tempCalendarGrid.setY(-calendarGrid.getHeight() + 6);
        tempCalendarGrid.setTranslationX(0); // Start at the original position

        // Move the new calendarGrid to the sliding-in position
        calendarGrid.setTranslationX(translationX);

        // Update the actual calendar grid with new data
        updateCalendar(calendarGrid);

        // Prepare animations
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(tempCalendarGrid, "translationX", 0, -translationX);
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(calendarGrid, "translationX", translationX, 0);

        // Set animation durations
        slideOut.setDuration(450); // Slide-out duration
        slideIn.setDuration(450); // Slide-in duration
        slideIn.setStartDelay(0); // Start slide-in halfway through slide-out

        // Start animations together with staggered timing
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideOut, slideIn);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Remove the temporary calendar grid after the animation ends
                parent.removeView(tempCalendarGrid);
            }
        });

        animatorSet.start();
    }

    private View cloneView(View original) {
        if (original instanceof TextView) {
            TextView originalText = (TextView) original;
            TextView clone = new TextView(original.getContext());
            clone.setText(originalText.getText());
            clone.setTextSize(originalText.getTextSize() / originalText.getContext().getResources().getDisplayMetrics().scaledDensity); // Convert pixel size to sp
            clone.setTextColor(originalText.getTextColors());
            clone.setGravity(originalText.getGravity());
            clone.setBackground(originalText.getBackground());
            clone.setLayoutParams(originalText.getLayoutParams());
            clone.setPadding(
                    originalText.getPaddingLeft(),
                    originalText.getPaddingTop(),
                    originalText.getPaddingRight(),
                    originalText.getPaddingBottom()
            );
            return clone;
        } else if (original instanceof LinearLayout) {
            LinearLayout originalLayout = (LinearLayout) original;
            LinearLayout clone = new LinearLayout(original.getContext());
            clone.setOrientation(originalLayout.getOrientation());
            clone.setLayoutParams(originalLayout.getLayoutParams());
            clone.setBackground(originalLayout.getBackground());
            clone.setPadding(
                    originalLayout.getPaddingLeft(),
                    originalLayout.getPaddingTop(),
                    originalLayout.getPaddingRight(),
                    originalLayout.getPaddingBottom()
            );

            // Clone each child view recursively
            for (int i = 0; i < originalLayout.getChildCount(); i++) {
                View child = originalLayout.getChildAt(i);
                View clonedChild = cloneView(child);
                clone.addView(clonedChild);
            }
            return clone;
        } else if (original instanceof GridLayout) {
            GridLayout originalGrid = (GridLayout) original;
            GridLayout clone = new GridLayout(original.getContext());
            clone.setLayoutParams(originalGrid.getLayoutParams());
            clone.setColumnCount(originalGrid.getColumnCount());
            clone.setRowCount(originalGrid.getRowCount());
            clone.setBackground(originalGrid.getBackground());
            clone.setPadding(
                    originalGrid.getPaddingLeft(),
                    originalGrid.getPaddingTop(),
                    originalGrid.getPaddingRight(),
                    originalGrid.getPaddingBottom()
            );

            // Clone each child view recursively
            for (int i = 0; i < originalGrid.getChildCount(); i++) {
                View child = originalGrid.getChildAt(i);
                View clonedChild = cloneView(child);
                clone.addView(clonedChild, originalGrid.getLayoutParams());
            }
            return clone;
        } else {
            // Handle generic views
            View clone = new View(original.getContext());
            clone.setLayoutParams(original.getLayoutParams());
            clone.setBackground(original.getBackground());
            return clone;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
