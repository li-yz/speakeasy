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
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");

        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        double modularity = calculateModularity(g,bestNonOverlapPartition);
        MyPrint.print("Xie版本，Q = "+modularity);
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

            double e_ci_in = 0.0d;//社区Ci内的总变数
            for(String node :nodesInCi){
                VertexNode v = g.map.get(node);
                for(String neighbor: v.neighborList.keySet()){
                    if(nodesInCi.contains(neighbor)){
                        e_ci_in +=1;
                    }
                }
            }
            e_ci_in = e_ci_in/2;//在上面累加的过程中，社区Ci内的同一条边被计算了两次，因此要除以2

            double e_ci_out = 0.0d;//社区Ci与外界相连的边数
            for(String node :nodesInCi){
                VertexNode v = g.map.get(node);
                for(String neighbor :v.neighborList.keySet()){
                    if(!nodesInCi.contains(neighbor)){//即当前节点v的临节点是社区Ci之外的点
                        e_ci_out += 1;
                    }
                }
            }
            //由于e_ci_out统计的是Ci内的点与社区Ci之外的点之间的边数，累加过程中不存在同一条边被加两次的情况，因此无需除以2

            contributionOfOneCommunity = e_ci_in/m - Math.pow(((2*e_ci_in+e_ci_out)/(2*m)),2);
            q +=contributionOfOneCommunity;
        }

        return q;
    }
}
