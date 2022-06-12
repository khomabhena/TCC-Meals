package com.kmab.tcc.food.orders;

public class SetterDeliveryArea {

    public SetterDeliveryArea() {
    }

    private String key, area, description;
    private double charge;
    private boolean isAvailable, isClub;

    public SetterDeliveryArea(String key, String area, String description, double charge,
                              boolean isAvailable, boolean isClub) {
        this.key = key;
        this.area = area;
        this.description = description;
        this.charge = charge;
        this.isAvailable = isAvailable;
        this.isClub = isClub;
    }

    public boolean isClub() {
        return isClub;
    }

    public void setClub(boolean club) {
        isClub = club;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
