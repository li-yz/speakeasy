package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * �����࣬��һЩ�򵥵��󽻼����󲢼������ƵĹ���
 * Created by Liyanzhen on 2017/2/15.
 */
public class MyUtils {
    /**
     * ��ͬһ��������־���ص����� VS ���ص����� ������ص��ڵ�
     * @param overlapList
     * @param nonOverlapList
     * @return
     */
    public static List<String> getOverlapNodes(List<String> overlapList,List<String>nonOverlapList){
        List<String> result = new ArrayList<String>();
        for(String e: overlapList){
            if(!nonOverlapList.contains(e)){
                result.add(e);
            }
        }
        return result;
    }
}
