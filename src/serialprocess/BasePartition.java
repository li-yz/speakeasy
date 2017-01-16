package serialprocess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Partition（划分）的基类
 *
 * 重叠划分OverlapPartition、非重叠划分Partition类 都继承BasePartition
 *
 * Created by Liyanzhen on 2017/1/16.
 */
public class BasePartition implements Serializable {
    /**
     * 保存社区划分
     */
    Map<String, List<String>> communities=new HashMap<String, List<String>>();

    public Map<String, List<String>> getCommunities() {
        return communities;
    }

    public void setCommunities(Map<String, List<String>> communities) {
        this.communities = communities;
    }
}
