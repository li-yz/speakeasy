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
 * Created by Liyanzhen on 2017/6/22.
 */
public class ModularityOfXieVersion {

    public static void main(String[] args){
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");

        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        double modularity = calculateModularity(g,bestNonOverlapPartition);
        MyPrint.print("Xie�汾��Q = "+modularity);
    }

    public static double calculateModularity(Graph g, Partition partition){
        double q = 0.0d;

        int m = g.totalEdgesList.size();
        List<String> allNodesList = new ArrayList<String>();
        allNodesList.addAll(g.map.keySet());


        Map<String,List<String>> communities = partition.getCommunities();
        Iterator iterator = communities.entrySet().iterator();
        while(iterator.hasNext()){
            double contributionOfOneCommunity = 0.0d;
            Map.Entry entry =(Map.Entry<String,List<String>> ) iterator .next();
            List<String> nodesInCi = (List<String>)entry.getValue();

            double e_ci_in = 0.0d;//����Ci�ڵ��ܱ���
            for(String node :nodesInCi){
                VertexNode v = g.map.get(node);
                for(String neighbor: v.neighborList.keySet()){
                    if(nodesInCi.contains(neighbor)){
                        e_ci_in +=1;
                    }
                }
            }
            e_ci_in = e_ci_in/2;//�������ۼӵĹ����У�����Ci�ڵ�ͬһ���߱����������Σ����Ҫ����2

            double e_ci_out = 0.0d;//����Ci����������ı���
            for(String node :nodesInCi){
                VertexNode v = g.map.get(node);
                for(String neighbor :v.neighborList.keySet()){
                    if(!nodesInCi.contains(neighbor)){//����ǰ�ڵ�v���ٽڵ�������Ci֮��ĵ�
                        e_ci_out += 1;
                    }
                }
            }
            //����e_ci_outͳ�Ƶ���Ci�ڵĵ�������Ci֮��ĵ�֮��ı������ۼӹ����в�����ͬһ���߱������ε����������������2

            contributionOfOneCommunity = e_ci_in/m - Math.pow(((2*e_ci_in+e_ci_out)/(2*m)),2);
            q +=contributionOfOneCommunity;
        }

        return q;
    }
}
