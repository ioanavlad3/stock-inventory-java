package src.main.java;

public class InternalTransaction extends Transaction{

    private int amount;
    private int shelf;
    private Product product;
    private Store store;

    public InternalTransaction(Employee e, Depozit d, Product p, int a, int shelf, Store s) {
        super(e, d);
        this.product = p;
        this.amount = a;
        this.shelf = shelf;
        this.store = s;
    }

    @Override
    public void execute() {
        if(this.employee.getRole() == Employee.Role.CASHIER){
            throw new SecurityException("The cashier is not allowed to do this transaction.");
        }
        // Transfer from Depozit to Store shelf
        depozit.removeProduct(product, amount);
        store.addProduct(product, shelf, amount);
        logOperation(" transferred " + amount + " of " + product.getName() + " from Depozit to Store shelf " + shelf);
    }

}
