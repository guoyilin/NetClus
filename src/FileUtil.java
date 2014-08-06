import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FileUtil {
	public static final String FILE_TERM = "termRank.txt";
	public static final String FILE_CONF = "confRank.txt";
	public static final String FILE_AUTHOR = "authorRank.txt";
	public static final String FILE_PAPER = "paperRank.txt";
	public static final String FILE_POSTERIOR = "result//simRankPosteriorChange.txt";
	public static final String ROOT = "data//";
	public static final String ROOT2 = "data//result//";
	public static final String FILE_CONFSRESULT = "result//confsResult.txt";

	public static void write(String path, String fileName,
			HashMap<Integer, Double> map) {
		String content = "";
		Iterator<Integer> it = map.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			double value = map.get(key);
			content += key + "\t" + value + "\n";
		}
		try {
			File f2;
			if (path != "") {
				File f = new File(ROOT + path);
				// create dir
				if (f.exists() == false) {
					f.mkdirs();
					System.out.println("路径不存在,但是已经成功创建了" + path);
				} else {
					System.out.println("文件路径存在" + path);
				}
				f2 = new File(ROOT + path + "//" + fileName);
			} else
				f2 = new File(ROOT + fileName);

			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f2, true)));
			output.write(content);
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write(String path, String fileName,
			ArrayList<IDRankPair> list) {
		String content = "";

		File f1 = new File(ROOT2 + path);
		// create dir
		if (f1.exists() == false) {
			f1.mkdirs();
			System.out.println("路径不存在,但是已经成功创建了" + path);
		} else {
			System.out.println("文件路径存在" + path);
		}

		for (int i = 0; i < list.size(); i++) {
			String code = list.get(i).code;
			String name = list.get(i).name;
			double rank = list.get(i).Rank;
			content += code + "\t" + name + "\t" + rank + "\n";
		}
		try {
			File f = new File(ROOT2 + path + "//" + fileName);
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f, true)));
			output.write(content);
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write(String path, double[] a) {
		String content = "";
		for (int i = 0; i < a.length; i++) {
			if (i != a.length - 1)
				content += a[i] + "\t";
			else
				content += a[i] + "\n";
		}
		try {
			File f = new File(ROOT + path);
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f, true)));
			output.write(content);
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isFileExist(String path) {
		File f = new File(ROOT + path);
		if (f.exists())
			return true;
		else
			return false;
	}

	public static HashMap<Integer, Double> readFile(String path) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		File file = new File(ROOT + path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(
					file), Charset.defaultCharset().name()));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String str[] = tempString.split("\t");
				map.put(Integer.valueOf(str[0]), Double.valueOf(str[1]));
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
		return map;
	}
}
