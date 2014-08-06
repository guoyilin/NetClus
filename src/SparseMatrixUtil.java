import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;


/**
 * sparse matrix 's compute method
 * @author Administrator
 *
 */
public class SparseMatrixUtil {
	/**
	 * matrixB is more big than matrixA
	 * @param matrixA
	 * @param matrixB
	 * @return
	 */
	public HashMap<Integer, Double> matrixAdd(HashMap<Integer, Double> matrixA, HashMap<Integer, Double> matrixB)
	{
		Iterator<Integer> it = matrixA.keySet().iterator();
		while(it.hasNext())
		{
			int key = it.next();
			double value = matrixA.get(key);
			double valueB = matrixB.get(key);
			value  = value + valueB;
			matrixA.put(key, value);
		}
		return matrixA;
	}
	public HashMap<Integer, Double> matrixMultiplyNumber(HashMap<Integer, Double> matrix, double number)
	{
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		Iterator<Integer> it = matrix.keySet().iterator();
		while(it.hasNext())
		{
			int key = it.next();
			double value = matrix.get(key);
			value *= number;
			result.put(key, value);
		}
		
		return result;
	}
	public int getMaxElementInArray(double [] a)
	{
		int maxElement = 0;
		double max = 0;
		for(int i = 0 ; i < a.length; i++)
		{
			if(a[i] > max)
			{
				maxElement = i;
				max = a[i];
			}
		}
		if(max == 0)
			return -1;
		else
			return maxElement;
	}
	public double innerProduct(double[] a, double[] b)
	{
		double result = 0;
		for(int i = 0 ; i < a.length; i++)
		{
			result = result +  a[i]*b[i];
		}
		return result;
	}
	public double arrayLength(double[] a)
	{
		double result = 0;
		for(int i = 0; i<a.length;i++)
		{
			result = result + a[i]*a[i];
		}
		return Math.sqrt(result);
	}
	public HashMap<Integer, Double> computeMatrixEigenVector(HashMap<Integer, HashMap<Integer, Double>> matrix, int maxIterator)
	{
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		//init
		Iterator<Integer> it = matrix.keySet().iterator();
		while(it.hasNext())
		{	
			int key = it.next();
			result.put(key, 1.0);
		}
		
		for(int i = 0 ; i < maxIterator; i++)
		{
			double diff = 0;
			// calculate the matrix-by-vector product Ab
			HashMap<Integer, Double> newResult = new HashMap<Integer, Double>();
			Iterator<Integer> it2 = matrix.keySet().iterator();
			while(it2.hasNext())
			{
				double rowSum = 0;
				int key2 = it2.next();
				HashMap<Integer, Double> map = matrix.get(key2);
				Iterator<Integer> it3 = map.keySet().iterator();
				while(it3.hasNext())
				{
					int key3 = it3.next();
					rowSum += map.get(key3).doubleValue()*result.get(key3);
				}
				newResult.put(key2, rowSum);
			}
			// calculate the length of the resultant vector
			double norm=0;
			Iterator<Integer> it4 = newResult.keySet().iterator();
			while(it4.hasNext())
			{
				norm += newResult.get(it4.next());
			}
			Iterator<Integer> it5 = newResult.keySet().iterator();
			while(it5.hasNext())
			{
				int it5_key = it5.next();
				newResult.put(it5_key,  newResult.get(it5_key)/norm);
			}		
			//diff
			Iterator<Integer> it6 = result.keySet().iterator();
			while(it6.hasNext())
			{
				int key6 = it6.next();
				diff += absDiff(result.get(key6), newResult.get(key6));
			}
		
			System.out.println("diff:"+diff);
			if(diff < 0.01)
			{
				System.out.println("computeMatrixEigenVector method finished: diff="+diff+ "after iterator :" + i);
				return newResult;
			}else
			{
				result.clear();
				result = newResult;
			}
		}
		System.gc();
		return result;
	}
	public ArrayList<IDRankPair> HashMapToArrayList(HashMap<Integer, Double> map)
	{
		ArrayList<IDRankPair> result = new ArrayList<IDRankPair>();
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext())
		{
			int key = it.next();
			result.add(new IDRankPair(key, map.get(key)));
		}
		Comparator comp = new Mycomparator();
	    Collections.sort(result,comp);  
		return result;
	}
	public double absDiff(double a1, double a2)
	{
		if(a1 > a2)
			return a1-a2;
		else
			return a2-a1;
	}
	public HashMap<Integer, HashMap<Integer, Double>> computeWcaInwholeNetwork()
	{
		HashMap<Integer, HashMap<Integer, Double>> wdaMatrix = Store.getInstance().pa;
		HashMap<Integer, HashMap<Integer, Double>> normWdaMatrix = normMatrix(wdaMatrix);
		HashMap<Integer, HashMap<Integer, Double>> wcdMatrix = Store.getInstance().cp;
		return multiplyMatrix(wcdMatrix,normWdaMatrix);
	}
	
	public HashMap<Integer, HashMap<Integer, Double>> computeWcaInSubNetwork(ArrayList<Integer> paperList)
	{
		HashMap<Integer, HashMap<Integer, Double>> wdaMatrix = getSubMatrixWda(paperList);
		HashMap<Integer, HashMap<Integer, Double>> normWdaMatrix = normMatrix(wdaMatrix);
		HashMap<Integer, HashMap<Integer, Double>> wcdMatrix = getSubMatrixWcd(paperList);
		return multiplyMatrix(wcdMatrix,normWdaMatrix);
	}
	public HashMap<Integer, HashMap<Integer, Double>> computeWacInwholeNetwork()
	{
		HashMap<Integer, HashMap<Integer, Double>> wdcMatrix = Store.getInstance().pc;
		HashMap<Integer, HashMap<Integer, Double>> normWdcMatrix = normMatrix(wdcMatrix);
		HashMap<Integer, HashMap<Integer, Double>> wadMatrix = Store.getInstance().ap;
		return multiplyMatrix(wadMatrix,normWdcMatrix);
	}
	public HashMap<Integer, HashMap<Integer, Double>> computeWacInSubNetwork(ArrayList<Integer> paperList)
	{
		HashMap<Integer, HashMap<Integer, Double>>	wdcMatrix = getSubMatrixWdc(paperList);
		HashMap<Integer, HashMap<Integer, Double>> normWdcMatrix = normMatrix(wdcMatrix);
		HashMap<Integer, HashMap<Integer, Double>> wadMatrix = getSubMatrixWad(paperList);
		return multiplyMatrix(wadMatrix,normWdcMatrix);
	}
	public HashMap<Integer, HashMap<Integer, Double>> multiplyMatrix(HashMap<Integer, HashMap<Integer, Double>> matrixA, HashMap<Integer, HashMap<Integer, Double>> matrixB)
	{
		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> inverseMatrix = getMatrixTranspose(matrixB);
		Iterator<Integer> it_matrixA = matrixA.keySet().iterator();
		//loop all row
		//int count = 0;
		while (it_matrixA.hasNext()) {
//			count++;
//			if(count%100 == 0)
//				System.out.println(count);
			int row = it_matrixA.next();
			//loop all inverseMatrix's row
			Iterator<Integer> it_inverse = inverseMatrix.keySet().iterator();
			while(it_inverse.hasNext()){
				int row_inverse = it_inverse.next();
				Iterator<Integer> it = inverseMatrix.get(row_inverse).keySet().iterator();
				double rowSum = 0;
				while(it.hasNext())
				{
					int col = it.next();
					if(matrixA.get(row).containsKey(col)){
						rowSum += matrixA.get(row).get(col) * inverseMatrix.get(row_inverse).get(col);
					}
				}
				if(rowSum !=0 )
				{
					if(result.containsKey(row))
					{
						result.get(row).put(row_inverse, rowSum);
					}else{
						HashMap<Integer, Double> map = new HashMap<Integer, Double>();
						map.put(row_inverse, rowSum);
						result.put(row, map);
					}
				}
				
			}
				
		}
		System.gc();
		return result;
	}
	public HashMap<Integer, HashMap<Integer, Double>> normMatrix(
			HashMap<Integer, HashMap<Integer, Double>> matrix) {
		HashMap<Integer, HashMap<Integer, Double>> normMatrix = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> diagonalMatrix = getMatrixLowSum(matrix);
		Iterator<Integer> it = matrix.keySet().iterator();
		while (it.hasNext()) {
			int row = it.next();
			HashMap<Integer, Double> columnMap = matrix.get(row);															
			Iterator<Integer> column_it = columnMap.keySet()
					.iterator();
			while (column_it.hasNext()) {
				int column = column_it.next();
				double oldValue = columnMap.get(column);
				double newValue = oldValue / (diagonalMatrix.get(row));
				columnMap.put(column, newValue);
			}
			// update ap
			normMatrix.put(row, columnMap);
		}
		return normMatrix;
	}

	public HashMap<Integer, Double> getMatrixLowSum(
			HashMap<Integer, HashMap<Integer, Double>> matrix) {
		HashMap<Integer, Double> newMatrix = new HashMap<Integer, Double>();
		Iterator<Integer> it = matrix.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			HashMap<Integer, Double> rowMatrix = matrix.get(key);													
			double rowSum = 0;
			Iterator<Double> row_it = rowMatrix.values().iterator();
			while (row_it.hasNext()) {
				rowSum += row_it.next();
			}
			newMatrix.put(key, rowSum);
		}
		return newMatrix;
	}

	public HashMap<Integer, HashMap<Integer, Double>> getSubMatrixWda(
			ArrayList<Integer> paperList) {
		HashMap<Integer, HashMap<Integer, Double>> newWda = new HashMap<Integer, HashMap<Integer, Double>>();
		for (int i = 0; i < paperList.size(); i++) {
			int paperid = paperList.get(i);
			HashMap<Integer, Double> map = Store.getInstance().pa.get(paperid);
			if(map != null)
				newWda.put(paperid, map);
		}
		return newWda;
	}
	public HashMap<Integer, HashMap<Integer, Double>> getSubMatrixWad(
			ArrayList<Integer> paperList) {
		HashMap<Integer, HashMap<Integer, Double>>	wdaMatrix = getSubMatrixWda(paperList);
		return getMatrixTranspose(wdaMatrix);
	}
	public HashMap<Integer, HashMap<Integer, Double>> getSubMatrixWdc(
			ArrayList<Integer> paperList) {
		HashMap<Integer, HashMap<Integer, Double>> newWdc = new HashMap<Integer, HashMap<Integer, Double>>();
		for (int i = 0; i < paperList.size(); i++) {
			int paperid = paperList.get(i);
			HashMap<Integer, Double> confsMap =Store.getInstance().pc.get(paperid);
			if(confsMap != null)
			newWdc.put(paperid, confsMap);
		}
		return newWdc;
	}

	public HashMap<Integer, HashMap<Integer, Double>> getSubMatrixWcd(
			ArrayList<Integer> paperList) {
		HashMap<Integer, HashMap<Integer, Double>>	wdcMatrix = getSubMatrixWdc(paperList);
		return getMatrixTranspose(wdcMatrix);

	}
	public HashMap<Integer, Double> normlize1DHashMap(HashMap<Integer, Double> map)
	{
		Iterator<Double> rank_it = map.values().iterator();
		double sum = 0;
		while(rank_it.hasNext())
		{
			sum += rank_it.next();
		}
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext())
		{
			int key = it.next();
			double rank = map.get(key);
			rank = rank/sum;
			map.put(key, rank);
		}
		return map;
	}
	public double[] normlize1DArray(double[] map)
	{
		double sum = 0;
		for(int i = 0 ; i < map.length; i++)
			sum+= map[i];
		for(int i = 0 ; i < map.length;i++)
		{
			map[i] = map[i]/sum;
		}
		return map;
	}
	/**
	 * get matrix's transpose
	 * 
	 * @param matrix
	 * @return
	 */
	public HashMap<Integer, HashMap<Integer, Double>> getMatrixTranspose(
			HashMap<Integer, HashMap<Integer, Double>> matrix) {
		HashMap<Integer, HashMap<Integer, Double>> inverseMatrix = new HashMap<Integer, HashMap<Integer, Double>>();
		Iterator<Integer> it = matrix.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			HashMap<Integer, Double> map = matrix.get(key);
			Iterator<Integer> it_map = map.keySet().iterator();
			while (it_map.hasNext()) {
				int key_map = it_map.next();
				double value = map.get(key_map);
				if (inverseMatrix.containsKey(key_map)) {
					inverseMatrix.get(key_map).put(key, value);
				} else {
					HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
					temp.put(key, value);
					inverseMatrix.put(key_map, temp);
				}
			}
		}
		return inverseMatrix;
	}
}
