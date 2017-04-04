package postprocess;

import serialprocess.Graph;
import serialprocess.OverlapPartition;
import serialprocess.Partition;
import serialprocess.VertexNode;
import utils.MyPrint;
import utils.MySerialization;
import utils.MyUtils;

import java.util.*;

/**
 * Created by Liyanzhen on 2017/3/14.
 */
public class AnalysisWithKnownLiterature {
    public static void main(String []args){
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.2.26\\graph.obj");
        List<String> allNodeList = (List<String>) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\allNodeList\\allNodeList.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.2.26\\overlapPartition.obj");

        checkGSE8432SoybeanRustGenes(g,allNodeList, overlapPartition);
    }

    /**
     * �ж�����ͼȫ���ڵ����Ƿ������GSE8432ԭʼ�������ᵽ�ļ�������ⲡ��صĻ���
     *
     * @param allNodeList
     * @param overlapPartition
     */
    private static void checkGSE8432SoybeanRustGenes(Graph g,List<String> allNodeList , OverlapPartition overlapPartition){
        Set<String> genesInvolvedSoybeanRust = new HashSet<String>();
        genesInvolvedSoybeanRust.add("Gma.16735.2.S1_at");
        genesInvolvedSoybeanRust.add("GmaAffx.21211.1.S1_at");
        genesInvolvedSoybeanRust.add("GmaAffx.92386.1.S1_at");
        genesInvolvedSoybeanRust.add("GmaAffx.91805.1.S1_at");
        genesInvolvedSoybeanRust.add("Gma.6606.1.S1_at");
        genesInvolvedSoybeanRust.add("Gma.7559.1.S1_s_at");
        genesInvolvedSoybeanRust.add("GmaAffx.92383.1.S1_at");

        Set<String> allNodes = new HashSet<String>();
        allNodes.addAll(allNodeList);

        Set<String> intersection = MyUtils.findInteractionOf2Set(genesInvolvedSoybeanRust,allNodes);
        MyPrint.print("GSE8432ԭʼ�����й��ᵽ "+genesInvolvedSoybeanRust.size()+" ������ⲡ��صĻ���");

        //ȷ����Щ����������ĸ�������
        Iterator iterator = overlapPartition.getCommunities().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,List<String>> entry =(Map.Entry<String,List<String>>) iterator.next();

            Set<String> curCommunity = new HashSet<String>();
            curCommunity.addAll(entry.getValue());
            Set<String> set=MyUtils.findInteractionOf2Set(genesInvolvedSoybeanRust,curCommunity);
            if(set.size() > 0){
                MyPrint.print("��ǰ������"+entry.getKey()+",������С"+curCommunity.size()+" ,����"+set.size()+" ���ⲡ��ػ���");
                for(String e :set){
                    MyPrint.print(e);
                }
            }

        }

        //���� �⼸���ⲡ��ػ��� ��ͼg�еĶ�
        for(String e :genesInvolvedSoybeanRust){
            VertexNode v = g.map.get(e);

            MyPrint.print("���� "+e+" ��"+v.neighborList.size() +"���ھ�");
            Map<String,Double> neighbors = v.neighborList;
            for(Map.Entry entry :neighbors.entrySet()){
                MyPrint.print(e+"---"+entry.getKey()+"֮��ı�Ȩֵ="+entry.getValue());
            }
        }

    }

}
