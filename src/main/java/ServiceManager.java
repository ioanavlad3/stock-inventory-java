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

    public Distributor getCheapestDistributor(String product){
        return distributorList.stream()
                .filter(d -> d.getPrice(product) != null)
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

}
