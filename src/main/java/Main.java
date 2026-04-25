package src.main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static final String FILE_CAT = "src/main/java/categories.txt";
    private static final String FILE_PROD = "src/main/java/products.txt";
    private static final String FILE_DIS = "src/main/java/distributors.txt";

    public static void main(String[] args){
        List<Category> categories = readCategory();
        List <Product> products = loadInventory();
        List <Distributor> distributors = loadDistributors();

        ServiceManager serviceManager = ServiceManager.getInstace();
        serviceManager.setCategoryList(categories);
        serviceManager.setDistributors(distributors);
        StorageSpace warehouse = new StorageSpace(1000, 1000, 5);
        serviceManager.setStorageSpace(warehouse);

        InventoryManager inventoryManager = new InventoryManager(warehouse, new ArrayList<>(distributors));
        for(Product p : products){
            inventoryManager.restockProduct(p, 50);
        }

    }

    public static List<Category> readCategory() {
        List<Category> categories = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_CAT))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                String name = tokens[0].trim();
                String description = tokens[1].trim();
                Category cat = new Category(name, description);
                categories.add(cat);
            }
        } catch (IOException e){
            System.err.println("Error parsing file: " + e.getMessage());
        }
        return categories;
    }

    public static List<Product> loadInventory() {
        List<Product> myProducts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PROD))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0];
                String name = data[1];
                String desc = data[2];
                double pPrice = Double.parseDouble(data[3]);
                double sPrice = Double.parseDouble(data[4]);
                Category.Type cat = Category.Type.valueOf(data[5]);

                Product product;
                if (type.equals("PERISHABLE")) {
                    LocalDate expDate = LocalDate.parse(data[6]); // Format YYYY-MM-DD
                    int temp = Integer.parseInt(data[7]);

                    product = new PerishableProduct(name, desc, pPrice, sPrice, expDate, temp, cat);
                } else {
                    int warranty = Integer.parseInt(data[6]);
                    char energyClass = data[7].charAt(0);
                    int power = Integer.parseInt(data[8]);

                    product = new ElectronicProduct(name, desc, pPrice, sPrice, warranty, energyClass, power, cat);
                }

                myProducts.add(product);
            }
        } catch (IOException e) {
            System.err.println("Error parsing file: " + e.getMessage());
        }
        return myProducts;
    }

    public static List<Distributor> loadDistributors() {
        List<Distributor> distributors = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_DIS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");
                float rating = Float.parseFloat(parts[0]);
                int paymentTerm = Integer.parseInt(parts[1]);
                String catalogData = parts[2];

                HashMap<String, Float> catalog = new HashMap<>();
                String[] items = catalogData.split(",");

                for (String item : items) {
                    String[] entry = item.split(":");
                    String productName = entry[0].trim();
                    float price = Float.parseFloat(entry[1].trim());
                    catalog.put(productName, price);
                }

                distributors.add(new Distributor(rating, paymentTerm, catalog));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return distributors;
    }
}
