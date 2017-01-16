package serialprocess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2016/12/23.
 */
public class Partition extends BasePartition implements Serializable {
    //基类的communities与nodeCommunityMap可以表示一个社区划分
    Map<String, String> nodeCommunityMap=new HashMap<String, String>();

    public Map<String, List<String>> getCommunities() {
        return communities;
    }

    public void setCommunities(Map<String, List<String>> communities) {
        this.communities = communities;
    }

    public Map<String, String> getNodeCommunityMap() {
        return nodeCommunityMap;
    }

    public void setNodeCommunityMap(Map<String, String> nodeCommunityMap) {
        this.nodeCommunityMap = nodeCommunityMap;
    }
}
