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
 * ������ͼG��������ص��������ֽ������� gephi�ܶ�ȡ��CSV��ʽ �ļ�
 * Created by Liyanzhen on 2017/2/22.
 */
public class SaveGraphAndComunitiesResultInCSVStyle {
    public static void main(String[] args){
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.2.18\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.2.18\\overlapPartition.obj");

        saveGraphInCSVStyle(g,bestNonOverlapPartition);
    }

    private static void saveGraphInCSVStyle(Graph g,Partition bestNonOverlapPartition){
        int m=g.map.size();//�ڵ�����
        TreeMap<Integer,String> nodeIndexMapName = new TreeMap<Integer, String>();
        TreeMap<String,Integer> nodeNameMapIndex = new TreeMap<String, Integer>();
        //��һ�����Ƚ��� ���-�ڵ��� ��Ӧ��ϵ
        Iterator iterator = g.map.entrySet().iterator();
        int index=0;
        while(iterator.hasNext()){
            Map.Entry<String, VertexNode> entry =(Map.Entry<String, VertexNode>) iterator.next();
            String vname = entry.getKey();
            nodeIndexMapName.put(index,vname);
            nodeNameMapIndex.put(vname,index);
            index++;
        }

        //�ڶ����� ���������� -������� ��Ӧ��ϵ
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

        //��3���ȱ���ڵ㼯�� �ڵ㼯��ͷ��id   label   modularity_class
        try{
            FileWriter nodesWriter = new FileWriter("E:\\��ҵ����\\3 Gephi��������ͼ\\soybean-net-nodes.csv");
            BufferedWriter bwnodes = new BufferedWriter(nodesWriter);

            //��������ͷ
            StringBuffer tableHeader = new StringBuffer();
            tableHeader.append("id ");
            tableHeader.append("\t");
            tableHeader.append("label");
            tableHeader.append("\t");
            tableHeader.append("modularity_class");

            bwnodes.write(tableHeader.toString());
            bwnodes.newLine();

            //��ʼ����indexMapName�еĽڵ� ����Ϊ�����ǰ�key�������ģ�ֱ�Ӱ�key��Ϊ�ڵ��id���ˣ�
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
            MyPrint.print("��ͼ g����ΪCSV��ʽ�Ľڵ㼯ʱ�����쳣��");
        }

        try{
            FileWriter edgeWriter = new FileWriter("E:\\��ҵ����\\3 Gephi��������ͼ\\soybean-net-edges.csv");
            BufferedWriter bwedge = new BufferedWriter(edgeWriter);

            //�Ƚ���csv�ļ���ͷ
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

            //��ʼ������ӡ����g�����б�
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
            MyPrint.print("����ͼg�ı߳����쳣��");
        }

    }
}
