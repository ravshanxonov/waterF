package org.example.waterf.entity.enums;

public enum BottleTypes {
    FIVE("5L",5000),
    TEN("10L",10000),
    TWENTY("20L",20000);

    private String type;
    private Integer price;

    BottleTypes(String type, Integer price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public Integer getPrice() {
        return price;
    }
}
