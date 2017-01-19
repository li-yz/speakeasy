package utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Liyanzhen on 2017/1/13.
 */
public class MathTest {
    public static void main(String []args){
        MyPrint.print(":"+Math.log(1000));
        test();
        String str = "1 2 3 4";
        MyOutPut.saveStringResultToTxt(str,"D:\\paperdata\\soybean\\community detection\\ɸѡ�ص��ڵ�Wv,c�ֲ�\\test.txt");
    }
    private static void test(){
        Set<Integer> result = new HashSet<Integer>();
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();

        set1.add(1);
        set1.add(3);
        set1.add(5);
        set2.add(1);
        set2.add(2);
        set2.add(3);

        result.clear();
        result.addAll(set1);
        result.retainAll(set2);
        MyPrint.print("����"+result);
    }


}
