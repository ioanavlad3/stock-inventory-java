package src.main.java;

import java.util.*;

public class Distributor {
    private static int CODE = 100;
    private final String codeUnique;
    private float rating;
    private int paymentTerm;
    // name - price
    private HashMap<String, Float> catalog = new HashMap<>();

    public Distributor(float rating, int pT, HashMap<String, Float> c) {
        this.rating = rating;
        this.paymentTerm = pT;
        this.catalog = c;
        this.codeUnique = getUniqueCode();
    }

    private String getUniqueCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        sb.append(CODE++);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Distributor d = (Distributor) o;
        return Objects.equals(codeUnique, d.codeUnique);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(codeUnique);
    }

    public Float getPrice(String productName) {
        return catalog.get(productName);
    }

    public float getRating() {
        return rating;
    }

    public String getCodeUnique() {
        return codeUnique;
    }
}
