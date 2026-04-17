package src.main.java;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public abstract class Transaction {
    protected LocalDateTime timestamp;
    protected Employee employee;
    protected int placement; // shelf or district
    protected StorageSpace storage;
    protected Product product;
    protected int amount;

    public Transaction(Employee e, StorageSpace storage, Product p, int amount, int placement) {
        this.timestamp = LocalDateTime.now();
        this.employee = e;
        this.storage = storage;
        this.product = p;
        this.amount = amount;
        this.placement = placement;
    }

    protected abstract List<Employee.Role> getAllowedRoles();

    protected void checkPermission(){
        if(!getAllowedRoles().contains(employee.getRole())){
            throw new SecurityException("Employee role " + employee.getRole() +
                    " is not allowed to perform this transaction.");
        }
    }

    public abstract void execute();

    protected void logOperation(String details){
        System.out.println("[" + timestamp + "] Employee: " + employee.getName() +
                " | " + details);
    }



}
