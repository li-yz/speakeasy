package serialprocess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2016/12/28.
 * �ص��������ֽ�� ʵ����
 */
public class OverlapPartition implements Serializable {
    /**
     * ������������
     */
    Map<String,List<String>> communities;

    //�����ص��������㼰����������
    Map<String,List<String>> nodeMapCommunities;

    public Map<String, List<String>> getCommunities() {
        return communities;
    }

    public void setCommunities(Map<String, List<String>> communities) {
        this.communities = communities;
    }

    public Map<String, List<String>> getNodeMapCommunities() {
        return nodeMapCommunities;
    }

    public void setNodeMapCommunities(Map<String, List<String>> nodeMapCommunities) {
        this.nodeMapCommunities = nodeMapCommunities;
    }
}
