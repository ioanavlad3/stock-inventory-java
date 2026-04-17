package src.main.java;

import java.util.List;

public class OutTransaction extends Transaction{
    private final Store store;
    
    public OutTransaction(Employee e, Deposit d, Product p, int amount, int placement,
                          Store store) {
        super(e, d, p, amount, placement);
        this.store = store;
    }


    @Override
    protected List<Employee.Role> getAllowedRoles() {
        return List.of(Employee.Role.MANAGER, Employee.Role.ADMINISTRATOR);
    }

    @Override
    public void execute() {
        checkPermission();
        store.removeProduct(product, amount);
        logOperation(" sold " + amount + " of " + product.getName());
    }
}
