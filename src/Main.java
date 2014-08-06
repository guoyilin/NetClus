
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int count = Main.compute(12);
		System.out.println(count);
	}
	public static int compute(int number)
	{
		int count = 0;
		int temp = 0;
		for(int i = 1;  i <=  number; i++)
		{
			while(i>0)
			{
				i = i/10;
				temp = i%10;
				if(temp == 1)
					count++;
			}	
		}
		return count;
	}

}
