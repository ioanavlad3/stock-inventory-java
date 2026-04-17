package src.main.java;

import java.util.List;

// class for the elements that came in Deposit
public class SupplyTransaction extends Transaction{

    public SupplyTransaction(Employee e, Deposit d, int district, int amount,
                             Product p, int placement) {
        super(e, d, p, amount, placement);
    }


    @Override
    protected List<Employee.Role> getAllowedRoles() {
        return List.of(Employee.Role.MANAGER, Employee.Role.ADMINISTRATOR);
    }

    @Override
    public void execute() {
        checkPermission();
        storage.addProduct(product, placement, amount);
        this.employee.addBonus(1);
        logOperation(" received supply: " + amount + " of " + product.getName() +
                " in district " + placement);
    }
}
