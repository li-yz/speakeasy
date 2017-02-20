package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类，做一些简单的求交集、求并集等类似的工具
 * Created by Liyanzhen on 2017/2/15.
 */
public class MyUtils {
    /**
     * 求同一个社区标志的重叠社区 VS 非重叠社区 多出的重叠节点
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
