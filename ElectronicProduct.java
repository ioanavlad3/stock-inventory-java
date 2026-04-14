public class ElectronicProduct extends Product implements Taxable{
    private int monthsWarranty;
    private char energeticClass;
    private int power;

    protected ElectronicProduct(String name, String description, double purchasePrice,
                                double salePrice, int monthsWarranty, char energeticClass, int power) {
        super(name, description, purchasePrice, salePrice);
        this.monthsWarranty = monthsWarranty;
        this.energeticClass = energeticClass;
        this.power = power;
    }


    @Override
    public double getTaxesValue() {
        return this.salePrice * TVA_STANDARD;
    }

    @Override
    public double getFinalPrice() {
        return this.salePrice + getTaxesValue();
    }
}
