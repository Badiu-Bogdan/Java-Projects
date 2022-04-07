package domain.Toy;

import java.util.Comparator;

public class ToyWeightComparator implements Comparator<Toy> {

    @Override
    public int compare(Toy a, Toy b)
    {
        return (int) (b.getWeight() - a.getWeight());
    }

}