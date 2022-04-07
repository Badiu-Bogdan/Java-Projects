package domain.Purchase;

import java.util.Comparator;

public class PurchaseYearComparator implements Comparator<Purchase> {

    @Override
    public int compare(Purchase a, Purchase b)
    {
        return (int) (a.getPurchaseYear() - b.getPurchaseYear());
    }

}