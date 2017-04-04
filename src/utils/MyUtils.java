package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * �����������ϵĽ���
     * @param set1
     * @param set2
     * @return
     */
    public static Set<String> findInteractionOf2Set(Set<String>set1 ,Set<String>set2){
        Set<String> result = new HashSet<String>();

        result.clear();
        result.addAll(set1);
        result.retainAll(set2);
        return result;
    }
}
