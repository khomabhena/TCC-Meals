package com.kmab.tcc.food.orders;

public class SetterMerchant {

    public SetterMerchant() {
    }

    private String name;
    private long code;

    public SetterMerchant(String name, long code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
