package src.main.java;
// class for the elements that came in Depozit
public class SupplyTransaction extends Transaction{

    private int district;
    private int amount;
    private Product product;

    public SupplyTransaction(Employee e, Depozit d, int district, int amount, Product p) {
        super(e, d);
        this.district = district;
        this.amount = amount;
        this.product = p;
    }

    @Override
    public void execute() {
        if (this.employee.getRole() == Employee.Role.CASHIER){
            throw new SecurityException("Cashier is not allowed to do this transaction");
        }
        depozit.addProduct(product, district, amount);
        this.employee.addBonus(1);
        logOperation(" received supply: " + amount + " of " + product.getName() + " in district " + district);
    }
}
