package com.example.mobil_proje;
public class DataHolder {
    private static DataHolder instance;
    private Foods selectedFood;
    private DataHolder() {}
    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }
    public Foods getSelectedFood() {
        return selectedFood;
    }
    public void setSelectedFood(Foods selectedFood) {
        this.selectedFood = selectedFood;
    }
}
