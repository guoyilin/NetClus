/**
 * Parser.java - To parse the dataset
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Parser {

	private static final String AUTHOR_FILE = "users.txt";
	private static final String CONF_FILE = "group.txt";
	private static final String TERM_FILE = "tags.txt";
	private static final String PAPER_FILE = "photo.txt";
	private static final String PAPER_AUTHOR_FILE = "photo_user.txt";
	private static final String PAPER_CONF_FILE = "photo_group.txt";
	private static final String PAPER_TERM_FILE = "photo_tag.txt";
	private HashMap<String, Integer> confSet;

	public Parser() {
		confSet = new HashMap<String, Integer>();
		read_author();
		read_paper();
		read_conf();
		read_term();
		read_paper_conf();
		paperSelect();
		read_paper_author();
		read_paper_term();

	}

	private void paperSelect() {
		Iterator<Integer> it = Store.getInstance().pc.keySet().iterator();
		while (it.hasNext()) {
			int paperid = it.next();
			String paperCode = Store.getInstance().paperCode.get(paperid);
			String paperName = Store.getInstance().temp_vertex_p.get(paperCode).name;
			Store.getInstance().vertex_p.put(paperCode, new Vertex(paperName,
					paperid, Vertex.PAPER));
		}
		Store.getInstance().temp_vertex_p = null;
	}

	private void read_paper_author() {
		File file = new File(PAPER_AUTHOR_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				if (Store.getInstance().vertex_p.containsKey(str[0])) {
					Store.getInstance().paper_author(str[0], str[1]);
					Store.getInstance().author_paper(str[1], str[0]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private void read_paper_conf() {
		File file = new File(PAPER_CONF_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				if (confSet.containsKey(str[1])) {
					int oldvalue = confSet.get(str[1]);
					if (oldvalue < 500)// 每个会议少于500篇论文
					{
						Store.getInstance().paper_conf(str[0], str[1]);
						Store.getInstance().conf_paper(str[1], str[0]);
						oldvalue++;
						confSet.put(str[1], oldvalue);
					}

				} else {
					confSet.put(str[1], 1);
					Store.getInstance().paper_conf(str[0], str[1]);
					Store.getInstance().conf_paper(str[1], str[0]);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private void read_paper_term() {
		File file = new File(PAPER_TERM_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				if (Store.getInstance().vertex_p.containsKey(str[0])) {
					Store.getInstance().paper_term(str[0], str[1]);
					Store.getInstance().term_paper(str[1], str[0]);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private void read_author() {
		File file = new File(AUTHOR_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				Store.getInstance().author(str[0], str[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private void read_paper() {
		File file = new File(PAPER_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				if(str.length < 3)
					Store.getInstance().paper(str[0], "");
				else
					Store.getInstance().paper(str[0], str[1]);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private void read_conf() {
		File file = new File(CONF_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				Store.getInstance().conference(str[0], str[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private void read_term() {
		File file = new File(TERM_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				Store.getInstance().term(str[0], str[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

}
