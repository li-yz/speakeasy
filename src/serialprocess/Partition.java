package serialprocess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2016/12/23.
 */
public class Partition implements Serializable {
    //communities与nodeCommunityMap可以表示一个社区划分
    Map<String, List<String>> communities=new HashMap<String, List<String>>();
    Map<String, String> nodeCommunityMap=new HashMap<String, String>();
}
