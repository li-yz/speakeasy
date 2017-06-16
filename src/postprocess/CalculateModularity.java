package postprocess;

import serialprocess.Graph;
import serialprocess.Partition;
import serialprocess.VertexNode;
import utils.MyPrint;
import utils.MySerialization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 计算模块度
 *
 * Created by Liyanzhen on 2017/1/10.
 */
public class CalculateModularity {



    public static double calculateModularity(Graph g, Partition partition){
        double q = 0.0d;
        int m = g.totalEdgesList.size();//网络图g中的总边数
        //        如果(原始网络图文件中 同一条无向边被表示了2次)，m要除以二
        //m=m/2;

        int n=0;//n表示节点总数

        //保存所有节点，便于循环遍历所有节点
        List<String> allNodeList=new ArrayList<String>();

        n=g.map.size();
        Iterator iter=g.map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry=(Map.Entry)iter.next();
            String node=(String)entry.getKey();
            allNodeList.add(node);
        }
//        System.out.println("把网络图的节点名都保存到1个list中，方便遍历，节点总数"+allNodeList.size());

        Map<String, String> nodeCommunityMap = partition.getNodeCommunityMap();

        for(int i=0;i < n;i++){
            for(int j=i+1;j < n;j++){
                int Aij = 0;
                VertexNode nodei = g.map.get(allNodeList.get(i));
                VertexNode nodej = g.map.get(allNodeList.get(j));
                if(nodei.neighborList.containsKey(allNodeList.get(j)) ){
                    Aij=1;
                }
                int Ki=nodei.neighborList.size();//节点i的度
                int Kj=nodej.neighborList.size();//节点j的度

                //如果节点i、j属于同一个社区，则对模块度值有一个贡献值
                if(nodeCommunityMap.get(nodei.vertexName).equals(nodeCommunityMap.get(nodej.vertexName))){
                    double temp = Aij - ((double)(Ki*Kj)/(2*m));
                    q += temp;
                }
            }
        }
        q= q/(2*m);

        return q;
    }
}
