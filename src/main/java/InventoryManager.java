package src.main.java;

import java.util.ArrayList;

public class InventoryManager {
    private ArrayList<Distributor> listDistributor = new ArrayList<>();
    private final StorageSpace warehouse;


    public InventoryManager(StorageSpace warehouse) {
        this.warehouse = warehouse;
    }

    public InventoryManager(StorageSpace warehouse, ArrayList<Distributor> listDistributor) {
        this.warehouse = warehouse;
        this.listDistributor = new ArrayList<>(listDistributor);
    }

    public void addDistributor(Distributor distributor) {
        listDistributor.add(distributor);
    }

    // method that returns from where to buy
    public void restockProduct(Product product, int amount) {
        float minPrice = Float.MAX_VALUE;
        Distributor distributor = null;
        String productName = product.getName();
        for(Distributor d : listDistributor) {
            float price = d.getPrice(productName);
            if (price != -1){
                if(price < minPrice){
                    minPrice = price;
                    distributor = d;
                }

            }
        }

        if(distributor != null) {
            System.out.println("Restocking " + amount + " units of " + productName +
                    " from distributor with code: " + distributor.getCodeUnique());
            addProductToOptimalLocation(product, amount);

        } else {
            System.out.println("Product " + productName + " not found in any distributor's catalog.");
        }
    }

    // Add product to warehouse without specific location


    // Add products to warehouse with specific location
    public void addProductToWarehouse(Product product, int location, int amount) {
        try {
            warehouse.addProduct(product, location, amount);
            System.out.println("Successfully added " + amount + " units of "
                    + product.getName() + " at location " + location + ".");
        } catch (WeightLimitExceededException | VolumeLimitExceededException e) {
            System.out.println("Error adding product: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addProductToOptimalLocation(Product product, int amount) {
        warehouse.addProductToOptimalLocation(product, amount);
    }

}
