package serialprocess;

import serialprocess.*;
import utils.MyPrint;
import utils.MySerialization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 把网络图G、及其非重叠社区划分结果保存成 gephi能读取的CSV格式 文件
 * Created by Liyanzhen on 2017/2/22.
 */
public class SaveGraphAndComunitiesResultInCSVStyle {
    public static void main(String[] args){
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.2.18\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.2.18\\overlapPartition.obj");

        saveGraphInCSVStyle(g,bestNonOverlapPartition);
    }

    private static void saveGraphInCSVStyle(Graph g,Partition bestNonOverlapPartition){
        int m=g.map.size();//节点总数
        TreeMap<Integer,String> nodeIndexMapName = new TreeMap<Integer, String>();
        TreeMap<String,Integer> nodeNameMapIndex = new TreeMap<String, Integer>();
        //第一步：先建立 序号-节点名 对应关系
        Iterator iterator = g.map.entrySet().iterator();
        int index=0;
        while(iterator.hasNext()){
            Map.Entry<String, VertexNode> entry =(Map.Entry<String, VertexNode>) iterator.next();
            String vname = entry.getKey();
            nodeIndexMapName.put(index,vname);
            nodeNameMapIndex.put(vname,index);
            index++;
        }

        //第二步： 建立社区名 -社区序号 对应关系
        Map<String,Integer> commuNameMapIndex = new TreeMap<String, Integer>();
        Map<Integer,String> communityIndexMapName = new TreeMap<Integer, String>();

        Iterator it = bestNonOverlapPartition.getCommunities().entrySet().iterator();
        index =0;
        while(it.hasNext()){
            Map.Entry<String,List<String>> entry =(Map.Entry<String,List<String>>) it.next();
            String commuName = entry.getKey();
            commuNameMapIndex.put(commuName,index);
            communityIndexMapName.put(index,commuName);
            index++;
        }

        //第3步先保存节点集。 节点集表头：id   label   modularity_class
        try{
            FileWriter nodesWriter = new FileWriter("E:\\毕业论文\\3 Gephi处理网络图\\soybean-net-nodes.csv");
            BufferedWriter bwnodes = new BufferedWriter(nodesWriter);

            //先制作表头
            StringBuffer tableHeader = new StringBuffer();
            tableHeader.append("id ");
            tableHeader.append("\t");
            tableHeader.append("label");
            tableHeader.append("\t");
            tableHeader.append("modularity_class");

            bwnodes.write(tableHeader.toString());
            bwnodes.newLine();

            //开始遍历indexMapName中的节点 （因为这里是按key序号排序的，直接把key作为节点的id好了）
            StringBuffer sb = new StringBuffer();
            iterator = nodeIndexMapName.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<Integer,String> entry = (Map.Entry<Integer,String>)iterator.next();
                int id = entry.getKey();
                String label = entry.getValue();
                String comunityName = bestNonOverlapPartition.getNodeCommunityMap().get(label);
                int modularity_class = commuNameMapIndex.get(comunityName);
                sb = new StringBuffer();
                sb.append(id);
                sb.append("\t");
                sb.append(label);
                sb.append("\t");
                sb.append(modularity_class);
                bwnodes.write(sb.toString());
                bwnodes.newLine();
            }

            bwnodes.close();
            nodesWriter.close();
        }catch (Exception e){
            e.printStackTrace();
            MyPrint.print("将图 g保存为CSV格式的节点集时出现异常！");
        }

        try{
            FileWriter edgeWriter = new FileWriter("E:\\毕业论文\\3 Gephi处理网络图\\soybean-net-edges.csv");
            BufferedWriter bwedge = new BufferedWriter(edgeWriter);

            //先建立csv文件表头
            StringBuffer th = new StringBuffer();
            th.append("Source");
            th.append("\t");
            th.append("Target");
            th.append("\t");
            th.append("Type");
            th.append("\t");
            th.append("id");
            bwedge.write(th.toString());
            bwedge.newLine();

            //开始遍历打印网络g的所有边
            List<Edges>edges = g.totalEdgesList;
            int edgeId = 0;
            StringBuffer sb = new StringBuffer();
            for(Edges edge :edges){
                VertexNode from = edge.fromNode;
                VertexNode to = edge.toNode;
                int source =nodeNameMapIndex.get(from.vertexName);
                int target = nodeNameMapIndex.get(to.vertexName);
                sb = new StringBuffer();
                sb.append(source);
                sb.append("\t");
                sb.append(target);
                sb.append("\t");
                sb.append("Undirected");
                sb.append("\t");
                sb.append(edgeId);
                bwedge.write(sb.toString());
                bwedge.newLine();

                edgeId++;
            }

            bwedge.close();
            edgeWriter.close();
        }catch (Exception e){
            e.printStackTrace();
            MyPrint.print("保存图g的边出现异常！");
        }

    }
}
