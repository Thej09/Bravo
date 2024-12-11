package com.test.bravo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import com.test.bravo.model.Category;
import com.test.bravo.model.Exercise;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bravo.db";
    private static final int DATABASE_VERSION = 5;

    // Table and Column Definitions
    private static final String TABLE_CATEGORY = "Category";
    private static final String TABLE_EXERCISE = "Exercise";
    private static final String TABLE_DAILY_ACTIVITY = "DailyActivity";

    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_COLOR = "color";

    private static final String KEY_EXERCISE_NAME = "name";
    private static final String KEY_CATEGORY_NAME_FK = "category_name";
    private static final String KEY_SET_TYPE = "set_type";
    private static final String KEY_DONE_TODAY = "done_today";
    private static final String KEY_WEIGHT_REPS = "weight_reps";
    private static final String KEY_DURATIONS = "durations";

    private static final String KEY_DATE = "date"; // Date of completion
    private static final String KEY_EXERCISE_NAME_DAILY = "exercise_name";
    private static final String KEY_CATEGORY_NAME_DAILY = "category_name";
    private static final String KEY_SET_TYPE_DAILY = "set_type";
    private static final String KEY_WEIGHT_REPS_DAILY = "weight_reps";
    private static final String KEY_DURATIONS_DAILY = "durations";
    private static final String KEY_DONE_TIME = "done_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                KEY_CATEGORY_NAME + " TEXT PRIMARY KEY, " +
                KEY_CATEGORY_COLOR + " INTEGER, " +
                "isCollapsed INTEGER)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        String CREATE_EXERCISE_TABLE = "CREATE TABLE " + TABLE_EXERCISE + " (" +
                KEY_EXERCISE_NAME + " TEXT PRIMARY KEY, " +
                KEY_CATEGORY_NAME_FK + " TEXT, " +
                KEY_SET_TYPE + " TEXT, " +
                KEY_DONE_TODAY + " INTEGER, " +
                "doneTime INTEGER, " +  // New column to store doneTime as a timestamp
                KEY_WEIGHT_REPS + " TEXT, " +
                KEY_DURATIONS + " TEXT, " +
                "exerciseNotes TEXT, " +
                "FOREIGN KEY(" + KEY_CATEGORY_NAME_FK + ") REFERENCES " + TABLE_CATEGORY + "(" + KEY_CATEGORY_NAME + "))";
        db.execSQL(CREATE_EXERCISE_TABLE);

        String CREATE_DAILY_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_DAILY_ACTIVITY + " (" +
                KEY_DATE + " TEXT, " +
                KEY_EXERCISE_NAME_DAILY + " TEXT, " +
                KEY_CATEGORY_NAME_DAILY + " TEXT, " +
                KEY_SET_TYPE_DAILY + " TEXT, " +
                KEY_WEIGHT_REPS_DAILY + " TEXT, " +
                KEY_DURATIONS_DAILY + " TEXT, " +
                KEY_DONE_TIME + " INTEGER, " +
                "PRIMARY KEY (" + KEY_DATE + ", " + KEY_EXERCISE_NAME_DAILY + "))";
        db.execSQL(CREATE_DAILY_ACTIVITY_TABLE);
        // HERE
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_EXERCISE + " ADD COLUMN exerciseNotes TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_EXERCISE + " ADD COLUMN doneTime INTEGER DEFAULT 0");
        }
        if (oldVersion < 5) {
            String CREATE_DAILY_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_DAILY_ACTIVITY + " (" +
                    KEY_DATE + " TEXT, " +
                    KEY_EXERCISE_NAME_DAILY + " TEXT, " +
                    KEY_CATEGORY_NAME_DAILY + " TEXT, " +
                    KEY_SET_TYPE_DAILY + " TEXT, " +
                    KEY_WEIGHT_REPS_DAILY + " TEXT, " +
                    KEY_DURATIONS_DAILY + " TEXT, " +
                    KEY_DONE_TIME + " INTEGER, " +
                    "PRIMARY KEY (" + KEY_DATE + ", " + KEY_EXERCISE_NAME_DAILY + "))";
            db.execSQL(CREATE_DAILY_ACTIVITY_TABLE);
        }
    }

    // Methods to save and load daily exercises

    public void saveDailyActivity(String date, Exercise exercise, String categoryName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_EXERCISE_NAME_DAILY, exercise.getName());
        values.put(KEY_CATEGORY_NAME_DAILY, categoryName);
        values.put(KEY_SET_TYPE_DAILY, exercise.getSetType());
        values.put(KEY_WEIGHT_REPS_DAILY, new JSONArray(exercise.getWeightReps()).toString());
        values.put(KEY_DURATIONS_DAILY, new JSONArray(exercise.getDurations()).toString());
        values.put(KEY_DONE_TIME, exercise.getDoneTime());

        db.insertWithOnConflict(TABLE_DAILY_ACTIVITY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteDailyActivity(String date, String exerciseName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DAILY_ACTIVITY,
                KEY_DATE + " = ? AND " + KEY_EXERCISE_NAME_DAILY + " = ?",
                new String[]{date, exerciseName});
    }

    public List<Exercise> getDailyActivity(String date) {
        SQLiteDatabase db = getReadableDatabase();
        List<Exercise> dailyExercises = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_DAILY_ACTIVITY +
                " WHERE " + KEY_DATE + " = ? ORDER BY " + KEY_DONE_TIME + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXERCISE_NAME_DAILY));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME_DAILY));
                String setType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SET_TYPE_DAILY));
                String weightRepsJson = cursor.getString(cursor.getColumnIndexOrThrow(KEY_WEIGHT_REPS_DAILY));
                String durationsJson = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DURATIONS_DAILY));
                long doneTime = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_DONE_TIME));

                List<int[]> weightReps = parseWeightRepsFromJson(weightRepsJson);
                List<Integer> durations = parseDurationsFromJson(durationsJson);

                Category category = getCategoryByName(categoryName);

                Exercise exercise = new Exercise(exerciseName, category, setType);
                exercise.setWeightReps(weightReps);
                exercise.setDurations(durations);
                exercise.setDoneTime(doneTime);

                dailyExercises.add(exercise);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return dailyExercises;
    }
    private Category getCategoryByName(String categoryName) {
        SQLiteDatabase db = getReadableDatabase();
        Category category = null;

        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryName});

        if (cursor != null && cursor.moveToFirst()) {
            int categoryColor = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CATEGORY_COLOR));
            boolean isCollapsed = cursor.getInt(cursor.getColumnIndexOrThrow("isCollapsed")) == 1;

            category = new Category(categoryName, categoryColor);
            category.setIsCollapsed(isCollapsed);

            cursor.close();
        }

        return category;
    }



    // Save and load current data

    public void saveDataToDatabase(Map<String, Category> categoryMap) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // Clear existing data
            db.delete(TABLE_CATEGORY, null, null);
            db.delete(TABLE_EXERCISE, null, null);

            for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
                String categoryName = entry.getKey();
                Category category = entry.getValue();

                // Insert into Category table
                ContentValues categoryValues = new ContentValues();
                categoryValues.put(KEY_CATEGORY_NAME, category.getName());
                categoryValues.put(KEY_CATEGORY_COLOR, category.getColor());
                categoryValues.put("isCollapsed", category.getIsCollapsed() ? 1 : 0); // Save isCollapsed
                db.insert(TABLE_CATEGORY, null, categoryValues);

                // Insert into Exercise table
                for (Exercise exercise : category.getExercises()) {
                    ContentValues exerciseValues = new ContentValues();
                    exerciseValues.put(KEY_EXERCISE_NAME, exercise.getName());
                    exerciseValues.put(KEY_CATEGORY_NAME_FK, categoryName);
                    exerciseValues.put(KEY_SET_TYPE, exercise.getSetType());
                    exerciseValues.put(KEY_DONE_TODAY, exercise.getDoneToday() ? 1 : 0);

                    // Convert weightReps and durations to JSON
                    exerciseValues.put(KEY_WEIGHT_REPS, new JSONArray(exercise.getWeightReps()).toString());
                    exerciseValues.put(KEY_DURATIONS, new JSONArray(exercise.getDurations()).toString());

                    exerciseValues.put("exerciseNotes", exercise.getExerciseNotes());
                    exerciseValues.put("doneTime", exercise.getDoneTime());

                    db.insert(TABLE_EXERCISE, null, exerciseValues);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // Load Data

    public Map<String, Category> loadDataFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        Map<String, Category> categoryMap = new HashMap<>();

        // Load Categories
        Cursor categoryCursor = db.query(TABLE_CATEGORY, null, null, null, null, null, null);
        if (categoryCursor != null && categoryCursor.moveToFirst()) {
            do {
                String categoryName = categoryCursor.getString(categoryCursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME));
                int categoryColor = categoryCursor.getInt(categoryCursor.getColumnIndexOrThrow(KEY_CATEGORY_COLOR));
                boolean isCollapsed = categoryCursor.getInt(categoryCursor.getColumnIndexOrThrow("isCollapsed")) == 1;

                Category category = new Category(categoryName, categoryColor);
                category.setIsCollapsed(isCollapsed);
                categoryMap.put(categoryName, category);

            } while (categoryCursor.moveToNext());
            categoryCursor.close();
        }

        // Load Exercises
        Cursor exerciseCursor = db.query(TABLE_EXERCISE, null, null, null, null, null, null);
        if (exerciseCursor != null && exerciseCursor.moveToFirst()) {
            do {
                String exerciseName = exerciseCursor.getString(exerciseCursor.getColumnIndexOrThrow(KEY_EXERCISE_NAME));
                String categoryName = exerciseCursor.getString(exerciseCursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME_FK));
                String setType = exerciseCursor.getString(exerciseCursor.getColumnIndexOrThrow(KEY_SET_TYPE));
                boolean doneToday = exerciseCursor.getInt(exerciseCursor.getColumnIndexOrThrow(KEY_DONE_TODAY)) == 1;

                // Parse JSON data
                String weightRepsJson = exerciseCursor.getString(exerciseCursor.getColumnIndexOrThrow(KEY_WEIGHT_REPS));
                String durationsJson = exerciseCursor.getString(exerciseCursor.getColumnIndexOrThrow(KEY_DURATIONS));
                List<int[]> weightReps = parseWeightRepsFromJson(weightRepsJson);
                List<Integer> durations = parseDurationsFromJson(durationsJson);

                Exercise exercise = new Exercise(exerciseName, null, setType);
                exercise.setDoneToday(doneToday);
                exercise.setWeightReps(weightReps);
                exercise.setDurations(durations);

                String exerciseNotes = exerciseCursor.getString(exerciseCursor.getColumnIndexOrThrow("exerciseNotes"));
                exercise.setExerciseNotes(exerciseNotes);

                long doneTime = exerciseCursor.getLong(exerciseCursor.getColumnIndexOrThrow("doneTime"));
                exercise.setDoneTime(doneTime);

                if (categoryMap.containsKey(categoryName)) {
                    categoryMap.get(categoryName).addExercise(exercise);
                }
            } while (exerciseCursor.moveToNext());
            exerciseCursor.close();
        }

        return categoryMap;
    }

    private List<int[]> parseWeightRepsFromJson(String json) {
        List<int[]> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray pair = jsonArray.getJSONArray(i);
                list.add(new int[]{pair.getInt(0), pair.getInt(1)});
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Integer> parseDurationsFromJson(String json) {
        List<Integer> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getCatColour(String categoryName) {
        SQLiteDatabase db = getReadableDatabase();
        int categoryColor = -1; // Default value if no category is found

        String query = "SELECT " + KEY_CATEGORY_COLOR + " FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryName});

        if (cursor != null && cursor.moveToFirst()) {
            categoryColor = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CATEGORY_COLOR));
            cursor.close();
        }

        return categoryColor;
    }


}