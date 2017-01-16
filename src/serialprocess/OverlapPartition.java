package serialprocess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2016/12/28.
 * 重叠社区划分结果 实体类
 */
public class OverlapPartition extends BasePartition implements Serializable {

    //保存重叠社区几点及其所属社区
    Map<String,List<String>> nodeMapCommunities;

    public Map<String, List<String>> getNodeMapCommunities() {
        return nodeMapCommunities;
    }

    public void setNodeMapCommunities(Map<String, List<String>> nodeMapCommunities) {
        this.nodeMapCommunities = nodeMapCommunities;
    }
}
