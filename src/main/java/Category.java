package src.main.java;


import java.util.ArrayList;

public class Category {
    private String name;
    private String description;
   // private ArrayList<Product> products = new ArrayList<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

//   public void addProduct(Product p) {
//        this.products.add(p);
//    }

//    public void addProductList(ArrayList<Product> ps) {
//        this.products.addAll(ps);
//    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString(){
        return "Category{name='" + name + "', description='" + description + "'}";
    }

//    @Override
//    public String toString(){
//        StringBuilder sb = new StringBuilder("{name=" + this.name + ", description=" +
//                this.description);
//        if(!this.products.isEmpty()) {
//            sb.append(", products:");
//            for (Product p : products){
//                sb.append(p.toString());
//            }
//        }
//        sb.append("}");
//        return sb.toString();
//    }

}
