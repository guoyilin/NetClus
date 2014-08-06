/**
 * SingleCluster - Single Cluster store for the program
 */

import Jama.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author pksunkara
 */
public class SingleCluster {

	public ArrayList<Integer> paperList;// this cluster's paper set.
	public ArrayList<Integer> authorList;//only last time used,write to file.
	public ArrayList<Integer> confList;//only last time used,write to file.
	public ArrayList<Integer> termList;//only last time used,write to file.
	public ArrayList<IDRankPair> rank_conf;
	public HashMap<Integer, Double> rank_conf_map;
	public ArrayList<IDRankPair> rank_author;
	public HashMap<Integer, Double> rank_author_map;
	public ArrayList<IDRankPair> rank_term;
	public HashMap<Integer, Double> rank_term_map;
	public ArrayList<IDRankPair> rank_paper;
	public HashMap<Integer, Double> rank_paper_map;
	public double[] center;// cluster's center.

	public SingleCluster(int k) {
		this.paperList = new ArrayList<Integer>();
		this.center = new double[k];
		rank_conf = new ArrayList<IDRankPair>();
		rank_author = new ArrayList<IDRankPair>();
		rank_term = new ArrayList<IDRankPair>();
		rank_paper = new ArrayList<IDRankPair>();
		rank_conf_map = new HashMap<Integer, Double>();
		rank_author_map = new HashMap<Integer, Double>();
		rank_term_map = new HashMap<Integer, Double>();
		rank_paper_map = new HashMap<Integer, Double>();
		authorList =new ArrayList<Integer>();
		confList =new ArrayList<Integer>();
		termList= new ArrayList<Integer>();
	}

	public void clearAll() {
		paperList.clear();
		rank_conf.clear();
		rank_conf_map.clear();
		rank_author.clear();
		rank_author_map.clear();
		rank_term.clear();
		rank_term_map.clear();
		rank_paper.clear();
		rank_paper_map.clear();
	}
}
