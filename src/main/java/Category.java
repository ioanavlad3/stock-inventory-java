import java.util.ArrayList;

public class Category {
    private String name;
    private String description;
    private ArrayList<Product> products = new ArrayList<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addProduct(Product p) {
        this.products.add(p);
    }

    public void addProductList(ArrayList<Product> ps) {
        for (int i = 0 ; i < ps.toArray().length; i++) {
            this.products.add(ps.get(i));
        }
    }
}
