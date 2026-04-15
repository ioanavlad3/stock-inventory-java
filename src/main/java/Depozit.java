package src.main.java;

public class Depozit extends StorageSpace {
    private int operationCost;

    public Depozit(int capacityVolume, int capacityWeight, int cost) {
        super(capacityVolume, capacityWeight);
        this.operationCost = cost;
    }

    public int getOperationCost() {
        return operationCost;
    }
}