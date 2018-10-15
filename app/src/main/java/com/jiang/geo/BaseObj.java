package com.jiang.geo;

public class BaseObj {

    protected int type; // 1 construction 2 people

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isPeople() {
        return type == 2;
    }

    public boolean isConstuctions() {
        return type == 1;
    }
}
