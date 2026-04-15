package src.main.java;

public class OutTransaction extends Transaction{
    private Store store;
    private Product product;
    private int amount;
    
    public OutTransaction(Employee e, Depozit d, Store s, Product p, int amount) {
        super(e, d);
        this.store = s;
        this.product = p;
        this.amount = amount;
    }

    @Override
    public void execute() {
        if(this.employee.getRole() != Employee.Role.CASHIER){
            throw new SecurityException("Only the cashier is allowed to perform sales transactions");
        }
        store.removeProduct(product, amount);
        logOperation(" sold " + amount + " of " + product.getName());
    }
}
