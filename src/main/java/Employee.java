package src.main.java;

public class Employee {
    public enum Role {ADMINISTRATOR, MANAGER, CASHIER}
    private Role role;
    private String name;
    private double salary;
    private double bonus;

    public Employee(String name, Role role,  double salary){
        this.name = name;
        this.salary = salary;
        this.role = role;
        this.bonus = 0;
    }
    // for every transaction the employee gets 1% bonus
    public void addBonus(int numberTransactions){
        this.salary += 0.01 * numberTransactions * salary;
    }

    public Role getRole(){
        return this.role;
    }

    public String getName() {
        return this.name;
    }

}
