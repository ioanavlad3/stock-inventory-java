import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Depozit {
    private final int capacityVolume;
    private final int capacityWeight;
    private double capacityWeightRemain;
    private double capacityVolumeRemain;
    private HashMap<Product, StockInfo> position = new HashMap<>();
    private int operationCost;
    //private List<Tranzaction> history;

    private static class StockInfo {
        int district;
        int amount;

        StockInfo(int d, int a) {
            this.amount = a;
            this.district = d;
        }

    }

    public Depozit(int capacityVolume, int capacityWeight, int cost) {
        this.capacityVolume = capacityVolume;
        this.capacityWeight = capacityWeight;
        this.capacityWeightRemain = capacityWeight;
        this.capacityVolumeRemain = capacityVolume;
        this.operationCost = cost;
    }

    public void addProduct(Product p, int district, int amount) {
        if (p.getWeight() * amount > capacityWeightRemain){
            throw new WeightLimitExceededException("Weight limit exceeded. Required: " + p.getWeight() * amount
             + " , Available: " + this.capacityWeightRemain);
        }

        if(p.getVolume() * amount< capacityVolumeRemain){
            throw new VolumeLimitExceededException("Volume limit exceeded. Required: " + p.getVolume() * amount
                    + " , Available: " + this.capacityVolumeRemain);
        }
        StockInfo si = new StockInfo(district, amount);
        if (position.containsKey(p)){
            position.get(p).amount += amount;
        } else {
            position.put(p, si);
        }

        capacityVolumeRemain -= p.getVolume() * amount;
        capacityWeightRemain -= p.getWeight() * amount;

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Depozit d = (Depozit) o;
        return Objects.equals(d.hashCode(), this.hashCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.capacityVolume);
    }

}
