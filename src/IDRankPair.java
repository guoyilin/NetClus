
public class IDRankPair {
	public IDRankPair(int id, double rank) {
		this.ID = id;
		this.Rank = rank;
		
	}
	public IDRankPair(String code, String name, double rank )
	{
		this.code = code;
		this.Rank = rank;
		this.name = name;
	}
	public int ID;
	public String code;
	public String name;
	public double Rank;
}

