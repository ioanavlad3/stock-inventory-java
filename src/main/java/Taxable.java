package src.main.java;

public interface Taxable {
    double TVA_PERISHABLE = 0.09;
    double TVA_STANDARD = 0.19;

    double getTaxesValue(); // Method that returns the value of the tax
    double getFinalPrice(); // sale price + tax
}