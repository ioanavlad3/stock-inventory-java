package src.main.java;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public abstract class StorageSpace {
    protected final int maxVolume;
    protected final int maxWeight;
    protected double capacityWeightRemain;
    protected double capacityVolumeRemain;
    protected int currentTemperature;
    // ex:
    // p1 -> r1   10
    // p1 -> r2   20
    // p1 -> r15  30
    // Maps each product to a map of locations (shelf/district) and their quantities
    protected Map<Product, Map<Integer, Integer>> inventory = new HashMap<>();

    public StorageSpace(int maxVolume, int maxWeight, int currentTemperature) {
        this.maxVolume = maxVolume;
        this.maxWeight = maxWeight;
        this.capacityWeightRemain = maxWeight;
        this.capacityVolumeRemain = maxVolume;
        this.currentTemperature = currentTemperature;
    }

    public void addProduct(Product p, int location, int amount) {
        if (p instanceof PerishableProduct pp){
            if (this.currentTemperature > pp.getStorageTemperature()){
                throw new RuntimeException("The Storage is too warm for this product! Required: " +
                        pp.getStorageTemperature() + ", Current: " + this.currentTemperature);
            }
        }

        double totalWeight = p.getWeight() * amount;
        double totalVolume = p.getVolume() * amount;

        if (totalWeight > capacityWeightRemain) {
            throw new WeightLimitExceededException("Limit exceeded. Required: " + totalWeight + ", Available: " + capacityWeightRemain);
        }
        if (totalVolume > capacityVolumeRemain) {
            throw new VolumeLimitExceededException("Limit exceeded. Required: " + totalVolume + ", Available: " + capacityVolumeRemain);
        }

        // Get or create the location map for this product
        inventory.putIfAbsent(p, new HashMap<>());
        Map<Integer, Integer> locations = inventory.get(p);

        // Add or update the quantity at this location
        //locations.put(location, locations.getOrDefault(location, 0) + amount);
        if (location == 0) {
            addProductToOptimalLocation(p, amount);
        }

        capacityWeightRemain -= totalWeight;
        capacityVolumeRemain -= totalVolume;
    }

    public void removeProduct(Product p, int amount) {
        if (!inventory.containsKey(p)) {
            throw new RuntimeException("Product not found in inventory!");
        }

        Map<Integer, Integer> locations = inventory.get(p);
        int totalAmount = locations.values().stream().mapToInt(Integer::intValue).sum();

        if (totalAmount < amount) {
            throw new RuntimeException("Not enough stock available! Required: " + amount + ", Available: " + totalAmount);
        }

        // Remove from locations starting from the first ones
        int remainingToRemove = amount;

        ArrayList<Integer> locationKeys = new ArrayList<>(locations.keySet());
        for (Integer location : locationKeys) {
            int quantityAtLocation = locations.get(location);
            if (quantityAtLocation <= remainingToRemove) {
                remainingToRemove -= quantityAtLocation;
                locations.remove(location);
            } else {
                locations.put(location, quantityAtLocation - remainingToRemove);
                remainingToRemove = 0;
                break;
            }
        }

        // Remove product entry if no quantity left
        if (locations.isEmpty()) {
            inventory.remove(p);
        }

        capacityWeightRemain += p.getWeight() * amount;
        capacityVolumeRemain += p.getVolume() * amount;
    }


    // Check if a location is empty (no products at that location)
    public boolean isLocationEmpty(int location) {
        for (Map<Integer, Integer> locations : inventory.values()) {
            if (locations.containsKey(location)) {
                return false;
            }
        }
        return true;
    }

    // Get total quantity of all products at a specific location
    public int getTotalQuantityAtLocation(int location) {
        int total = 0;
        for (Map<Integer, Integer> locations : inventory.values()) {
            if (locations.containsKey(location)) {
                total += locations.get(location);
            }
        }
        return total;
    }

    // Get quantity of a specific product at a specific location
    public int getQuantityAt(Product p, int location) {
        if (!inventory.containsKey(p)) {
            return 0;
        }
        return inventory.get(p).getOrDefault(location, 0);
    }

    // Display what products are at a specific location
    public void displayProductsAtLocation(int location) {
        System.out.println("\n--- Products at Location " + location + " ---");
        boolean found = false;
        for (Product p : inventory.keySet()) {
            int qty = getQuantityAt(p, location);
            if (qty > 0) {
                System.out.println("  Product: " + p.getName() + " | Quantity: " + qty);
                found = true;
            }
        }
        if (!found) {
            System.out.println("  Location is EMPTY");
        }
    }

    // Find the location with the least amount of total quantity (best for distributing stock)
    public int findLeastOccupiedLocation() {
        int leastOccupiedLocation = 0;
        int minQuantity = Integer.MAX_VALUE;

        // Get all locations currently in use
        for (Map<Integer, Integer> locations : inventory.values()) {
            for (Integer location : locations.keySet()) {
                int totalAtLocation = getTotalQuantityAtLocation(location);
                if (totalAtLocation < minQuantity) {
                    minQuantity = totalAtLocation;
                    leastOccupiedLocation = location;
                }
            }
        }

        // If no products exist yet, return location 1 (to avoid always using 0)
        if (inventory.isEmpty()) {
            return 1;
        }

        return leastOccupiedLocation;
    }

    // Add product to the least occupied location automatically
    public void addProductToOptimalLocation(Product p, int amount) {
        int optimalLocation = findLeastOccupiedLocation();
        addProduct(p, optimalLocation, amount);
        System.out.println("Product " + p.getName() + " added to location " + optimalLocation);
    }
}