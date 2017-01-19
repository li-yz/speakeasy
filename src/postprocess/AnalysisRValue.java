package postprocess;

import utils.MyPrint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ��ɸѡ�ص������ڵ�Ĺ����У���Ҫһ����ֵr�����������ÿһ���ڵ��ƽ��Ȩֵ
 *
 * ����ֲ������һ����ŵľ�ֵ
 * Created by Liyanzhen on 2017/1/18.
 */
public class AnalysisRValue {
    public static void main(String[] args){
        readWvcDataAndReturnMean("D:\\paperdata\\soybean\\community detection\\ɸѡ�ص��ڵ�Wv,c�ֲ�\\Wvc.txt");

        compareLPAndConsensus();
    }
    public static double readWvcDataAndReturnMean(String path){
        double sum  = 0.0d;
        int numOfNoneZero = 0;
        double mean = 0.0d;
        double min = 10;
        double max = 0.0d;
        try{
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            String[] arr = line.split(" ");

            for(String v :arr){
                double e = Double.parseDouble(v);
                if(e > 0){
                    numOfNoneZero++;
                    sum+=e;
                    if(e > max){
                        max = e;
                    }
                    if(e < min){
                        min = e;
                    }
                }
            }
            mean = sum/numOfNoneZero;
            MyPrint.print("���� "+numOfNoneZero+" ������ֵ");
            MyPrint.print("��ֵ = "+mean);
            MyPrint.print("��Сֵ = "+min);
            MyPrint.print("���ֵ = "+max);

            br.close();
            fr.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mean;
    }

    public static void compareLPAndConsensus(){
        try{
            File file = new File("D:\\paperdata\\soybean\\community detection\\ɸѡ�ص��ڵ�Wv,c�ֲ�\\Wvc.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            String[] arr1 = line.split(" ");

            br.close();
            fr.close();

            File file2 = new File("D:\\paperdata\\soybean\\community detection\\��ʷ������\\14��32\\Wvc.txt");
            FileReader fr2 = new FileReader(file2);
            BufferedReader br2 = new BufferedReader(fr2);
            String line2 = br2.readLine();
            br2.close();
            fr2.close();

            String[] arr2 = line2.split(" ");

            int l1=arr1.length;
            int l2 = arr2.length;
            int sameNum=0;
            for(int i=0;i < l1 &&i <l2;i++){
                if(arr1[i].equals(arr2[i])){
                    sameNum++;
                }
            }

            MyPrint.print("��Wvc���ݸ��� "+l1);
            MyPrint.print("ԭʼWvc�����ݸ���"+l2);
            MyPrint.print("��������ͬ�ĸ���"+sameNum);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
