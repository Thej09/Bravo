package com.test.bravo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

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
    private static final int DATABASE_VERSION = 11;

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

    private static final String TABLE_PLANNED_EXERCISES = "PlannedExercises";
    private static final String KEY_PLANNED_DATE = "planned_date";
    private static final String KEY_PLANNED_EXERCISE_NAME = "exercise_name";
    private static final String KEY_PLANNED_SET_TYPE = "set_type";
    private static final String KEY_PLANNED_WEIGHT_REPS = "weight_reps";
    private static final String KEY_PLANNED_DURATIONS = "durations";
    private static final String KEY_PLANNED_ADDED_TIME = "added_time";

    private static final String TABLE_ROUTINES = "routines";
    private static final String KEY_ROUTINE_NAME = "routine_name";
    private static final String KEY_EXERCISE_DATA = "exercise_data";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " (" +
                KEY_CATEGORY_NAME + " TEXT PRIMARY KEY, " +
                KEY_CATEGORY_COLOR + " INTEGER, " +
                "isCollapsed INTEGER)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        String CREATE_EXERCISE_TABLE = "CREATE TABLE " + TABLE_EXERCISE + " (" +
                KEY_EXERCISE_NAME + " TEXT PRIMARY KEY, " +
                KEY_CATEGORY_NAME_FK + " TEXT, " +
                KEY_SET_TYPE + " TEXT, " +
                KEY_DONE_TODAY + " INTEGER, " +
                "completed INTEGER, " +  // New field for boolean completed
                "completionTime INTEGER, " +  // New field for long completionTime
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
                "exerciseColour INTEGER, " +
                "completed INTEGER, " +
                "completionTime INTEGER, " +
                "PRIMARY KEY (" + KEY_DATE + ", " + KEY_EXERCISE_NAME_DAILY + "))";
        db.execSQL(CREATE_DAILY_ACTIVITY_TABLE);
        // HERE

        String CREATE_PLANNED_EXERCISES_TABLE = "CREATE TABLE " + TABLE_PLANNED_EXERCISES + " (" +
                KEY_PLANNED_DATE + " TEXT, " +
                KEY_PLANNED_EXERCISE_NAME + " TEXT, " +
                KEY_PLANNED_SET_TYPE + " TEXT, " +
                KEY_PLANNED_WEIGHT_REPS + " TEXT, " +
                KEY_PLANNED_DURATIONS + " TEXT, " +
                "categoryClr INTEGER, " + // Add categoryClr
                "categoryName TEXT, " +  // Add categoryName
                KEY_PLANNED_ADDED_TIME + " INTEGER, " +
                "PRIMARY KEY (" + KEY_PLANNED_DATE + ", " + KEY_PLANNED_EXERCISE_NAME + "))";
        db.execSQL(CREATE_PLANNED_EXERCISES_TABLE);

        String CREATE_ROUTINES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTINES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ROUTINE_NAME + " TEXT NOT NULL UNIQUE, " + // Add UNIQUE constraint
                KEY_EXERCISE_DATA + " TEXT NOT NULL" +
                ")";
        db.execSQL(CREATE_ROUTINES_TABLE);
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
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLE_DAILY_ACTIVITY + " ADD COLUMN exerciseColour INTEGER");
        }
        if (oldVersion < 7) { // Assuming version 7 for the new fields
            db.execSQL("ALTER TABLE " + TABLE_EXERCISE + " ADD COLUMN completed INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_EXERCISE + " ADD COLUMN completionTime INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_DAILY_ACTIVITY + " ADD COLUMN completed INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_DAILY_ACTIVITY + " ADD COLUMN completionTime INTEGER DEFAULT 0");
        }
        if (oldVersion < 8) { // Assuming the new version is 8
            String CREATE_PLANNED_EXERCISES_TABLE = "CREATE TABLE " + TABLE_PLANNED_EXERCISES + " (" +
                    KEY_PLANNED_DATE + " TEXT, " +
                    KEY_PLANNED_EXERCISE_NAME + " TEXT, " +
                    KEY_PLANNED_SET_TYPE + " TEXT, " +
                    KEY_PLANNED_WEIGHT_REPS + " TEXT, " +
                    KEY_PLANNED_DURATIONS + " TEXT, " +
                    KEY_PLANNED_ADDED_TIME + " INTEGER, " +
                    "PRIMARY KEY (" + KEY_PLANNED_DATE + ", " + KEY_PLANNED_EXERCISE_NAME + "))";
            db.execSQL(CREATE_PLANNED_EXERCISES_TABLE);
        }
        if (oldVersion < 9) { // Assuming version 9 introduces these changes
            db.execSQL("ALTER TABLE " + TABLE_PLANNED_EXERCISES + " ADD COLUMN categoryClr INTEGER DEFAULT -1");
            db.execSQL("ALTER TABLE " + TABLE_PLANNED_EXERCISES + " ADD COLUMN categoryName TEXT DEFAULT ''");
        }
        if (oldVersion < 10) { // Assuming version 10 introduces the routines table
            String CREATE_ROUTINES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTINES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_ROUTINE_NAME + " TEXT NOT NULL, " +
                    KEY_EXERCISE_DATA + " TEXT NOT NULL" +
                    ")";
            db.execSQL(CREATE_ROUTINES_TABLE);
        }
        if (oldVersion < 11) {
            db.execSQL("ALTER TABLE " + TABLE_ROUTINES + " RENAME TO " + TABLE_ROUTINES + "_old");
            String CREATE_ROUTINES_TABLE = "CREATE TABLE " + TABLE_ROUTINES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_ROUTINE_NAME + " TEXT NOT NULL UNIQUE, " + // Add UNIQUE constraint
                    KEY_EXERCISE_DATA + " TEXT NOT NULL" +
                    ")";
            db.execSQL(CREATE_ROUTINES_TABLE);
            db.execSQL("INSERT INTO " + TABLE_ROUTINES + " (id, " + KEY_ROUTINE_NAME + ", " + KEY_EXERCISE_DATA + ") " +
                    "SELECT id, " + KEY_ROUTINE_NAME + ", " + KEY_EXERCISE_DATA + " FROM " + TABLE_ROUTINES + "_old");
            db.execSQL("DROP TABLE " + TABLE_ROUTINES + "_old");
        }
    }

    // Methods to save and load daily exercises

    public void saveDailyActivity(String date, Exercise exercise, String categoryName, int categoryClr) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_EXERCISE_NAME_DAILY, exercise.getName());
        values.put(KEY_CATEGORY_NAME_DAILY, categoryName);
        values.put(KEY_SET_TYPE_DAILY, exercise.getSetType());
        values.put(KEY_WEIGHT_REPS_DAILY, new JSONArray(exercise.getWeightReps()).toString());
        values.put(KEY_DURATIONS_DAILY, new JSONArray(exercise.getDurations()).toString());
        values.put(KEY_DONE_TIME, exercise.getDoneTime());
        values.put("exerciseColour", categoryClr);
        values.put("completed", exercise.getCompleted() ? 1 : 0); // Save boolean as integer
        values.put("completionTime", exercise.getCompletionTime()); // Save long
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
                " WHERE " + KEY_DATE + " = ? ORDER BY " + "completionTime" + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXERCISE_NAME_DAILY));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME_DAILY));
                String setType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SET_TYPE_DAILY));
                String weightRepsJson = cursor.getString(cursor.getColumnIndexOrThrow(KEY_WEIGHT_REPS_DAILY));
                String durationsJson = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DURATIONS_DAILY));
                long doneTime = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_DONE_TIME));
                int categoryClr = cursor.getInt(cursor.getColumnIndexOrThrow("exerciseColour"));


                List<int[]> weightReps = parseWeightRepsFromJson(weightRepsJson);
                List<Integer> durations = parseDurationsFromJson(durationsJson);

//                Category category = null;

                Exercise exercise = new Exercise(exerciseName, null, setType);
                exercise.setWeightReps(weightReps);
                exercise.setDurations(durations);
                exercise.setDoneTime(doneTime);
                exercise.setCategoryClr(categoryClr);
                exercise.setCategoryName(categoryName);

                boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow("completed")) == 1;
                long completionTime = cursor.getLong(cursor.getColumnIndexOrThrow("completionTime"));

                exercise.setCompleted(completed);
                exercise.setCompletionTime(completionTime);

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
        Log.e("DatabaseHelper1", "Attempt Save");
        Log.e("myTag5", "Oh No");

        try {
            // Clear existing data
            db.delete(TABLE_CATEGORY, null, null);
            db.delete(TABLE_EXERCISE, null, null);

            for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
                String categoryName = entry.getKey();
                Category category = entry.getValue();

                Log.e("DatabaseHelper1", "Category: " + category.getName() + " Colour: " + category.getColor());

                // Insert into Category table
                ContentValues categoryValues = new ContentValues();
                categoryValues.put(KEY_CATEGORY_NAME, category.getName());
                categoryValues.put(KEY_CATEGORY_COLOR, category.getColor());
                categoryValues.put("isCollapsed", category.getIsCollapsed() ? 1 : 0); // Save isCollapsed
                long result = db.insert(TABLE_CATEGORY, null, categoryValues);

                if (result == -1) {
                    Log.e("DatabaseHelper2", "Failed to insert into Category table: " + category.getName());
                } else {
                    Log.e("DatabaseHelper2", "Inserted: " + category.getName());
                }

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

                    exerciseValues.put("completed", exercise.getCompleted() ? 1 : 0); // Save boolean as integer
                    exerciseValues.put("completionTime", exercise.getCompletionTime()); // Save long

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

                boolean completed = exerciseCursor.getInt(exerciseCursor.getColumnIndexOrThrow("completed")) == 1;
                long completionTime = exerciseCursor.getLong(exerciseCursor.getColumnIndexOrThrow("completionTime"));

                exercise.setCompleted(completed);
                exercise.setCompletionTime(completionTime);

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


    public void savePlannedExercise(String date, Exercise exercise) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLANNED_DATE, date);
        values.put(KEY_PLANNED_EXERCISE_NAME, exercise.getName());
        values.put(KEY_PLANNED_SET_TYPE, exercise.getSetType());
        values.put(KEY_PLANNED_WEIGHT_REPS, new JSONArray(exercise.getWeightReps()).toString());
        values.put(KEY_PLANNED_DURATIONS, new JSONArray(exercise.getDurations()).toString());
        values.put("categoryClr", exercise.getCategoryClr()); // Save categoryClr
        values.put("categoryName", exercise.getCategoryName()); // Save categoryName
        values.put(KEY_PLANNED_ADDED_TIME, System.currentTimeMillis());

        db.insertWithOnConflict(TABLE_PLANNED_EXERCISES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deletePlannedExercise(String date, String exerciseName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PLANNED_EXERCISES,
                KEY_PLANNED_DATE + " = ? AND " + KEY_PLANNED_EXERCISE_NAME + " = ?",
                new String[]{date, exerciseName});
    }

    public List<Exercise> getPlannedExercises(String date) {
        SQLiteDatabase db = getReadableDatabase();
        List<Exercise> plannedExercises = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_PLANNED_EXERCISES + " WHERE " + KEY_PLANNED_DATE + " = ? ORDER BY " + KEY_PLANNED_ADDED_TIME + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLANNED_EXERCISE_NAME));
                String setType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLANNED_SET_TYPE));
                String weightRepsJson = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLANNED_WEIGHT_REPS));
                String durationsJson = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLANNED_DURATIONS));
                int categoryClr = cursor.getInt(cursor.getColumnIndexOrThrow("categoryClr"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("categoryName"));
                long addedTime = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PLANNED_ADDED_TIME));

                List<int[]> weightReps = parseWeightRepsFromJson(weightRepsJson);
                List<Integer> durations = parseDurationsFromJson(durationsJson);

                Exercise exercise = new Exercise(exerciseName, null, setType);
                exercise.setWeightReps(weightReps);
                exercise.setDurations(durations);
                exercise.setCategoryClr(categoryClr); // Set categoryClr
                exercise.setCategoryName(categoryName); // Set categoryName
                exercise.setDoneTime(addedTime);

                plannedExercises.add(exercise);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return plannedExercises;
    }

    public void deletePlannedExercisesBeforeDate(String date) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // Delete entries from PlannedExercises where the planned date is less than the specified date
            String deleteQuery = "DELETE FROM " + TABLE_PLANNED_EXERCISES + " WHERE " + KEY_PLANNED_DATE + " < ?";
            db.execSQL(deleteQuery, new String[]{date});
            Log.i("DatabaseHelper", "Deleted planned exercises before date: " + date);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting planned exercises before date: " + date, e);
        } finally {
            db.close();
        }
    }

    // Routine functions

    public void saveRoutine(String routineName, List<String[]> exercises) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTINE_NAME, routineName);
        values.put(KEY_EXERCISE_DATA, serializeRoutineExercises(exercises)); // Serialize exercises to a string
        db.insertWithOnConflict(TABLE_ROUTINES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<String[]> loadRoutine(String routineName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + KEY_EXERCISE_DATA + " FROM " + TABLE_ROUTINES +
                " WHERE " + KEY_ROUTINE_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{routineName});

        if (cursor != null && cursor.moveToFirst()) {
            String serialized = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXERCISE_DATA));
            cursor.close();
            return deserializeRoutineExercises(serialized); // Deserialize string back to list of tuples
        }
        return new ArrayList<>(); // Return empty list if routine not found
    }

    public void deleteRoutine(String routineName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ROUTINES, KEY_ROUTINE_NAME + " = ?", new String[]{routineName});
    }

    private String serializeRoutineExercises(List<String[]> exercises) {
        StringBuilder builder = new StringBuilder();
        for (String[] pair : exercises) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(pair[0]).append(":").append(pair[1]); // Format: exerciseName:categoryName
        }
        return builder.toString();
    }

    private List<String[]> deserializeRoutineExercises(String serialized) {
        List<String[]> exercises = new ArrayList<>();
        if (serialized == null || serialized.isEmpty()) return exercises;

        String[] pairs = serialized.split(",");
        for (String pair : pairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                exercises.add(parts); // Add as (exerciseName, categoryName)
            }
        }
        return exercises;
    }

    public void clearRoutinesTable() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE_ROUTINES, null, null); // Deletes all rows in the routines table
            db.setTransactionSuccessful();
            Log.i("DatabaseHelper", "Routines table cleared successfully.");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while clearing the routines table.", e);
        } finally {
            db.endTransaction();
        }
    }

    public boolean checkIfRoutineExists(String routineName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT 1 FROM " + TABLE_ROUTINES + " WHERE " + KEY_ROUTINE_NAME + " = ?";
            cursor = db.rawQuery(query, new String[]{routineName});
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public List<String> getAllRoutineNames() {
        List<String> routineNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("routines", new String[]{"routine_name"}, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String routineName = cursor.getString(cursor.getColumnIndexOrThrow("routine_name"));
                routineNames.add(routineName);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return routineNames;
    }


}