package domain.Pet;

import java.util.Comparator;

public class PetBirthYearComparator implements Comparator<Pet> {

    @Override
    public int compare(Pet a, Pet b)
    {
        return (int) (b.getBirthDate() - a.getBirthDate());
    }

}