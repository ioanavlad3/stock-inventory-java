package src.main.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceManager {
    private static ServiceManager INSTANCE = null;
    private List<Distributor> distributorList = new ArrayList<>();
    private StorageSpace ss;
    private List<Transaction> transactionHistory = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    private ServiceManager() {}

    public static ServiceManager getInstace() {
        if(INSTANCE == null) {
            INSTANCE =  new ServiceManager();
            return INSTANCE;
        }
        return INSTANCE;
    }

    public Distributor getBestDistributor(String product) {
        float bestScore = 0;
        Distributor bestDistributor = null;
        for (Distributor d : distributorList) {
            float score = d.getRating() / d.getPrice(product);
            if (score > bestScore) {
                bestScore = score;
                bestDistributor = d;
            }
        }
        return bestDistributor;
    }

    public void setDistributors(List<Distributor> distributors) {
        this.distributorList = distributors;
    }

    public void setStorageSpace(StorageSpace storageSpace) {
        this.ss = storageSpace;
    }

    public void setCategoryList(List<Category> categories){
        this.categoryList = categories;
    }

    public Distributor getCheapestDistributor(String product){
        return distributorList.stream()
                .filter(d -> d.getPrice(product) != null && d.getPrice(product) > 0)
                .min(Comparator.comparingDouble(d -> d.getPrice(product)))
                .orElse(null);
    }

    public List<Distributor> getDistributorForProduct(String product){
        return distributorList.stream()
                .filter(d -> d.getPrice(product) != null)
                .collect(Collectors.toCollection(ArrayList<Distributor>::new));
    }

    public int getGlobalProductStock(Product p){
        return ss.getTotalProductStock(p);
    }

    public void displayAllProducts(){
        ss.displayAllProducts();
    }


    public void recordTransaction(Transaction transaction){
        this.transactionHistory.add(transaction);
        System.out.println("Transaction recorded at " + LocalDateTime.now());
    }

    public List<Product> getBestSellingProducts(){
        List<Product> bestSelling = new ArrayList<>();
        // map with every product that have been sold
        Map<Product, Integer> sells = new HashMap<>();
        for (Transaction t : transactionHistory){
            if (t instanceof OutTransaction){
                Product p = t.getProduct();
                int amount = t.getAmount();
                sells.put(p, sells.getOrDefault(p, 0) + amount);
            }
        }
        return sells.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey) // extract only the product
                .collect(Collectors.toList());
    }

    // the total amount of money made in a specific day
    public double getTotalPriceDay(LocalDate date) {
        double sum = 0;
        for (Transaction t : transactionHistory){
            LocalDateTime t_date = t.getDateTime();
            if (t_date.getYear() == date.getYear()
                    && t_date.getMonth() == date.getMonth()
                    && t_date.getDayOfMonth() == date.getDayOfMonth()
                ){
                sum +=  (t.getAmount() * t.getProduct().getFinalPrice());
            }
        }
        return sum;
    }

    public void applyExpirationDiscount(){
        for(Product p : ss.getProducts()){
            if (p instanceof PerishableProduct){
                ((PerishableProduct) p).applyExpirationDiscount();
            }
        }
    }

    // total profit = sell price - tva - buy price
    public double getTotalProfit(){
        double totalProfit = 0;
        for(Transaction t : transactionHistory){
            if(t instanceof OutTransaction){
                Product p = t.getProduct();
                int amount = t.getAmount();
                totalProfit += p.calculateProfit() * amount;
            }

        }
        return totalProfit;
    }

    public void getProductsByCategory(Category.Type category){
        ss.getProducts().stream().filter(p -> p.getCategory().equals(category))
                .forEach(p -> System.out.println(p.toString()));
    }

    public void printAllCategories() {
        Map<Category.Type, List<Product>> prodByCat = new HashMap<>();

        for (Product p : ss.getProducts()) {
            Category.Type cat = p.getCategory();

            prodByCat.putIfAbsent(cat, new ArrayList<>());

            prodByCat.get(cat).add(p);
        }

        int i = 1;
        for (Map.Entry<Category.Type, List<Product>> entry : prodByCat.entrySet()) {
            System.out.println(i + ": " + entry.getKey().getName());
            i++;
            for (Product p : entry.getValue()) {
                System.out.println("   - " + p.getName());
            }
            System.out.println();
        }
    }

    // from where to buy
    public void restockProduct(Employee employee, Product product, int amount) {
        Distributor cheapest = getCheapestDistributor(product.getName());

        if (cheapest != null) {
            int optimalLocation = ss.findLocationForProduct(product);
            SupplyTransaction supplyTxt = new SupplyTransaction(
                    employee, (Deposit) ss, optimalLocation, amount, product, optimalLocation
            );

            try {
                supplyTxt.execute();
                recordTransaction(supplyTxt);
                System.out.println("Restock successful from " + cheapest.getCodeUnique());
            } catch (Exception e) {
                System.out.println("Restock failed: " + e.getMessage());
            }

        } else {
            System.out.println("Product " + product.getName() + " not found at any distributor.");
        }
    }

    // add manually
    public void addProductToWarehouse(Product product, int location, int amount) {
        try {
            ss.addProduct(product, location, amount);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void transferToStore(Employee employee, Product product, int amount, int shelfLocation, Store store) {
        if (!(ss instanceof Deposit)) {
            System.out.println("Error: Current storage is not a Deposit. Cannot perform internal transfer.");
            return;
        }
        int currentStock = ss.getTotalProductStock(product);
        if (currentStock < amount) {
            System.out.println("Error: Insufficient stock in deposit. Available: " + currentStock);
            return;
        }

        InternalTransaction transferTxt = new InternalTransaction(
                employee, (Deposit) ss, product, amount, shelfLocation, store
        );

        try {
            transferTxt.execute();
            recordTransaction(transferTxt);

            System.out.println("Successfully transferred " + amount + " units to Store shelf " + shelfLocation);
        } catch (SecurityException e) {
            System.out.println("Permission denied: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    public void sellProduct(Employee employee, Product product, int amount, Store store) {
        if (store.getTotalProductStock(product) < amount) {
            System.out.println("Error: Not enough stock on store shelves for " + product.getName());
            return;
        }
        OutTransaction salesTxt = new OutTransaction(
                employee, (Deposit) ss, product, amount, 0, store
        );

        try {
            salesTxt.execute();

            recordTransaction(salesTxt);

            System.out.println("Successfully sold " + amount + " units of " + product.getName());
            System.out.println("Total price: " + (amount * product.getFinalPrice()));

        } catch (SecurityException e) {
            System.out.println("Access Denied: Only Managers/Admins can finalize sales. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }

}
