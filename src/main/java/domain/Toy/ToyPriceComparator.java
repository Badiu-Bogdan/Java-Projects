package domain.Toy;

import java.util.Comparator;

public class ToyPriceComparator implements Comparator<Toy> {

    @Override
    public int compare(Toy a, Toy b)
    {
        return (int) (b.getPrice() - a.getPrice());
    }

}