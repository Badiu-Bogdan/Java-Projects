package domain.Client;


import domain.BaseEntity;

public class Client extends BaseEntity<Long> {

    private String serialNumber;
    private String name;
    private String address;
    private int yearOfRegistration;

    public Client() {
    }

    public Client(String new_serialNumber, String new_name, String new_address, int new_yearOfRegistration)
    {
        this.serialNumber = new_serialNumber;
        this.name = new_name;
        this.address = new_address;
        this.yearOfRegistration = new_yearOfRegistration;
    }

    //getters
    public String getSerialNumber(){return this.serialNumber;}
    public String getName(){return this.name;}
    public String getAddress(){return this.address;}
    public int getYearOfRegistration(){return this.yearOfRegistration;}

    //setters
    public void setSerialNumber(String newSerialNumber){this.serialNumber = newSerialNumber;}
    public void setName(String newName){this.name = newName;}
    public void setAddress(String newAddress){this.address = newAddress;}
    public void setYearOfRegistration(int newYearOfRegistration){this.yearOfRegistration = newYearOfRegistration;}

    @Override
    public boolean equals(Object e)
    {
        if (this == e) return true;
        if (e == null || this.getClass() != e.getClass()) return false;

        Client client = (Client) e;


        if (!this.serialNumber.equals(client.serialNumber)) return false;
        return this.name.equals(client.name);
    }

    @Override
    public int hashCode()
    {
        int result = this.serialNumber.hashCode();
        result = 31 * result + this.name.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "Client{" +
                "serialNumber='" + serialNumber + '\'' +
                ", name='" + name + '\''
                + ", address= " + address + '\''
                +" ,yearOfRegistration= " + yearOfRegistration + '\''
                + "} " + super.toString();
    }
}
