package src.main.java;


import java.util.ArrayList;

public class Category {

    public enum Type {
        DAIRY("Dairy Products"),
        BAKERY("Bakery Items"),
        FRUITS("Fresh Fruits"),
        VEGETABLES("Fresh Vegetables"),
        ELECTRONICS("Electronics");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String getName() {
            return displayName;
        }
    }

    private String name;
    private String description;


    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }


    @Override
    public String toString(){
        return "Category{name='" + name + "', description='" + description + "'}";
    }


}
