/**
 * Store - Data store for the program
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * @author pksunkara
 */
public class Store {
	private static Store instance = null;
	public HashMap<String, Vertex> vertex_p;
	public HashMap<String, Vertex> temp_vertex_p;
	public HashMap<String, Vertex> vertex_t;
	public HashMap<String, Vertex> temp_vertex_t;
	public HashMap<String, Vertex> vertex_a;
	public HashMap<String, Vertex> temp_vertex_a;
	public ArrayList<String> paperCode;
	public ArrayList<String> authorCode;
	public ArrayList<String> confCode;
	public ArrayList<String> termCode;
	public HashMap<String, Vertex> vertex_c;

	// public HashMap<String, Edge> e;

	public int a = 0, c = 0, t = 0, p = 0, temp_p = 0, temp_t = 0, temp_a = 0;

	public HashMap<Integer, HashMap<Integer, Double>> pa;// paper-author
	public HashMap<Integer, HashMap<Integer, Double>> ap;// author-paper

	public HashMap<Integer, HashMap<Integer, Double>> pc;// paper-conf
	public HashMap<Integer, HashMap<Integer, Double>> cp;// conf-paper

	public HashMap<Integer, HashMap<Integer, Double>> pt;// paper-term set
	public HashMap<Integer, HashMap<Integer, Double>> tp;// term-paper set

	public Store() {
		this.vertex_p = new HashMap<String, Vertex>();
		this.temp_vertex_p = new HashMap<String, Vertex>();
		this.vertex_t = new HashMap<String, Vertex>();
		this.temp_vertex_t = new HashMap<String, Vertex>();
		this.temp_vertex_a = new HashMap<String, Vertex>();
		this.vertex_c = new HashMap<String, Vertex>();
		this.vertex_a = new HashMap<String, Vertex>();
		// this.e = new HashMap<String, Edge>();
		this.pa = new HashMap<Integer, HashMap<Integer, Double>>();
		this.pc = new HashMap<Integer, HashMap<Integer, Double>>();
		this.pt = new HashMap<Integer, HashMap<Integer, Double>>();
		this.tp = new HashMap<Integer, HashMap<Integer, Double>>();
		this.ap = new HashMap<Integer, HashMap<Integer, Double>>();
		this.cp = new HashMap<Integer, HashMap<Integer, Double>>();
		this.paperCode = new ArrayList<String>();
		this.authorCode = new ArrayList<String>();
		this.confCode = new ArrayList<String>();
		this.termCode = new ArrayList<String>();
	}

	public static synchronized Store getInstance() {
		if (instance == null)
			instance = new Store();
		return instance;
	}

	public void author(String itemId, String name) {
		this.temp_vertex_a.put(itemId, new Vertex(name, this.temp_a,
				Vertex.PERSON));
		// this.authorCode.add(itemId);
		this.temp_a++;// author number
	}

	public void term(String itemId, String name) {
		this.temp_vertex_t.put(itemId, new Vertex(name, this.temp_t,
				Vertex.TERM));
		// this.termCode.add(itemId);
		this.temp_t++;// term number
	}

	public void paper(String itemId, String name) {

		this.temp_vertex_p.put(itemId, new Vertex(name, this.temp_p,
				Vertex.PAPER));

		this.temp_p++;// term number

	}

	public void conference(String itemId, String name) {
		this.vertex_c.put(itemId, new Vertex(name, this.c, Vertex.CONFERENCE));
		this.confCode.add(itemId);
		this.c++;// conf number
	}

	public void paper_conf(String paperId, String confId) {
		// store paper
		this.vertex_p.put(paperId, new Vertex("", this.p, Vertex.PAPER));
		this.paperCode.add(paperId);
		this.p++;// paper number

		HashMap<Integer, Double> conf_value = new HashMap<Integer, Double>();
		conf_value.put(this.vertex_c.get(confId).id, (double) 1);
		pc.put(this.vertex_p.get(paperId).id, conf_value);
	}

	public void conf_paper(String confId, String paperId) {
		int paperid = this.vertex_p.get(paperId).id;
		int confid = this.vertex_c.get(confId).id;
		if (this.cp.containsKey(confid)) {
			this.cp.get(confid).put(paperid, (double) 1);
		} else {
			HashMap<Integer, Double> paperHM = new HashMap<Integer, Double>();
			paperHM.put(paperid, (double) 1);
			this.cp.put(confid, paperHM);
		}
	}

	public void paper_author(String paperId, String authorId) {
		// store author
		if (this.vertex_a.containsKey(authorId)) {
			//
		} else {
			this.authorCode.add(authorId);
			String authorName = Store.getInstance().temp_vertex_a.get(authorId).name;
			this.vertex_a.put(authorId, new Vertex(authorName, this.a,
					Vertex.PERSON));
			this.a++;
		}

		int paperid = this.vertex_p.get(paperId).id;
		int authorid = this.vertex_a.get(authorId).id;

		if (this.pa.containsKey(paperid)) {
			this.pa.get(paperid).put(authorid, (double) 1);
		} else {
			HashMap<Integer, Double> authorHM = new HashMap<Integer, Double>();
			authorHM.put(authorid, (double) 1);
			this.pa.put(paperid, authorHM);
		}
	}

	public void author_paper(String authorId, String paperId) {
		int paperid = this.vertex_p.get(paperId).id;
		int authorid = this.vertex_a.get(authorId).id;
		if (this.ap.containsKey(authorid)) {
			this.ap.get(authorid).put(paperid, (double) 1);
		} else {
			HashMap<Integer, Double> paperHM = new HashMap<Integer, Double>();
			paperHM.put(paperid, (double) 1);
			this.ap.put(authorid, paperHM);
		}
	}

	public void paper_term(String paperId, String termId)// paper and term is :
															// n-to-n
	{
		// store term
		if (this.vertex_t.containsKey(termId)) {
			//
		} else {
			this.termCode.add(termId);
			String termName = Store.getInstance().temp_vertex_t.get(termId).name;
			this.vertex_t.put(termId, new Vertex(termName, this.t,
					Vertex.TERM));
			this.t++;
		}
		

		int paperid = this.vertex_p.get(paperId).id;
		int termid = this.vertex_t.get(termId).id;
		if (this.pt.containsKey(paperid)) {
			if (this.pt.get(paperid).containsKey(termid)) {
				int old = this.pt.get(paperid).get(termid).intValue();
				old++;
				this.pt.get(paperid).put(termid, (double) old);
			} else {
				this.pt.get(paperid).put(termid, (double) 1);
			}
		} else {
			HashMap<Integer, Double> termHM = new HashMap<Integer, Double>();
			termHM.put(termid, (double) 1);
			this.pt.put(paperid, termHM);
		}
	}

	public void build() {
		// System.gc();
		// // this.formWdc();
		// // this.formWda();
		// // this.formWdt();
		// // this.cleanse();
		// System.gc();
	}

	public void cleanse() {
		this.pa = null;
		this.pc = null;
		this.pt = null;
	}

	public void term_paper(String termId, String paperId) {
		int paperid = this.vertex_p.get(paperId).id;
		int termid = this.vertex_t.get(termId).id;
		if (this.tp.containsKey(termid)) {
			if (this.tp.get(termid).containsKey(paperid)) {
				int old = this.tp.get(termid).get(paperid).intValue();
				old++;
				this.tp.get(termid).put(paperid, (double) old);
			} else {
				this.tp.get(termid).put(paperid, (double) 1);
			}
		} else {
			HashMap<Integer, Double> paperHM = new HashMap<Integer, Double>();
			paperHM.put(paperid, (double) 1);
			this.tp.put(termid, paperHM);
		}

	}

	// public void formWdc() {
	// this.wdc = new double[this.p][this.c];
	// Iterator<String> it = this.pc.keySet().iterator();
	// // iterate every paper-conf
	// while (it.hasNext()) {
	// String paperId = it.next();
	// String confId = this.pc.get(paperId);
	// this.wdc[this.vertex_p.get(paperId).id][this.vertex_c.get(confId).id]++;
	// }
	// }

	// public void formWda() {
	// this.wda = new double[this.p][this.a];
	// Iterator<String> it = this.pa.keySet().iterator();
	// // iterate every paper-conf
	// while (it.hasNext()) {
	// String paperId = it.next();
	// Set<String> authorSet = this.pa.get(paperId);
	// Iterator<String> author_it = authorSet.iterator();
	// while (author_it.hasNext()) {
	// String authorId = author_it.next();
	// this.wda[this.vertex_p.get(paperId).id][this.vertex_a
	// .get(authorId).id]++;
	// }
	// }
	// }

	// public void formWdt() {
	// this.wdt = new double[this.p][this.t];
	// Iterator<String> it = this.pa.keySet().iterator();
	// // iterate every paper-conf
	// while (it.hasNext()) {
	// String paperId = it.next();
	// Set<String> authorSet = this.pa.get(paperId);
	// Iterator<String> author_it = authorSet.iterator();
	// while (author_it.hasNext()) {
	// String authorId = author_it.next();
	// this.wda[this.vertex_p.get(paperId).id][this.vertex_a
	// .get(authorId).id]++;
	// }
	// }
	// }

}
