package src.main.java;

import java.util.List;

public class InternalTransaction extends Transaction{
    private final Store store;

    public InternalTransaction(Employee e, Deposit d, Product p, int a, int placement,
                               Store s) {
        super(e, d, p, a, placement);
        this.store = s;
    }

    @Override
    protected List<Employee.Role> getAllowedRoles() {
        return List.of(Employee.Role.MANAGER, Employee.Role.ADMINISTRATOR);
    }

    @Override
    public void execute() {
        checkPermission();
        // Transfer from Deposit to Store shelf
        storage.removeProduct(product, amount);
        store.addProduct(product, placement, amount);
        logOperation(" transferred " + amount + " of " + product.getName() +
                " from Deposit to Store shelf " + placement);
    }

}
