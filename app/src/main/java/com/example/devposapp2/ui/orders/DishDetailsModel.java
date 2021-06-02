package com.example.devposapp2.ui.orders;

import java.io.Serializable;

public class DishDetailsModel implements Serializable {
    String dishName;
    String price;
    String quantity;

    public DishDetailsModel() {

    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

