import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class temp {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File f=new File("info.txt");
		
		
		FileReader fr=new FileReader(f);
		BufferedReader br=new BufferedReader(fr);
		String x;
		while((x=br.readLine())!=null){
			String str[]=x.split(":");
			System.out.print(str[0]+":");
			//System.out.println("str[1]"+str[1]);
			String val[]=str[1].split(",");
			//System.out.println("Val"+val);
			for(int i=0;i<val.length;i++){
				//System.out.println("Val[]"+val[i]);
				String vals[]=val[i].split("-");
				char xx[]=vals[0].toCharArray();
				for(char ch:xx){
					System.out.print((int)ch);
				}
				System.out.print("-"+vals[1]+"-"+vals[2]+",");
			}
			System.out.println();
			//System.out.println(x+"***");
		}
	}

}
