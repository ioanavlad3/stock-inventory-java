package src.main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String FILE_CAT = "src/main/java/categories.txt";
    private static final String FILE_PROD = "src/main/java/products.txt";
    private static final String FILE_DIS = "src/main/java/distributors.txt";
    private static final String FILE_EMP = "src/main/java/employees.txt";

    public static void main(String[] args) {
        // 1. Load Data
        List<Category> categories = readCategory();
        List<Product> products = loadInventory();
        List<Distributor> distributors = loadDistributors();
        List<Employee> employees = loadEmployees();

        // 2. Setup ServiceManager
        ServiceManager serviceManager = ServiceManager.getInstace();
        serviceManager.setCategoryList(categories);
        serviceManager.setDistributors(distributors);

        Deposit warehouse = new Deposit(1000, 1000, 1);
        serviceManager.setStorageSpace(warehouse);

        Store store = new Store(500, 500, 1, "Lidl");

        // add products to the deposit
        for(Product p : products){
            warehouse.addProduct(p, 0, 100); // Initial stock of 100 for each product in the deposit
        }
        runMenu(serviceManager, products, employees, store);
    }

    private static void runMenu(ServiceManager manager, List<Product> products, List<Employee> employees, Store store) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Display all products in Warehouse");
            System.out.println("2. Display all categories");
            System.out.println("3. Restock product (Supply Transaction)");
            System.out.println("4. Transfer product to Store (Internal Transaction)");
            System.out.println("5. Sell product (Out Transaction)");
            System.out.println("6. Display best-selling products");
            System.out.println("7. Display total profit");
            System.out.println("8. Calculate total sales for a specific day");
            System.out.println("9. Apply expiration discounts (Perishable products)");
            System.out.println("10. Find best/cheapest distributor for a product");
            System.out.println("11. Check global stock for a product");
            System.out.println("12. Filter products by category");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    System.out.println("\n--- Warehouse Inventory ---");
                    manager.displayAllProducts();
                }
                case 2 -> {
                    System.out.println("\n--- Categories ---");
                    manager.printAllCategories();
                }
                case 3 -> handleRestock(manager, products, employees, scanner);
                case 4 -> handleTransfer(manager, products, employees, store, scanner);
                case 5 -> handleSale(manager, products, employees, store, scanner);
                case 6 -> {
                    System.out.println("\n--- Best Selling Products ---");
                    List<Product> bestSellers = manager.getBestSellingProducts();
                    if (bestSellers.isEmpty()) System.out.println("No sales recorded yet.");
                    else bestSellers.forEach(p -> System.out.println("- " + p.getName()));
                }
                case 7 -> System.out.println("\nTotal Profit: " + manager.getTotalProfit());
                case 8 -> handleDailySales(manager, scanner);
                case 9 -> {
                    System.out.println("\n--- Applying Expiration Discounts ---");
                    manager.applyExpirationDiscount();
                    System.out.println("Discounts applied to eligible perishable products.");
                }
                case 10 -> handleDistributorCheck(manager, scanner);
                case 11 -> handleGlobalStockCheck(manager, products, scanner);
                case 12 -> handleCategoryFilter(manager, scanner);
                case 0 -> {
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
        scanner.close();
    }

    private static void handleRestock(ServiceManager manager, List<Product> products, List<Employee> employees, Scanner scanner) {
        System.out.print("Enter employee name (ADMIN, MANAGER) for restocking: ");
        String empName = scanner.nextLine();
        Employee emp = employees.stream().filter(e -> e.getName().equalsIgnoreCase(empName.trim()))
                .findFirst().orElse(null);

        System.out.print("Enter product name: ");
        String prodName = scanner.nextLine();
        Product prod = products.stream().filter(p -> p.getName().equalsIgnoreCase(prodName.trim()))
                .findFirst().orElse(null);

        if (emp == null || prod == null) {
            System.out.println("Error: Employee or Product not found.");
            return;
        }

        System.out.print("Enter amount to restock: ");
        int amount = Integer.parseInt(scanner.nextLine());
        manager.restockProduct(emp, prod, amount);
    }

    private static void handleTransfer(ServiceManager manager, List<Product> products, List<Employee> employees, Store store, Scanner scanner) {
        System.out.print("Enter employee name (ADMIN, MANAGER) for transfer: ");
        String empName = scanner.nextLine();
        Employee emp = employees.stream().filter(e -> e.getName().equalsIgnoreCase(empName.trim()))
                .findFirst().orElse(null);

        System.out.print("Enter product name: ");
        String prodName = scanner.nextLine();
        Product prod = products.stream().filter(p -> p.getName().equalsIgnoreCase(prodName.trim()))
                .findFirst().orElse(null);

        if (emp == null || prod == null) {
            System.out.println("Error: Employee or Product not found.");
            return;
        }

        System.out.print("Enter amount to transfer to store: ");
        int amount = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter shelf location ID: ");
        int shelf = Integer.parseInt(scanner.nextLine());

        manager.transferToStore(emp, prod, amount, shelf, store);
    }

    private static void handleSale(ServiceManager manager, List<Product> products, List<Employee> employees, Store store, Scanner scanner) {
        System.out.print("Enter employee name (CASHIER) authorizing the sale: ");
        String empName = scanner.nextLine();
        Employee emp = employees.stream().filter(e -> e.getName().equalsIgnoreCase(empName.trim()))
                .findFirst().orElse(null);

        System.out.print("Enter product name: ");
        String prodName = scanner.nextLine();
        Product prod = products.stream().filter(p -> p.getName().equalsIgnoreCase(prodName.trim()))
                .findFirst().orElse(null);

        if (emp == null || prod == null) {
            System.out.println("Error: Employee or Product not found.");
            return;
        }

        System.out.print("Enter amount sold: ");
        int amount = Integer.parseInt(scanner.nextLine());
        manager.sellProduct(emp, prod, amount, store);
    }

    private static void handleDailySales(ServiceManager manager, Scanner scanner) {
        try {
            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            System.out.println("Total revenue for " + date + ": " + manager.getTotalPriceDay(date));
        } catch (Exception e) {
            System.out.println("Invalid date format.");
        }
    }

    private static void handleDistributorCheck(ServiceManager manager, Scanner scanner) {
        System.out.print("Enter product name to check distributors: ");
        String prodName = scanner.nextLine();
        Distributor best = manager.getBestDistributor(prodName);
        Distributor cheap = manager.getCheapestDistributor(prodName);

        if (cheap != null) System.out.println("Cheapest Distributor: " + cheap.getCodeUnique()
                + " Price: " + cheap.getPrice(prodName));
        if (best != null) System.out.println("Best Rated Distributor: " + best.getCodeUnique());
        if (cheap == null) System.out.println("No distributor found for this product.");
    }

    private static void handleGlobalStockCheck(ServiceManager manager, List<Product> products, Scanner scanner) {
        System.out.print("Enter product name: ");
        String prodName = scanner.nextLine();
        Product prod = products.stream().filter(p -> p.getName().equalsIgnoreCase(prodName.trim()))
                .findFirst().orElse(null);

        if (prod != null) {
            System.out.println("Global Stock for " + prod.getName() + ": " + manager.getGlobalProductStock(prod));
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void handleCategoryFilter(ServiceManager manager, Scanner scanner) {
        System.out.print("Enter Category Type (e.g., ELECTRONICS, PERISHABLE): ");
        String catStr = scanner.nextLine().toUpperCase().trim();
        try {
            Category.Type type = Category.Type.valueOf(catStr);
            manager.getProductsByCategory(type);
        } catch (Exception e) {
            System.out.println("Invalid Category Type.");
        }
    }

    // reading methods
    public static List<Category> readCategory() {
        List<Category> categories = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_CAT))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                categories.add(new Category(tokens[0].trim(), tokens[1].trim()));
            }
        } catch (IOException e){ System.err.println("File error: " + e.getMessage()); }
        return categories;
    }

    public static List<Product> loadInventory() {
        List<Product> myProducts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PROD))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0], name = data[1], desc = data[2];
                double pPrice = Double.parseDouble(data[3]), sPrice = Double.parseDouble(data[4]);
                Category.Type cat = Category.Type.valueOf(data[5]);
                if (type.equals("PERISHABLE")) {
                    myProducts.add(new PerishableProduct(name, desc, pPrice, sPrice, LocalDate.parse(data[6]), Integer.parseInt(data[7]), cat));
                } else {
                    myProducts.add(new ElectronicProduct(name, desc, pPrice, sPrice, Integer.parseInt(data[6]), data[7].charAt(0), Integer.parseInt(data[8]), cat));
                }
            }
        } catch (IOException e) { System.err.println("File error: " + e.getMessage()); }
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
                int term = Integer.parseInt(parts[1]);
                HashMap<String, Float> catalog = new HashMap<>();
                for (String item : parts[2].split(",")) {
                    String[] entry = item.split(":");
                    catalog.put(entry[0].trim(), Float.parseFloat(entry[1].trim()));
                }
                distributors.add(new Distributor(rating, term, catalog));
            }
        } catch (IOException e) { System.err.println("File error: " + e.getMessage()); }
        return distributors;
    }

    public static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_EMP))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                employees.add(new Employee(data[0].trim(), Employee.Role.valueOf(data[1].trim().toUpperCase()), Double.parseDouble(data[2].trim())));
            }
        } catch (IOException e) { System.err.println("File error: " + e.getMessage()); }
        return employees;
    }
}