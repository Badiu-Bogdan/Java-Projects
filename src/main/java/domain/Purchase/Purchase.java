package domain.Purchase;

import domain.BaseEntity;

import java.util.Comparator;

public class Purchase extends BaseEntity<Long> {

    private String serialNumber;
    private Long clientId;
    private Long toyId;
    private int purchaseYear;

    public Purchase() {
    }

    public Purchase(String serialNumber, Long clientId, Long toyId, int purchaseYear) {
        this.serialNumber = serialNumber;
        this.clientId = clientId;
        this.toyId = toyId;
        this.purchaseYear = purchaseYear;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getToyId() {
        return toyId;
    }

    public void setToyId(Long toyId) {
        this.toyId = toyId;
    }

    public int getPurchaseYear() {
        return purchaseYear;
    }

    public void setPurchaseYear(int purchaseYear) {
        this.purchaseYear = purchaseYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Purchase purchase = (Purchase) o;


        if (!serialNumber.equals(purchase.serialNumber)) return false;
        return clientId.equals(purchase.clientId) && toyId.equals(purchase.toyId);

    }

    @Override
    public int hashCode() {
        int result = serialNumber.hashCode();
        long sum = (clientId + toyId + purchaseYear);
        result = 31 * result + Long.hashCode(sum);

        return result;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "serialNumber='" + serialNumber + '\'' +
                ", clientId='" + clientId + '\'' +
                ", toyId='"+ toyId+ '\'' +
                ", purchaseYear=" + purchaseYear +
                "} " + super.toString();
    }
}

