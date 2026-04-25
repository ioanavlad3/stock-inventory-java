package src.main.java;

import java.util.Comparator;

public class ElectronicProduct extends Product implements Taxable  {
    private int monthsWarranty;
    private char energeticClass;
    private int power;

    protected ElectronicProduct(String name, String description, double purchasePrice,
                                double salePrice, int monthsWarranty, char energeticClass, int power,
                                Category.Type cat) {
        super(name, description, purchasePrice, salePrice, cat);
        this.monthsWarranty = monthsWarranty;
        this.energeticClass = energeticClass;
        this.power = power;
    }

    public static Comparator<ElectronicProduct> compareByPower = new Comparator<ElectronicProduct>() {
        @Override
        public int compare(ElectronicProduct o1, ElectronicProduct o2) {
            return o1.power - o2.power;
        }
    };
    // desc by Energetic class : A, B , C ...
    public static Comparator<ElectronicProduct> compareByEnergeticClass = new Comparator<ElectronicProduct>() {
        @Override
        public int compare(ElectronicProduct o1, ElectronicProduct o2) {
            return Character.compare(o2.energeticClass, o1.energeticClass);
        }
    };
    // Desc by warranty
    public static Comparator<ElectronicProduct> compareByWarranty = new Comparator<ElectronicProduct>() {
        @Override
        public int compare(ElectronicProduct o1, ElectronicProduct o2) {
            return o2.monthsWarranty - o1.monthsWarranty;
        }
    };

    @Override
    public double getTaxesValue() {
        return this.salePrice * TVA_STANDARD;
    }

    @Override
    public double getFinalPrice() {
        return this.salePrice + getTaxesValue();
    }

    @Override
    public String toString(){
        return "Electronic{name=" + this.name + ", description=" + this.description +
                ", warranty months=" + this.monthsWarranty + ", energetic class = " + this.energeticClass +
                ", power = " + this.power + "W}";
    }

}
