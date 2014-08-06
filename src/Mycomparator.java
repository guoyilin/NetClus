import java.util.Comparator;

public class Mycomparator implements Comparator {

	// Calls CaseInsensitiveComparer.Compare with the parameters reversed.
	public int compare(Object x, Object y) {
		if (((IDRankPair) x).Rank > (((IDRankPair) y).Rank))
			return -1;
		else if (((IDRankPair) x).Rank == (((IDRankPair) y).Rank))
			return 0;
		else
			return 1;
	}
}