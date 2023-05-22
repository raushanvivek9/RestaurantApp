package com.example.restaurant.Model;

public class MenuNote {
    private String menu_name;
    private Integer price;
    private String image;

    public MenuNote() {
    }

    public MenuNote(Integer price) {
        this.price = price;
    }

    public MenuNote(String image) {
        this.image = image;
    }

    public MenuNote(String menu_name, Integer price) {
        this.menu_name = menu_name;
        this.price = price;
    }

    public MenuNote(String menu_name, Integer price, String image) {
        this.menu_name = menu_name;
        this.price = price;
        this.image = image;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
