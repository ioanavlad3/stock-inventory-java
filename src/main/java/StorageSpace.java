package src.main.java;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public abstract class StorageSpace {
    protected final int maxVolume;
    protected final int maxWeight;
    protected double capacityWeightRemain;
    protected double capacityVolumeRemain;

    // Maps each product to a map of locations (shelf/district) and their quantities
    protected Map<Product, Map<Integer, Integer>> inventory = new HashMap<>();

    public StorageSpace(int maxVolume, int maxWeight) {
        this.maxVolume = maxVolume;
        this.maxWeight = maxWeight;
        this.capacityWeightRemain = maxWeight;
        this.capacityVolumeRemain = maxVolume;
    }

    public void addProduct(Product p, int location, int amount) {
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
        locations.put(location, locations.getOrDefault(location, 0) + amount);

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
        for (Integer location : new ArrayList<>(locations.keySet())) {
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
}