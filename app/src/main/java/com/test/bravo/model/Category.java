package com.test.bravo.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Category {
    private String name;
    private int color;
    private List<Exercise> exercises;
    private boolean isCollapsed;

    public Category(String name, int color) {
        this.name = name;
        this.color = color;
        this.exercises = new ArrayList<>();
        this.isCollapsed = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsCollapsed() {
        return isCollapsed;
    }

    public void setIsCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        this.exercises.remove(exercise);
    }

    public boolean hasExercise(String exerciseName) {
        for (Exercise exercise : exercises) {
            if (exercise.getName().equalsIgnoreCase(exerciseName)) {
                return true;
            }
        }
        return false;
    }

    public Exercise getExercise(String exerciseName) {
        for (Exercise exercise : exercises) {
            if (exercise.getName().equalsIgnoreCase(exerciseName)) {
                return exercise;
            }
        }
        return null; // Return null if no exercise with the given name is found
    }
}
