package serialprocess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2016/12/28.
 * �ص��������ֽ�� ʵ����
 */
public class OverlapPartition extends BasePartition implements Serializable {

    //�����ص��������㼰����������
    Map<String,List<String>> nodeMapCommunities;

    public Map<String, List<String>> getNodeMapCommunities() {
        return nodeMapCommunities;
    }

    public void setNodeMapCommunities(Map<String, List<String>> nodeMapCommunities) {
        this.nodeMapCommunities = nodeMapCommunities;
    }
}
