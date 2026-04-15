package src.main.java;

import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class Transaction {
    protected LocalDateTime timestamp;
    protected Employee employee;
    protected Depozit depozit;

    public Transaction(Employee e, Depozit d) {
        this.timestamp = LocalDateTime.now();
        this.employee = e;
        this.depozit = d;
    }

    public abstract void execute();

    protected void logOperation(String details){
        System.out.println("[" + timestamp + "] Employee: " + employee.getName() + " | " + details);
    }



}
