package domain.Adoption;

import domain.Purchase.Purchase;

import java.util.Comparator;

public class AdoptionYearComparator implements Comparator<Adoption> {

    @Override
    public int compare(Adoption a, Adoption b)
    {
        return (int) (b.getAdoptionYear() - a.getAdoptionYear());
    }

}