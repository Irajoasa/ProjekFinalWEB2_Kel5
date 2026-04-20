package com.example.productcrud.model;

public enum Category {
    ELEKTRONIK("Elektronik"),
    BUKU("Buku"),
    MAKANAN("Makanan"),
    PAKAIAN("Pakaian");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromIndex(Integer index) {
        if (index == null || index < 0 || index >= values().length) {
            return null;
        }
        return values()[index];
    }
}
