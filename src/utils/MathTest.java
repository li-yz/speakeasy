package utils;

import postprocess.AnalysisCommunitiesDistribution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Liyanzhen on 2017/1/13.
 */
public class MathTest {
    public static void main(String []args){
        MyPrint.print(":"+Math.log(1000));
        test();
        String str = "1 2 3 4";
        MyOutPut.saveStringResultToTxt(str,"D:\\paperdata\\soybean\\community detection\\筛选重叠节点Wv,c分布\\test.txt");
        AnalysisCommunitiesDistribution obj = new AnalysisCommunitiesDistribution();
        String[] strs =obj.getTrueOverlapCommunitiesNames();
        int[][]m = obj.getTrueOverlapCommunitiesIntersectionMatrix();
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
        MyPrint.print("交集"+result);
    }


}
