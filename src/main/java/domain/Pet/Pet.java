package domain.Pet;

import domain.BaseEntity;

/*
Author: Albu F
 */
public class Pet extends BaseEntity<Long> {

        private String serialNumber;
        private String name;
        private String breed;
        private int birthDate;
        // storeId => lacks abstractization

        public Pet() {
        }

        public Pet(String serialNumber, String name, String breed, int birthDate) {
            this.serialNumber = serialNumber;
            this.name = name;
            this.breed = breed;
            this.birthDate = birthDate;  //this is only the year :) indeed=))
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBreed(){
            return breed;
        }

       public void setBreed(String breed){
            this.breed = breed;
       }
       public int getBirthDate(){
            return birthDate;
       }
       public void setBirthDate(int date){
            this.birthDate=date;
       }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pet pet = (Pet) o;

            if (!serialNumber.equals(pet.serialNumber)) return false;
            return name.equals(pet.name);

        }

        @Override
        public int hashCode() {
            int result = serialNumber.hashCode();
            result = 31 * result + name.hashCode();

            return result;
        }

        @Override
        //String serialNumber, String name, String bread, int birthDate
        public String toString() {
            return "Pet{" +
                    "serialNumber='" + serialNumber + '\'' +
                    ", name='" + name + '\'' +
                    ", bread='"+ breed+ '\'' +
                    ", birthDate=" + birthDate +
                    "} " + super.toString();
        }
    }

