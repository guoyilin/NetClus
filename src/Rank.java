/**
 * Rank - Rank the authors in a cluster
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Rank {
	public SparseMatrixUtil matrixUtil;
	public Cluster c = null;
	public static final int RANKITERATOR = 50;
	// HashMap<Integer, HashMap<Integer, Double>> Wca;
	// HashMap<Integer, HashMap<Integer, Double>> Wac;
	HashMap<Integer, HashMap<Integer, Double>> WcaInSubNetwork;
	HashMap<Integer, HashMap<Integer, Double>> WacInSubNetwork;

	// HashMap<Integer, HashMap<Integer, Double>> A_conf;//global
	// HashMap<Integer, HashMap<Integer, Double>> A_author;//global
	public Rank(Cluster c) {
		this.c = c;
		this.matrixUtil = new SparseMatrixUtil();
		WcaInSubNetwork = new HashMap<Integer, HashMap<Integer, Double>>();
		WacInSubNetwork = new HashMap<Integer, HashMap<Integer, Double>>();
		// Wca = new HashMap<Integer, HashMap<Integer, Double>>();//
		// Wca琛ㄧずauthor鍦╟onf涓婂彂琛ㄧ殑鐨勮鏂囩殑鏁扮洰
		// Wac = new HashMap<Integer, HashMap<Integer, Double>>();
		// A_conf = new HashMap<Integer, HashMap<Integer, Double>>();
		// A_author = new HashMap<Integer, HashMap<Integer, Double>>();
	}

	public void clear() {
		WcaInSubNetwork.clear();
		WacInSubNetwork.clear();
	}

	/**
	 * conf's rank whole network
	 * 
	 * @param paperList
	 * @return conf's rank
	 */
	public HashMap<Integer, Double> rank_conf() {
		if (WcaInSubNetwork.size() == 0)
			WcaInSubNetwork = matrixUtil.computeWcaInwholeNetwork();
		System.out.println("WcaInSubNetwork finished.....");
		if (WacInSubNetwork.size() == 0)
			WacInSubNetwork = matrixUtil.computeWacInwholeNetwork();
		System.out.println("WacInSubNetwork finished.....");
		HashMap<Integer, HashMap<Integer, Double>> matrix = matrixUtil
				.multiplyMatrix(WcaInSubNetwork, WacInSubNetwork);
		System.out.println("WcaInSubNetwork*WacInSubNetwork  finished.....");
		// power method to compute matrix's primary eigenvector.
		return matrixUtil.computeMatrixEigenVector(matrix, RANKITERATOR);

	}

	/**
	 * conf's rank belong to sub-network G
	 * 
	 * @param paperList
	 * @return conf's rank
	 */
	public HashMap<Integer, Double> rank_conf(ArrayList<Integer> paperList) {
		if (WcaInSubNetwork.size() == 0)
			WcaInSubNetwork = matrixUtil.computeWcaInSubNetwork(paperList);
		System.out.println("WcaInSubNetwork finished.....");
		if (WacInSubNetwork.size() == 0)
			WacInSubNetwork = matrixUtil.computeWacInSubNetwork(paperList);
		System.out.println("WacInSubNetwork finished.....");
		HashMap<Integer, HashMap<Integer, Double>> matrix = matrixUtil
				.multiplyMatrix(WcaInSubNetwork, WacInSubNetwork);
		System.out.println("WcaInSubNetwork*WacInSubNetwork  finished.....");
		// power method to compute matrix's primary eigenvector.
		HashMap<Integer, Double> confRank = matrixUtil
				.computeMatrixEigenVector(matrix, RANKITERATOR);
		for (int i = 0; i < Store.getInstance().c; i++) {
			if (!confRank.containsKey(i))
				confRank.put(i, 0.0);
		}
		return confRank;
	}

	/**
	 * all term's rank in sub-network G
	 * 
	 * @return
	 */
	public HashMap<Integer, Double> rank_term(ArrayList<Integer> paperList) {
		HashMap<Integer, Double> rank = new HashMap<Integer, Double>();

		for (int i = 0; i < paperList.size(); i++) {
			int paperid = paperList.get(i);
			HashMap<Integer, Double> termMap = Store.getInstance().pt
					.get(paperid);
			if (termMap == null)
				continue;
			Iterator<Integer> it = termMap.keySet().iterator();
			while (it.hasNext()) {
				int termid = it.next();
				double value = termMap.get(termid);
				if (rank.containsKey(termid)) {
					double oldvalue = rank.get(termid);
					value += oldvalue;
					rank.put(termid, value);// update
				} else {
					rank.put(termid, value);
				}
			}
		}
		// term number in the cluster
		Iterator<Integer> rank_it = rank.keySet().iterator();
		double sum = 0;
		while (rank_it.hasNext()) {
			int key = rank_it.next();
			sum += rank.get(key);
		}
		// norm the rank matrix.
		Iterator<Integer> it2 = rank.keySet().iterator();
		while (it2.hasNext()) {
			int key = it2.next();
			double value = rank.get(key);
			rank.put(key, value / sum);
		}
		// add zero term to rank
		for (int i = 0; i < Store.getInstance().t; i++) {
			if (!rank.containsKey(i))
				rank.put(i, 0.0);
		}
		System.gc();
		return rank;
	}

	/**
	 * all author's rank belong to sub-network G
	 * 
	 * @param paperList
	 * @return author's rank
	 */
	public HashMap<Integer, Double> rank_author(ArrayList<Integer> paperList) {
		if (WcaInSubNetwork.size() == 0)
			WcaInSubNetwork = matrixUtil.computeWcaInSubNetwork(paperList);
		System.out.println("WcaInSubNetwork finished.....");
		if (WacInSubNetwork.size() == 0)
			WacInSubNetwork = matrixUtil.computeWacInSubNetwork(paperList);
		System.out.println("WacInSubNetwork finished.....");
		HashMap<Integer, HashMap<Integer, Double>> matrix = matrixUtil
				.multiplyMatrix(WacInSubNetwork, WcaInSubNetwork);
		// power method to compute matrix's primary eigenvector.
		HashMap<Integer, Double> rank = matrixUtil.computeMatrixEigenVector(matrix, RANKITERATOR);
		// add zero term to rank
		for (int i = 0; i < Store.getInstance().a; i++) {
			if (!rank.containsKey(i))
				rank.put(i, 0.0);
		}
		return rank;
	}

	/**
	 * whole network rank_author
	 * 
	 * @return
	 */
	public HashMap<Integer, Double> rank_author() {
		if (WcaInSubNetwork.size() == 0)
			WcaInSubNetwork = matrixUtil.computeWcaInwholeNetwork();
		System.out.println("WcaInwholeNetwork finished.....");
		if (WacInSubNetwork.size() == 0)
			WacInSubNetwork = matrixUtil.computeWacInwholeNetwork();
		System.out.println("WacInwholeNetwork finished.....");
		HashMap<Integer, HashMap<Integer, Double>> matrix = matrixUtil
				.multiplyMatrix(WacInSubNetwork, WcaInSubNetwork);
		// power method to compute matrix's primary eigenvector.
		return matrixUtil.computeMatrixEigenVector(matrix, RANKITERATOR);
	}

	/**
	 * A_conf = Wca * Wac
	 */
	// public void computeA_conf() {
	// Iterator<Integer> it_wca = Wca.keySet().iterator();
	// while (it_wca.hasNext()) {
	// int conf = it_wca.next();
	// // loop all conf
	// for (int i = 0; i < Store.getInstance().c; i++) {
	// Iterator<Integer> it_wac = Wac.keySet().iterator();
	// // loop all author
	// double value = 0;
	// while (it_wac.hasNext()) {
	// int author = it_wac.next();
	// if (Wac.get(author).containsKey(i)
	// && Wca.get(conf).containsKey(author))
	// value += Wac.get(author).get(i)
	// * Wca.get(conf).get(author);
	// }
	// if (value != 0.0) {
	// if (A_conf.containsKey(conf)) {
	// A_conf.get(conf).put(i, value);
	// } else {
	// HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
	// temp.put(i, value);
	// A_conf.put(conf, temp);
	// }
	// }
	//
	// }
	//
	// }
	// System.out.println("computeA_conf finished");
	// }
	//
	// /**
	// * A_author = Wac * Wca
	// */
	// public void computeA_author() {
	// Iterator<Integer> it_wac = Wac.keySet().iterator();
	// while (it_wac.hasNext()) {
	// int author = it_wac.next();
	// // loop all author
	// for (int i = 0; i < Store.getInstance().a; i++) {
	// Iterator<Integer> it_wca = Wca.keySet().iterator();
	// // loop all ocnf
	// double value = 0;
	// while (it_wca.hasNext()) {
	// int conf = it_wca.next();
	// if (Wca.get(conf).containsKey(i)
	// && Wac.get(author).containsKey(conf))
	// value += Wca.get(conf).get(i)
	// * Wac.get(author).get(conf);
	// }
	// if (value != 0.0) {
	// if (A_author.containsKey(author)) {
	// A_author.get(author).put(i, value);
	// } else {
	// HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
	// temp.put(i, value);
	// A_author.put(author, temp);
	// }
	// }
	//
	// }
	//
	// }
	// System.out.println("computeA_author finished");
	// }

	// compute Wca
	// public void computeWca() {
	// // Dda^-1 * Wda
	// HashMap<Integer, HashMap<Integer, Double>> pa = Store.getInstance().pa;
	// HashMap<Integer, HashMap<Integer, Double>> ap = Store.getInstance().ap;
	// // store Dda^-1
	// HashMap<Integer, Double> Dda_1 = new HashMap<Integer, Double>();// record
	// // every
	// // paper's
	// // authors'
	// // number.
	// Iterator<Integer> pa_it = pa.keySet().iterator();
	// while (pa_it.hasNext()) {
	// int key = pa_it.next();// paperid
	// HashMap<Integer, Double> rowMatrix = pa.get(key);// paperid's
	// // authors
	// double rowSum = 0;
	// Iterator<Double> it = rowMatrix.values().iterator();
	// while (it.hasNext()) {
	// rowSum += it.next();
	// }
	// Dda_1.put(key, rowSum);// record every paper and authors' number.
	// }
	// // change Wad
	// Iterator<Integer> it_ap = ap.keySet().iterator();
	// int count = 0;
	// while (it_ap.hasNext()) {
	// count++;
	// int ap_key = it_ap.next();// author
	// HashMap<Integer, Double> author_papers_HM = ap.get(ap_key);// all
	// // papers
	// // for
	// // this
	// // author.
	// Iterator<Integer> it_author_papers_HM = author_papers_HM.keySet()
	// .iterator();
	// while (it_author_papers_HM.hasNext()) {
	// int key = it_author_papers_HM.next();// paper_id
	// double oldValue = author_papers_HM.get(key);
	// double newValue = oldValue / (Dda_1.get(key));
	// author_papers_HM.put(key, newValue);
	// }
	// // update ap
	// ap.put(ap_key, author_papers_HM);
	// }
	// System.out.println(count);
	// // Wcd * pa
	//
	// Iterator<Integer> cp_it = Store.getInstance().cp.keySet().iterator();
	// while (cp_it.hasNext()) {
	// int cp_key = cp_it.next();// conference
	// HashMap<Integer, Double> conf_papersHM =
	// Store.getInstance().cp.get(cp_key);// get
	// // all
	// // paper
	// // for
	// // this
	// // "key"
	// // conf
	// // Wda's column equal to Wad's line.
	// Iterator<Integer> ap_it = ap.keySet().iterator();
	// while (ap_it.hasNext()) {
	// int ap_key = ap_it.next();// author
	// HashMap<Integer, Double> author_papers_HM = ap.get(ap_key);// all
	// // papers
	// // for
	// // this
	// // author.
	// Iterator<Integer> it_author_papers_ = author_papers_HM.keySet()
	// .iterator();
	// // conf_papersHM*author_papers_HM
	// double author_paperNumber = 0;
	// // Iterator<Integer> it_conf_papers = conf_papersHM.keySet()
	// // .iterator();
	// while (it_author_papers_.hasNext()) {
	// int paper_key = it_author_papers_.next();// this author's
	// // paper
	// if (conf_papersHM.containsKey(paper_key)) {
	// author_paperNumber += conf_papersHM.get(paper_key)
	// * author_papers_HM.get(paper_key);
	// }
	// }
	// if (author_paperNumber != 0) {
	// // store Wca 's element
	// if (!Wca.containsKey(cp_key)) {
	// HashMap<Integer, Double> temp1 = new HashMap<Integer, Double>();
	// temp1.put(ap_key, author_paperNumber);
	// Wca.put(cp_key, temp1);
	// } else {
	// Wca.get(cp_key).put(ap_key, author_paperNumber);
	// }
	// }
	//
	// }
	// }
	// System.out.println("finished");
	//
	// }

	// public void computeWac() {
	// // Ddc^-1 * Wdc
	// HashMap<Integer, HashMap<Integer, Double>> pc = Store.getInstance().pc;
	// HashMap<Integer, HashMap<Integer, Double>> cp = Store.getInstance().cp;
	// // store Ddc^-1
	// HashMap<Integer, Double> Ddc_1 = new HashMap<Integer, Double>();
	// Iterator<Integer> pc_it = pc.keySet().iterator();
	// while (pc_it.hasNext()) {
	// int key = pc_it.next();// paperid
	// HashMap<Integer, Double> rowMatrix = pc.get(key);
	// double rowSum = 0;
	// Iterator<Double> it = rowMatrix.values().iterator();
	// while (it.hasNext()) {
	// rowSum += it.next();
	// }
	// Ddc_1.put(key, rowSum);
	// }
	// // change Wcd
	// Iterator<Integer> it_cp = cp.keySet().iterator();
	// while (it_cp.hasNext()) {
	// int cp_key = it_cp.next();// confid
	// HashMap<Integer, Double> author_papers_HM = cp.get(cp_key);// all
	// // papers
	// // for
	// // this
	// // conf.
	// Iterator<Integer> it_author_papers_HM = author_papers_HM.keySet()
	// .iterator();
	// while (it_author_papers_HM.hasNext()) {
	// int key = it_author_papers_HM.next();// paper_id
	// double oldValue = author_papers_HM.get(key);
	// double newValue = oldValue / (Ddc_1.get(key));
	// author_papers_HM.put(key, newValue);
	// }
	// // update cp
	// cp.put(cp_key, author_papers_HM);
	// }
	// // Wad * Ddc-1*Wdc
	// Iterator<Integer> ap_it = Store.getInstance().ap.keySet().iterator();
	// while (ap_it.hasNext()) {
	// int ap_key = ap_it.next();// author
	// HashMap<Integer, Double> author_papersHM =
	// Store.getInstance().ap.get(ap_key);// get
	// // all
	// // paper
	// // for
	// // this
	// // "key"
	// // author
	// // Wdc's column equal to Wcd's line.
	// Iterator<Integer> cp_it = cp.keySet().iterator();
	// while (cp_it.hasNext()) {
	// int cp_key = cp_it.next();// conf
	// HashMap<Integer, Double> conf_papers_HM = cp.get(cp_key);// all
	// // papers
	// // for
	// // this
	// // conf.
	// // conf_papersHM*author_papers_HM
	// double author_paperNumber = 0;
	// Iterator<Integer> it_author_papers = author_papersHM.keySet()
	// .iterator();
	// while (it_author_papers.hasNext()) {
	// int paper_key = it_author_papers.next();// paper
	// if (conf_papers_HM.containsKey(paper_key)) {
	// author_paperNumber += author_papersHM.get(paper_key)
	// * conf_papers_HM.get(paper_key);
	// }
	// }
	// if (author_paperNumber != 0) {
	// // store Wca 's element
	// if (!Wac.containsKey(ap_key)) {
	// HashMap<Integer, Double> temp1 = new HashMap<Integer, Double>();
	// temp1.put(cp_key, author_paperNumber);
	// Wac.put(ap_key, temp1);
	// } else {
	// Wac.get(ap_key).put(cp_key, author_paperNumber);
	// }
	// }
	// }
	//
	// }
	// System.out.println("finished");
	// }
	//

	// public ArrayList<RankAuthor> rank(int n) {
	// ArrayList<RankAuthor> r = new ArrayList<RankAuthor>();
	//
	// for (int i = 0; i < Store.getInstance().a; i++) {
	// r.add(new RankAuthor(c.c.get(n).ry.get(i, 0), i));
	// }
	//
	// Collections.sort(r, new RankComparator());
	//
	// return r;
	// }
	//
	// private class RankComparator implements Comparator<RankAuthor> {
	//
	// public int compare(RankAuthor a, RankAuthor b) {
	// if (a.v > b.v) {
	// return -1;
	// } else {
	// return 1;
	// }
	// }
	// }
}
