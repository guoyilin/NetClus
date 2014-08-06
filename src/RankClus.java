/**
 * Main class for rankclus
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author pksunkara
 */
public class RankClus {

	public static Cluster c;
	public static Rank r;
	public static int k = 4, t = 10;
	public static double lambda_s = 0.1, lambda_p = 0.9;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Initialized data store!");

		new Parser();
		System.out.println("Parsing input finished!");

		//s.build();//computer w matrix
		System.out.println("Building data structure finished!");
		
		r = new Rank (c);
		//r.computeWca();
		//r.computeWac();
		//r.computeA_conf();
		//r.computeA_author();
		
		c = new Cluster(k, lambda_s,lambda_p, r, t-1);
		System.out.println("Initialized clustering!");
		
		c.computewholeNetworkRank();
		for (int i=0; i<t; i++) {
			c.iterate(i);	
		}
		c.attributeBelongtoCluster();
		c.writeClusterResult();
//
//		System.out.println("Printing results!\n");
//		print();
	}

//	public static void print() {
//		HashMap<Integer, String> cl = new HashMap<Integer, String>();
//		HashMap<Integer, String> ca = new HashMap<Integer, String>();
//
//		Iterator<String> it = Store.getInstance().v.keySet().iterator();
//		while (it.hasNext()) {
//			String name = it.next();
//			if (Store.getInstance().v.get(name).type == Vertex.CONFERENCE) {
//				cl.put(Store.getInstance().v.get(name).id, name);
//			} else {
//				ca.put(s.v.get(name).id, s.v.get(name).name);
//			}
//		}
//
//		c.step1();
//
//		for (int i=0; i<k; i++) {
//			System.out.println("Cluster " + i);
//
//			int cs = c.c.get(i).l.size();
//
//			for (int j=0; j<cs; j++) {
//				System.out.println("\t" + cl.get(c.c.get(i).l.get(j)));
//			}
//
//			System.out.println("");
//
//			ArrayList<RankAuthor> l = r.rank(i);
//			Iterator<RankAuthor> itr = l.iterator();
//			while (itr.hasNext()) {
//				System.out.println("\t" + ca.get(itr.next().id));
//			}
//
//			System.out.println("");
//		}
//	}
}
