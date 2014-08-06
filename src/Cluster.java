/**
 * Cluster - Cluster store for the program
 */

import Jama.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 * @author pksunkara
 */
public class Cluster {

	private int k = 0;// k+1 is the global network
	private double lambda_s = 0.1;
	private double lambda_p = 0.9;
	private Rank rank;
	private SparseMatrixUtil matrixUtil;
	private int Wholeiterator;
	private final int ITER2 = 20;
	public ArrayList<SingleCluster> clusterList = null;// all cluster.
	public double[][] paper_pdk;
	public double[][] conf_pdk;
	public double[][] term_pdk;
	public double[][] author_pdk;

	public Cluster(int k, double lambda_s, double lambda_p, Rank rank, int t) {
		matrixUtil = new SparseMatrixUtil();
		this.k = k;
		this.Wholeiterator = t;
		this.lambda_p = lambda_p;
		this.lambda_s = lambda_s;
		this.rank = rank;
		initCluster();
		initLast_pdk();

	}

	public void attributeBelongtoCluster() {
		// author
		for (int i = 0; i < Store.getInstance().a; i++) {
			// papers neibor to author
			HashMap<Integer, Double> map = Store.getInstance().ap.get(i);
			if (map != null) {
				Iterator<Integer> papers_it = map.keySet().iterator();
				while (papers_it.hasNext()) {
					int paper = papers_it.next();
					for (int j = 0; j < k + 1; j++)
						author_pdk[i][j] = author_pdk[i][j]
								+ paper_pdk[paper][j];
				}
				for (int j = 0; j < k + 1; j++)
					author_pdk[i][j] = author_pdk[i][j] / map.size();
			}

		}

		// conf
		for (int i = 0; i < Store.getInstance().c; i++) {
			// papers neibor to author
			HashMap<Integer, Double> map = Store.getInstance().cp.get(i);
			if (map != null) {
				Iterator<Integer> papers_it = map.keySet().iterator();
				while (papers_it.hasNext()) {
					int paper = papers_it.next();
					for (int j = 0; j < k + 1; j++)
						conf_pdk[i][j] = conf_pdk[i][j] + paper_pdk[paper][j];
				}
				for (int j = 0; j < k + 1; j++)
					conf_pdk[i][j] = conf_pdk[i][j] / map.size();
			}

		}

		// term
		for (int i = 0; i < Store.getInstance().t; i++) {
			// papers neibor to author
			HashMap<Integer, Double> map = Store.getInstance().tp.get(i);
			if (map != null) {
				Iterator<Integer> papers_it = map.keySet().iterator();
				while (papers_it.hasNext()) {
					int paper = papers_it.next();
					for (int j = 0; j < k + 1; j++)
						term_pdk[i][j] = term_pdk[i][j] + paper_pdk[paper][j];
				}
				for (int j = 0; j < k + 1; j++)
					term_pdk[i][j] = term_pdk[i][j] / map.size();
			}
		}

		clusterObjectResult();
		System.gc();
	}

	/**
	 * each's cluster contain: author, term, conf.
	 */
	public void clusterObjectResult() {
		// for (int i = 0; i < author_pdk.length; i++) {
		// FileUtil.write("result//authorResult", author_pdk[i]);
		// }
		// each author's belong
		for (int i = 0; i < author_pdk.length; i++) {
			int cluster = matrixUtil.getMaxElementInArray(author_pdk[i]);
			if (cluster != -1)// delete the author that has no paper.
				clusterList.get(cluster).authorList.add(i);
		}

		// each conf's belong
		for (int i = 0; i < conf_pdk.length; i++) {
			int cluster = matrixUtil.getMaxElementInArray(conf_pdk[i]);
			clusterList.get(cluster).confList.add(i);
		}
		// for (int i = 0; i < term_pdk.length; i++) {
		// FileUtil.write("result//termsResult.txt", term_pdk[i]);
		// }
		// each term's belong
		for (int i = 0; i < term_pdk.length; i++) {
			int cluster = matrixUtil.getMaxElementInArray(term_pdk[i]);
			if (cluster != -1)
				clusterList.get(cluster).termList.add(i);
		}
		System.gc();
	}

	public void writeClusterResult() {
		// write every conf's posterior probabilities.
		for (int i = 0; i < Store.getInstance().c; i++) {
			FileUtil.write(FileUtil.FILE_CONFSRESULT, conf_pdk[i]);
		}
		// // 测试
		// for (int i1 = 0; i1 < k + 1; i1++) {
		// System.out.println("--------------------------");
		// System.out.println(clusterList.get(i1).rank_paper_map.get(270));
		// System.out.println("--------------------------");
		// }

		// write each cluster's attribute object rank result to file
		for (int i = 0; i < k; i++) {
			// author
			ArrayList<Integer> authorList = clusterList.get(i).authorList;
			clusterList.get(i).rank_author.clear();
			for (int j = 0; j < authorList.size(); j++) {

				String authorCode = Store.getInstance().authorCode
						.get(authorList.get(j));
				String authorName = Store.getInstance().vertex_a
						.get(authorCode).name;
				// System.out.println("author id:"+authorList.get(j));
				// System.out.println("author code:"+authorCode);
				// System.out.println(clusterList.get(i).rank_author_map
				// .get(authorList.get(j)));
				double authorRank = 0;

				// if (clusterList.get(i).rank_author_map.get(authorList.get(j))
				// != null)
				authorRank = clusterList.get(i).rank_author_map.get(authorList
						.get(j));
				clusterList.get(i).rank_author.add(new IDRankPair(authorCode,
						authorName, authorRank));
			}
			Comparator comp = new Mycomparator();
		    Collections.sort(clusterList.get(i).rank_author,comp);  
			String path = "cluster" + i;
			FileUtil.write(path, FileUtil.FILE_AUTHOR,
					clusterList.get(i).rank_author);
			// conf
			ArrayList<Integer> confList = clusterList.get(i).confList;
			clusterList.get(i).rank_conf.clear();
			for (int j = 0; j < confList.size(); j++) {
				String confCode = Store.getInstance().confCode.get(confList
						.get(j));
				String confName = Store.getInstance().vertex_c.get(confCode).name;
				double confRank = 0;
				// if (clusterList.get(i).rank_conf_map.get(confList.get(j)) !=
				// null)
				confRank = clusterList.get(i).rank_conf_map
						.get(confList.get(j));
				clusterList.get(i).rank_conf.add(new IDRankPair(confCode,
						confName, confRank));
			}
			Collections.sort(clusterList.get(i).rank_conf,comp);  
			path = "cluster" + i;
			FileUtil.write(path, FileUtil.FILE_CONF,
					clusterList.get(i).rank_conf);
			// term
			ArrayList<Integer> termList = clusterList.get(i).termList;
			clusterList.get(i).rank_term.clear();
			for (int j = 0; j < termList.size(); j++) {
				String termCode = Store.getInstance().termCode.get(termList
						.get(j));
				String termName = Store.getInstance().vertex_t.get(termCode).name;
				double termRank = 0;
				// if (clusterList.get(i).rank_term_map.get(termList.get(j)) !=
				// null)
				termRank = clusterList.get(i).rank_term_map
						.get(termList.get(j));
				clusterList.get(i).rank_term.add(new IDRankPair(termCode,
						termName, termRank));
			}
			Collections.sort(clusterList.get(i).rank_term,comp);
			path = "cluster" + i;
			FileUtil.write(path, FileUtil.FILE_TERM,
					clusterList.get(i).rank_term);

		}
		System.gc();
	}

	private void initLast_pdk() {
		int totoalPapers = Store.getInstance().p;
		paper_pdk = new double[totoalPapers][k + 1];
		for (int i = 0; i < totoalPapers; i++) {
			for (int j = 0; j < k + 1; j++)
				paper_pdk[i][j] = 0;
		}
		int totoalConfs = Store.getInstance().c;
		conf_pdk = new double[totoalConfs][k + 1];
		for (int i = 0; i < totoalConfs; i++) {
			for (int j = 0; j < k + 1; j++)
				conf_pdk[i][j] = 0;
		}
		int totoalauthors = Store.getInstance().a;
		author_pdk = new double[totoalauthors][k + 1];
		for (int i = 0; i < totoalauthors; i++) {
			for (int j = 0; j < k + 1; j++)
				author_pdk[i][j] = 0;
		}
		int totoalterms = Store.getInstance().t;
		term_pdk = new double[totoalterms][k + 1];
		for (int i = 0; i < totoalterms; i++) {
			for (int j = 0; j < k + 1; j++)
				term_pdk[i][j] = 0;
		}
	}

	public void iterate(int t) {
		step1(t);
		step2(t);
		if(t < Wholeiterator)
			step3();
	}

	public void computewholeNetworkRank() {
		ArrayList<Integer> paperList = clusterList.get(k).paperList;
		if (!FileUtil.isFileExist(FileUtil.FILE_CONF)) {
			HashMap<Integer, Double> conf_rank = rank.rank_conf();
			clusterList.get(k).rank_conf_map = conf_rank;
			// write to file, in order to avoid compute again.
			FileUtil.write("", FileUtil.FILE_CONF, conf_rank);
		} else {
			// read file
			clusterList.get(k).rank_conf_map = FileUtil
					.readFile(FileUtil.FILE_CONF);
		}
		// author_rank
		if (!FileUtil.isFileExist(FileUtil.FILE_AUTHOR)) {
			HashMap<Integer, Double> author_rank = rank.rank_author();
			clusterList.get(k).rank_author_map = author_rank;
			// write to file, in order to avoid compute again.
			FileUtil.write("", FileUtil.FILE_AUTHOR, author_rank);
		} else {
			// read file
			clusterList.get(k).rank_author_map = FileUtil
					.readFile(FileUtil.FILE_AUTHOR);
		}
		// term_rank
		if (!FileUtil.isFileExist(FileUtil.FILE_TERM)) {
			HashMap<Integer, Double> term_rank = rank.rank_term(paperList);
			clusterList.get(k).rank_term_map = term_rank;
			// write to file, in order to avoid compute again.
			FileUtil.write("", FileUtil.FILE_TERM, term_rank);
		} else {
			// read file
			clusterList.get(k).rank_term_map = FileUtil
					.readFile(FileUtil.FILE_TERM);
		}
		rank.clear();
		// paper rank
		paperRankInCluster(0, k);
		System.out.println("whole network  rank finished................");
		System.gc();
	}

	public void paperRankInCluster(int iteratorNum, int cluster) {
		String path = "";
		if (cluster == k)
			path = "";
		else
			path = "iteration" + iteratorNum + "//cluster" + cluster;
		if (!FileUtil.isFileExist(path + "//" + FileUtil.FILE_PAPER)) {
			HashMap<Integer, Double> rankMap = new HashMap<Integer, Double>();
			Iterator<String> paperIdSet_it = Store.getInstance().vertex_p
					.keySet().iterator();
			while (paperIdSet_it.hasNext()) {
				double rank = 1;
				int paperid = Store.getInstance().vertex_p.get(paperIdSet_it
						.next()).id;
				// get paper's neighborhood
				HashMap<Integer, Double> authorsMap = Store.getInstance().pa
						.get(paperid);
				if (authorsMap != null)// some paper has no author record
										// paperid: 162636
				{
					// get authors
					Iterator<Integer> authorsMap_it = authorsMap.keySet()
							.iterator();
					while (authorsMap_it.hasNext()) {
						int author = authorsMap_it.next();
						double w = authorsMap.get(author);
						// if (clusterList.get(cluster).rank_author_map
						// .containsKey(author)) {
						double authorRank = clusterList.get(cluster).rank_author_map
								.get(author);
						rank = rank * Math.pow(authorRank, w);
						// }

					}
				}
				// get terms
				HashMap<Integer, Double> termsMap = Store.getInstance().pt
						.get(paperid);
				if (termsMap != null) {
					Iterator<Integer> termsMap_it = termsMap.keySet()
							.iterator();
					while (termsMap_it.hasNext()) {
						int term = termsMap_it.next();
						double w = termsMap.get(term);
						// if (clusterList.get(cluster).rank_term_map
						// .containsKey(term)) {
						double termRank = clusterList.get(cluster).rank_term_map
								.get(term);
						rank = rank * Math.pow(termRank, w);
						// }

					}
				}

				// get confs
				HashMap<Integer, Double> confsMap = Store.getInstance().pc
						.get(paperid);
				if (confsMap != null) {
					Iterator<Integer> confsMap_it = confsMap.keySet()
							.iterator();
					while (confsMap_it.hasNext()) {
						int conf = confsMap_it.next();
						double w = confsMap.get(conf);
						// if (clusterList.get(cluster).rank_conf_map
						// .containsKey(conf)) {
						double confRank = clusterList.get(cluster).rank_conf_map
								.get(conf);
						rank = rank * Math.pow(confRank, w);
						// }

					}

				}
				// if (rank != 1)// if rank = 1 , 那么该paper在此cluster中的rank 为 0
				rankMap.put(paperid, rank);
				// else
				// rankMap.put(paperid, 0.0);

			}
			// clusterList.get(cluster).rank_paper_map = rankMap;
			clusterList.get(cluster).rank_paper_map = matrixUtil
					.normlize1DHashMap(rankMap);
			// write to file, in order to avoid compute again.
			FileUtil.write(path, FileUtil.FILE_PAPER,
					clusterList.get(cluster).rank_paper_map);
		} else {
			// read file
			clusterList.get(cluster).rank_paper_map = FileUtil.readFile(path
					+ "//" + FileUtil.FILE_PAPER);
			// // 测试是否和为1
			// double sum1 = 0;
			// Iterator<Double> it1 =
			// clusterList.get(cluster).rank_paper_map.values()
			// .iterator();
			// while (it1.hasNext()) {
			// sum1 += it1.next();
			// }
			// System.out.println("---------------cluster k:"
			// + " rank_paper sum:" + sum1);
		}
		clusterList.get(cluster).rank_paper = matrixUtil
				.HashMapToArrayList(clusterList.get(cluster).rank_paper_map);
		// for(int i = 0 ; i < clusterList.get(cluster).rank_paper.size(); i++)
		// {
		// if(i < 10)
		// System.out.println( clusterList.get(cluster).rank_paper.get(i).Rank);
		// }
		System.gc();
	}

	public void confRankIncluster(int iteratorNum, int cluster) {
		// conf_rank
		String path = "iteration" + iteratorNum + "//" + "cluster" + cluster;
		if (!FileUtil.isFileExist(path + "//" + FileUtil.FILE_CONF)) {
			ArrayList<Integer> paperList = clusterList.get(cluster).paperList;
			HashMap<Integer, Double> conf_rank = rank.rank_conf(paperList);
			clusterList.get(cluster).rank_conf_map = conf_rank;

			clusterList.get(cluster).rank_conf_map = matrixUtil
					.matrixMultiplyNumber(
							clusterList.get(cluster).rank_conf_map,
							1 - lambda_s);
			HashMap<Integer, Double> conf_temp = matrixUtil
					.matrixMultiplyNumber(clusterList.get(k).rank_conf_map,
							lambda_s);
			clusterList.get(cluster).rank_conf_map = matrixUtil.matrixAdd(
					clusterList.get(cluster).rank_conf_map, conf_temp);
			// write to file, in order to avoid compute again.
			FileUtil.write(path, FileUtil.FILE_CONF,
					clusterList.get(cluster).rank_conf_map);
		} else {
			// read file
			clusterList.get(cluster).rank_conf_map = FileUtil.readFile(path
					+ "//" + FileUtil.FILE_CONF);

		}
		clusterList.get(cluster).rank_conf = matrixUtil
				.HashMapToArrayList(clusterList.get(cluster).rank_conf_map);
		System.gc();
	}

	public void authorRankIncluster(int iteratorNum, int cluster) {
		// author_rank
		String path = "iteration" + iteratorNum + "//cluster" + cluster;
		if (!FileUtil.isFileExist(path + "//" + FileUtil.FILE_AUTHOR)) {
			ArrayList<Integer> paperList = clusterList.get(cluster).paperList;
			HashMap<Integer, Double> author_rank = rank.rank_author(paperList);
			clusterList.get(cluster).rank_author_map = author_rank;
			clusterList.get(cluster).rank_author_map = matrixUtil
					.matrixMultiplyNumber(
							clusterList.get(cluster).rank_author_map,
							1 - lambda_s);
			HashMap<Integer, Double> author_temp = matrixUtil
					.matrixMultiplyNumber(clusterList.get(k).rank_author_map,
							lambda_s);
			clusterList.get(cluster).rank_author_map = matrixUtil.matrixAdd(
					clusterList.get(cluster).rank_author_map, author_temp);
			// write to file, in order to avoid compute again.
			FileUtil.write(path, FileUtil.FILE_AUTHOR,
					clusterList.get(cluster).rank_author_map);
		} else {
			// read file
			clusterList.get(cluster).rank_author_map = FileUtil.readFile(path
					+ "//" + FileUtil.FILE_AUTHOR);

		}
		clusterList.get(cluster).rank_author = matrixUtil
				.HashMapToArrayList(clusterList.get(cluster).rank_author_map);
		System.gc();
	}

	public void termRankIncluster(int iteratorNum, int cluster) {
		// term_rank
		String path = "iteration" + iteratorNum + "//cluster" + cluster;
		if (!FileUtil.isFileExist(path + "//" + FileUtil.FILE_TERM)) {
			ArrayList<Integer> paperList = clusterList.get(cluster).paperList;
			HashMap<Integer, Double> term_rank = rank.rank_term(paperList);
			clusterList.get(cluster).rank_term_map = term_rank;
			clusterList.get(cluster).rank_term_map = matrixUtil
					.matrixMultiplyNumber(
							clusterList.get(cluster).rank_term_map,
							1 - lambda_s);
			HashMap<Integer, Double> term_temp = matrixUtil
					.matrixMultiplyNumber(clusterList.get(k).rank_term_map,
							lambda_s);
			clusterList.get(cluster).rank_term_map = matrixUtil.matrixAdd(
					clusterList.get(cluster).rank_term_map, term_temp);
			// write to file, in order to avoid compute again.
			FileUtil.write(path, FileUtil.FILE_TERM,
					clusterList.get(cluster).rank_term_map);
		} else {
			// read file
			clusterList.get(cluster).rank_term_map = FileUtil.readFile(path
					+ "//" + FileUtil.FILE_TERM);

		}
		clusterList.get(cluster).rank_term = matrixUtil
				.HashMapToArrayList(clusterList.get(cluster).rank_term_map);
		System.gc();
	}

	public void step1(int t) {

		for (int i = 0; i < k; i++) {
			// compute every cluster's ranking-based probabilistic generative
			// model
			confRankIncluster(t, i);
			authorRankIncluster(t, i);
			termRankIncluster(t, i);
			rank.clear();
		}
		// paper rank for k cluster.
		for (int i = 0; i < k; i++) {
			paperRankInCluster(t, i);
		}
		System.gc();
	}

	public void step2(int t) {
		double[] pk = new double[k + 1];
		// init
		for (int i = 0; i < k + 1; i++) {
			pk[i] = 1.0 / (k + 1);
		}
		int totalPapers = Store.getInstance().p;
		double[][] pdkMap = new double[totalPapers][k + 1];// paper-k
		for (int i = 0; i < totalPapers; i++) {
			for (int j = 0; j < k + 1; j++)
				pdkMap[i][j] = 0;
		}
		boolean isContinue = true;

		for (int i = 0; i < ITER2 && isContinue; i++) {
			for (int j = 0; j < k + 1; j++) {
				HashMap<Integer, Double> paperMap = clusterList.get(j).rank_paper_map;// 看看是不是所有的paper
				Iterator<Integer> it = paperMap.keySet().iterator();
				while (it.hasNext()) {
					int paper = it.next();
					double rank = paperMap.get(paper);
					rank = rank * pk[j];
					pdkMap[paper][j] = rank;
				}
			}
			for (int i0 = 0; i0 < Store.getInstance().p; i0++)
				pdkMap[i0] = matrixUtil.normlize1DArray(pdkMap[i0]);
			// get p(k|di) for each k and each paper
			double[] newpk = new double[k + 1];
			for (int i0 = 0; i0 < k + 1; i0++) {
				newpk[i0] = 0;
			}
			// update pk for all cluster
			for (int j = 0; j < k + 1; j++) {
				// update pk for cluster i
				double sum = 0;
				for (int l = 0; l < totalPapers; l++) {
					sum += pdkMap[l][j];
				}
				newpk[j] = sum / totalPapers;
			}
			// compute diff
			double diff = 0;
			for (int i1 = 0; i1 < newpk.length; i1++) {
				diff += matrixUtil.absDiff(pk[i1], newpk[i1]);
			}
			System.out.println("step2 diff:" + diff);
			pk = newpk.clone();
			if (diff < 0.001) {
				System.out.println("em算法求paper posterior probability迭代次数:" + i);
				isContinue = false;
			}
		}
//		for (int i = 0; i < pk.length; i++) {
//			System.out
//					.println("----------------------------pk------------------------------");
//			System.out.println(pk[i]);
//			System.out
//					.println("----------------------------pk------------------------------");
//
//		}
		// write pdkMap to file
		// int paperid = Store.getInstance().vertex_p.get("436452").id;
		// FileUtil.write(FileUtil.FILE_POSTERIOR, pdkMap[paperid]);
		// last iteration, store pdkMap, for the sake of calculate the posterior
		// prob for each attribute object.
		this.paper_pdk = pdkMap;// finished, it's not need to ajust cluster.

	}

	public void step3() {
		// calculate each cluster's center.
		double[][] centers = getAllClustersCenter(this.paper_pdk);
		restoreAllClusters(this.paper_pdk, centers);
	}

	public double[][] getAllClustersCenter(double[][] pdkMap) {
		double[][] centers = new double[k][k];// each row is for each cluster
		for (int i = 0; i < k; i++) {
			double[] center = new double[k];
			for (int t = 0; t < center.length; t++)
				center[t] = 0;
			ArrayList<Integer> papersList = clusterList.get(i).paperList;
			for (int j = 0; j < papersList.size(); j++) {
				int paper = papersList.get(j);
				for (int l = 0; l < k; l++) {
					center[l] += pdkMap[paper][l];
				}

			}
			for (int t = 0; t < center.length; t++) {
				center[t] = center[t] / papersList.size();
			}
			centers[i] = center;
		}
		return centers;
	}

	public void restoreAllClusters(double[][] pdkMap, double[][] centers) {
		int total = Store.getInstance().p;
		int[] paperBelongList = new int[total];
		for (int i = 0; i < total; i++) {
			double distance = Double.MAX_VALUE;
			int belongK = 0;
			for (int j = 0; j < k; j++) {
				// compute two vector's distance.
				double innerProduct = matrixUtil.innerProduct(centers[j],
						pdkMap[i]);
				double length = matrixUtil.arrayLength(centers[j])
						* matrixUtil.arrayLength(pdkMap[i]);
				double distance1 = 1 - innerProduct / length;
				if (distance1 < distance) {
					distance = distance1;
					belongK = j;
				}
			}
			paperBelongList[i] = belongK;
		}
		// update each cluster's paperList
		for (int i = 0; i < k; i++) {
			clusterList.get(i).clearAll();
		}
		for (int i = 0; i < total; i++) {
			clusterList.get(paperBelongList[i]).paperList.add(i);
		}
	}

	// private void step2() {
	// pi = new double[s.c][k];
	// double[] pz = new double[k];
	//
	// for (int x = 0; x < k; x++) {
	// for (int y = 0; y < s.c; y++) {
	// pi[y][x] = (1.0 / k);// Ĭ����1/k
	// }
	// pz[x] = (1.0 / k);
	// }
	//
	// for (int j = 0; j < ITER; j++) {// ����5�ֵ��
	// for (int z = 0; z < k; z++) {
	// double den = 0.0, num = 0.0;
	//
	// for (int x = 0; x < s.c; x++) {
	// for (int y = 0; y < s.a; y++) {
	// num += s.xy(x, y) * pz[z]
	// * (this.clusterList.get(z).rx.get(x, 0))
	// * (this.clusterList.get(z).ry.get(y, 0));
	// den += s.xy(x, y);
	// }
	// }
	//
	// pz[z] = num / den;// p(z=k)
	// }
	//
	// for (int x = 0; x < s.c; x++) {
	// double den = 0.0;
	//
	// for (int y = 0; y < k; y++) {
	// den += this.clusterList.get(y).rx.get(x, 0) * pz[y];
	// }
	//
	// for (int y = 0; y < k; y++) {
	// pi[x][y] = (this.clusterList.get(y).rx.get(x, 0) * pz[y]) / den;
	// }
	// }
	// }
	//
	// for (int x = 0; x < k; x++) {
	// int cs = this.clusterList.get(x).l.size();
	//
	// for (int y = 0; y < cs; y++) {
	// for (int z = 0; z < k; z++) {
	// this.clusterList.get(x).s[z] += pi[this.clusterList.get(x).l.get(y)][z];
	// }
	// }
	//
	// for (int y = 0; y < k; y++) {
	// this.clusterList.get(x).s[y] /= cs;
	// }
	// }
	//
	// System.gc();
	// }
	//
	// private void step3() {
	// int[] m = new int[s.c];
	//
	// Iterator<String> it = this.s.v.keySet().iterator();
	// while (it.hasNext()) {
	// Vertex v = this.s.v.get(it.next());
	// if (v.type == Vertex.CONFERENCE) {
	// double min = Double.MAX_VALUE;
	//
	// for (int i = 0; i < k; i++) {
	// double num = 0.0, denp1 = 0.0, denp2 = 0.0;
	//
	// for (int j = 0; j < k; j++) {
	// num += pi[v.id][j] * this.clusterList.get(i).s[j];
	// denp1 += Math.pow(pi[v.id][j], 2);
	// denp2 += Math.pow(this.clusterList.get(i).s[j], 2);
	// }
	//
	// double d = (1 - (num / Math.sqrt(denp1 * denp2)));
	// if (d < min) {
	// min = d;
	// m[v.id] = i;// ��¼ ���� v.id ���ľ���i
	// }
	// }
	// }
	// }
	//
	// pi = null;// ÿһ�ֵ�theta ��Ҫ���¼���
	// clearAll();
	// beginCluster();
	// // ��ÿ��conf���¸�������ĸ�����������¸���ÿ����������
	// for (int i = 0; i < s.c; i++) {
	// this.clusterList.get(m[i]).l.add(i);
	// }
	//
	// System.gc();
	// }

	private void beginCluster() {
		this.clusterList = new ArrayList<SingleCluster>();
		for (int i = 0; i < k + 1; i++) {
			this.clusterList.add(new SingleCluster(i));
		}
	}

	// random partition each cluster.
	private void initCluster() {
		beginCluster();

		Iterator<String> it = Store.getInstance().vertex_p.keySet().iterator();
		// loop all paper
		while (it.hasNext()) {
			Vertex v = Store.getInstance().vertex_p.get(it.next());// get this
																	// paper
																	// vertex.
			Random random = new Random();
			int clusterNum = Math.abs(random.nextInt() % 4);
			v.cluster = clusterNum;
			this.clusterList.get(clusterNum).paperList.add(v.id);
			this.clusterList.get(k).paperList.add(v.id);// global network
		}
	}

	// private void clearAll() {
	// for (int i = 0; i < k; i++) {
	// this.clusterList.get(i).l.clear();// ���������conf�ᷢ��ı�
	// }
	// }
}
