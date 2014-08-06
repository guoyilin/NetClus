/**
 * Vertex - Objects representing vertexes of graphs
 * @author guoyilin1987@gmail.com
 */
public class Vertex {

	public int id = -1;
	public int type = -1;
	public int cluster = -1;//this vertex belongs to this cluster.

	public String name = null;

	public static final int PERSON = 0;
	public static final int CONFERENCE = 1;
	public static final int TERM = 2;
	public static final int PAPER = 3;
	public Vertex(String name, int id, int type) {
		this.name = name;
		this.id = id;
		this.type = type;
	}
}
