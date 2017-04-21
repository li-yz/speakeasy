package test;

import utils.MyPrint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liyanzhen on 2017/4/21.
 */
public class GeneralTest {
    public static void main(String[] args){
        test1();
    }
    private static void test1(){
        Map<String,Integer> map = new HashMap<String, Integer>();
        map.put("one",1);
        map.put("two",2);
        map.put("threee",3);
        MyPrint.print("remove前entry个数="+map.size());
        map.remove("two");
        MyPrint.print("remove后entry个数="+map.size());
    }
}
