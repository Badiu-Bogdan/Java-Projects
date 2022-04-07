package domain.Adoption;

import domain.BaseEntity;

import java.util.Comparator;

public class Adoption extends BaseEntity<Long> {
    private String serialNumber;
    private Long clientId;
    private Long petId;
    private int adoptionYear;

    public Adoption() {
    }

    public Adoption(String serialNumber, Long clientId, Long petId, int adoptionYear) {
        this.serialNumber = serialNumber;
        this.clientId = clientId;
        this.petId = petId;
        this.adoptionYear = adoptionYear;
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

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public int getAdoptionYear() {
        return adoptionYear;
    }

    public void setAdoptionYear(int adoptionYear) {
        this.adoptionYear = adoptionYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Adoption adoption = (Adoption) o;


        if (!serialNumber.equals(adoption.serialNumber)) return false;
        return clientId.equals(adoption.clientId) && petId.equals(adoption.petId);

    }

    @Override
    public int hashCode() {
        int result = serialNumber.hashCode();
        long sum = (clientId + petId + adoptionYear);
        result = 31 * result + Long.hashCode(sum);

        return result;
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "serialNumber='" + serialNumber + '\'' +
                ", clientId='" + clientId + '\'' +
                ", petId='"+ petId + '\'' +
                ", adoptionYear=" + adoptionYear +
                "} " + super.toString();
    }
}

