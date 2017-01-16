package serialprocess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Partition�����֣��Ļ���
 *
 * �ص�����OverlapPartition�����ص�����Partition�� ���̳�BasePartition
 *
 * Created by Liyanzhen on 2017/1/16.
 */
public class BasePartition implements Serializable {
    /**
     * ������������
     */
    Map<String, List<String>> communities=new HashMap<String, List<String>>();

    public Map<String, List<String>> getCommunities() {
        return communities;
    }

    public void setCommunities(Map<String, List<String>> communities) {
        this.communities = communities;
    }
}
