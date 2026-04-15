package src.main.java;

public class Store extends StorageSpace {
    private String storeName;

    public Store(int capacityVolume, int capacityWeight, String storeName) {
        super(capacityVolume, capacityWeight);
        this.storeName = storeName;
    }

    public void displayShelves() {
        System.out.println("Displaying stock for Store: " + storeName);
        inventory.forEach((product, locations) -> {
            locations.forEach((shelf, qty) -> {
                System.out.println("Shelf: " + shelf + " | Product: " + product.getName() + " | Qty: " + qty);
            });
        });
    }
}