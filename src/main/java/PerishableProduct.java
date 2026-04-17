package src.main.java;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PerishableProduct extends Product implements Taxable{
    private final LocalDate expirationDate;
    // the temperature that the product must be stored at
    private final int storageTemperature;

    protected PerishableProduct(String name, String description, double purchasePrice,
                                double salePrice, LocalDate expirationDate, int storageTemperature) {
        super(name, description, purchasePrice, salePrice);
        this.expirationDate = expirationDate;
        this.storageTemperature = storageTemperature;
    }

    public long getDaysUntilExpiration() {
        LocalDate today = LocalDate.now();
        // returns the difference in days (can be negative if it has already expired)
        return ChronoUnit.DAYS.between(today, expirationDate);
    }

    public boolean isExpired() {
        return getDaysUntilExpiration() <= 0;
    }

    public void applyExpirationDiscount() {
        long daysLeft = this.getDaysUntilExpiration();
        if (daysLeft > 0 && daysLeft <= 3) {
            // reduced by 30%
            this.salePrice *= 0.7;
        }
    }

    public int getStorageTemperature(){
        return this.storageTemperature;
    }

    @Override
    public double getTaxesValue() {
        return this.salePrice * TVA_PERISHABLE;
    }

    @Override
    public double getFinalPrice() {
        return this.salePrice + getTaxesValue();
    }
}
