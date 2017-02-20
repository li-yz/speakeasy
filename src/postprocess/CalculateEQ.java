package postprocess;

import serialprocess.Graph;
import serialprocess.OverlapPartition;
import serialprocess.Partition;
import serialprocess.VertexNode;
import utils.MyPrint;
import utils.MySerialization;

import java.util.*;

/**
 * EQ是模块度的改进版本，专门用来评价重叠社区 的模块性
 * Created by Liyanzhen on 2017/2/16.
 */
public class CalculateEQ {
    public static void main(String[] args){
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

//        double eq = getEQ(g,overlapPartition);
//        MyPrint.print("冯论文版EQ = "+eq);
//
        double eq2 = calculateEQOfShen(g,overlapPartition);
        MyPrint.print("shen原版论文 EQ = "+eq2);
    }

    public static double getEQ(Graph g, OverlapPartition overlapPartition){
        //
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");

        double q = 0.0d;
        int m = g.totalEdgesList.size();//网络图g中的总边数 注意！！！注意！！！注意！！！注意！！！注意！！！注意！！！若是无向图原始网络图数据文件中 同一条边表示了两次，则要把 m 除以二才是真实的边数
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

        Map<String, List<String>> nodeMapCommunities = overlapPartition.getNodeMapCommunities();

        //由于之前设计的OverlapPartition的数据结构不太合理，nodeMapCommunities只保存了重叠节点及其归属社区情况，没有直接保存非重叠节点及其所属社区，不便于直接判断两个节点 nodei 与 nodej是否属于同一个社区，
        //因此 从bestNonOverlapPartition之中找保存的非重叠节点-及其所属社区，并合并成一样的结构，合并到allNodeMapCommunities之中。
        Map<String, List<String>> allNodeMapCommunities= mergeAndGetTotalNodemapCommunities(bestNonOverlapPartition,nodeMapCommunities);

        for(int i=0;i < n;i++){
            for(int j=i+1;j < n;j++){
                int Aij = 0;
                VertexNode nodei = g.map.get(allNodeList.get(i));
                VertexNode nodej = g.map.get(allNodeList.get(j));
                if(nodei.neighborList.contains(allNodeList.get(j)) || nodej.neighborList.contains(allNodeList.get(i))){
                    Aij=1;
                }
                int Ki=nodei.neighborList.size();//节点i的度
                int Kj=nodej.neighborList.size();//节点j的度

                //如果节点i、j属于同一个社区，则对模块度值有一个贡献值
                if(isInTheSameCommunity(allNodeMapCommunities.get(nodei.vertexName),allNodeMapCommunities.get(nodej.vertexName))){
                    int oi = allNodeMapCommunities.get(nodei.vertexName).size();//节点i所属的社区数
                    int oj = allNodeMapCommunities.get(nodej.vertexName).size();//节点j所属的社区数

                    double temp =  ((double)1/(oi*oj))*(Aij - ((double)(Ki*Kj)/(2*m)));
                    q += temp;
                }
            }
        }
        q= q/(2*m);

        return q;
    }
    private static boolean isInTheSameCommunity(List<String>ci,List<String>cj){
        boolean r = false;

        Set<String> result = new HashSet<String>();
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();

        for(String e:ci){
            set1.add(e);
        }
        for(String e :cj){
            set2.add(e);
        }

        result.clear();
        result.addAll(set1);
        result.retainAll(set2);
        if(result.size() > 0){
            r = true;
        }

        return r;
    }

    private static Map<String,List<String>> mergeAndGetTotalNodemapCommunities(Partition bestNonOverlapPartition, Map<String, List<String>> nodeMapCommunities) {
        Map<String,List<String>> totalNodeMapCommunities = new HashMap<String, List<String>>();
        totalNodeMapCommunities.putAll(nodeMapCommunities);
        Iterator iter = bestNonOverlapPartition.getNodeCommunityMap().entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)iter.next();
            String vertexName=entry.getKey();
            if(totalNodeMapCommunities.containsKey(vertexName)){//即当前节点vertexName是重叠节点
                continue;
            }
            List<String> list = new ArrayList<String>();
            list.add(bestNonOverlapPartition.getNodeCommunityMap().get(vertexName));
            totalNodeMapCommunities.put(vertexName,list);
        }
        return totalNodeMapCommunities;
    }

    public static double calculateEQOfShen(Graph g, OverlapPartition overlapPartition){
        //
        double q = 0.0d;
        int m = g.totalEdgesList.size();//网络图g中的总边数 注意！！！注意！！！注意！！！注意！！！注意！！！注意！！！若是无向图原始网络图数据文件中 同一条边表示了两次，则要把 m 除以二才是真实的边数
//        如果(原始网络图文件中 同一条无向边被表示了2次)，m要除以二
//        m=m/2;
        int n=0;//n表示节点总数

        Map<String, List<String>> nodeMapCommunities = overlapPartition.getNodeMapCommunities();

        Map<String,List<String>> communities = overlapPartition.getCommunities();
        Iterator iterator = communities.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String commuName =(String) entry.getKey();
            List<String> nodes = (List<String>) entry.getValue();
            for(int i=0;i < nodes.size();i++){
                for(int j=i+1;j <nodes.size();j++){
                    int Aij = 0;
                    VertexNode nodei = g.map.get(nodes.get(i));
                    VertexNode nodej = g.map.get(nodes.get(j));
                    if(nodei.neighborList.contains(nodes.get(j)) || nodej.neighborList.contains(nodes.get(i))){
                        Aij=1;//节点i与j之间有边相连
                    }
                    int Ki=nodei.neighborList.size();//节点i的度
                    int Kj=nodej.neighborList.size();//节点j的度

                    int oi=1;
                    int oj=1;
                    if(overlapPartition.getNodeMapCommunities().containsKey(nodei.vertexName)){
                        oi = overlapPartition.getNodeMapCommunities().get(nodei.vertexName).size();
                    }
                    if(overlapPartition.getNodeMapCommunities().containsKey(nodej.vertexName)){
                        oj = overlapPartition.getNodeMapCommunities().get(nodej.vertexName).size();
                    }

                    double temp = ((double)1/(oi*oj))*(Aij - (double)(Ki*Kj)/(2*m));
                    q+=temp;
                }
            }
        }

        q= q/(2*m);

        return q;
    }

}
