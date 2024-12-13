package com.test.bravo.model;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private String name;
    private Category category;
    private String categoryName;
    private int categoryClr;
    private String setType;
    private boolean doneToday;
    private List<int[]> weightReps; // List of pairs (weight, reps)
    private List<Integer> durations; // List of durations
    private String exerciseNotes;
    private long doneTime;
    private int timerMins;
    private int timerSecs;

    public Exercise(String name, Category category, String setType) {
        this.name = name;
        this.category = category;
        this.setType = setType;
        this.categoryName = "";
        this.categoryClr = 1;
        this.weightReps = new ArrayList<>();
        this.durations = new ArrayList<>();
        this.doneToday = false;
        this.exerciseNotes = "";
        this.doneTime = 0;
        this.timerMins = 3;
        this.timerSecs = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTimerMins(){
        return this.timerMins;
    }

    public int getTimerSecs(){
        return this.timerSecs;
    }

    public void setTimerMins(int timerMins){
        this.timerMins = timerMins;
    }

    public void setTimerSecs(int timerSecs){
        this.timerSecs = timerSecs;
    }

    public int getCategoryClr() {
        return categoryClr;
    }

    public void setCategoryClr(int clr) {
        this.categoryClr = clr;
    }

    public String getExerciseNotes() {
        return exerciseNotes;
    }

    public void setExerciseNotes(String exerciseNotes) {
        this.exerciseNotes = exerciseNotes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public void setDoneToday(boolean doneToday) {
        this.doneToday = doneToday;
        if (doneToday) {
            this.doneTime = System.currentTimeMillis();
        } else {
            this.doneTime = 0; // Reset timestamp when unchecked
        }
    }

    public boolean getDoneToday() {
        return this.doneToday;
    }

    public List<int[]> getWeightReps() {
        return weightReps;
    }

    public void addWeightReps(int weight, int reps) {
        this.weightReps.add(new int[]{weight, reps});
    }

    public void setWeightReps(List<int[]> weightReps) {
        this.weightReps = weightReps;
    }

    public List<Integer> getDurations() {
        return durations;
    }

    public long getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(long doneTime) {
        this.doneTime = doneTime;
    }

    public void addDuration(int duration) {
        this.durations.add(duration);
    }

    public void setDurations(List<Integer> durations) {
        this.durations = durations;
    }
}
