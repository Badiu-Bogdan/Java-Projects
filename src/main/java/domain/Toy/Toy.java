package domain.Toy;

/*
Author: Daniel A
 */

import domain.BaseEntity;

public class Toy extends BaseEntity<Long> {
    private String serialNumber;
    private String name;
    private int weight;
    private String material;
    private double price;

    public Toy() {
    }

    public Toy(String serialNumber, String name, int weight, String material, double price) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.weight = weight;
        this.material = material;
        this.price = price;
    }

    public String getSerialNumber() { return serialNumber; }

    public String getName() { return name; }

    public int getWeight() { return weight; }

    public String getMaterial() { return material; }

    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public void setName(String name) { this.name = name; }

    public void setWeight(int weight) { this.weight = weight; }

    public void setMaterial(String material) { this.material = material; }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Toy toy = (Toy) o;


        if (!serialNumber.equals(toy.serialNumber)) return false;
        return name.equals(toy.name);

    }

    @Override
    public int hashCode() {
        int result = serialNumber.hashCode();
        result = 31 * result + name.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "Toy{" +
                "serialNumber='" + serialNumber + '\'' +
                ", name='" + name + '\'' +
                ", weight='"+ weight+ '\'' +
                ", material=" + material +
                ", price=" + price +
                "} " + super.toString();
    }

}
