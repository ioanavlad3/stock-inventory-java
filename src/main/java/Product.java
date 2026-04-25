package src.main.java;

import java.util.Objects;
import java.util.Random;

public abstract class Product {
    private static int counter = 1000;

    private final String id;
    protected String name;
    protected String description;
    protected double purchasePrice;
    protected double salePrice;
    //protected int minimumStock;
    protected double volume;
    protected double weight;
    protected Category category;

    protected Product(String name, String description, double purchasePrice, double salePrice,
                      Category category) {
        this.id = generateUniqueId();
        this.name = name;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.category = category;
    }

    private String generateUniqueId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // lg between 3 and 5
        int lengthId = random.nextInt(3) + 3;

        for(int i = 0; i < 3; i++){
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString() + (counter++);
    }

    public double calculateProfit() {
        return this.salePrice - this.purchasePrice;
    }

    public double getVolume(){
        return this.volume;
    }

    public double getWeight() {
        return this.weight;
    }

    public double getPurchasePrice(){
        return this.purchasePrice;
    }

    public abstract double getFinalPrice();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Product p = (Product) o;
        return Objects.equals(this.id, p.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString(){
        return "Product{name='" + name + "', description='" + description + "'}";
    }
}