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
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.2.26\\graph.obj");
        List<String> allNodeList = (List<String>) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\allNodeList\\allNodeList.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.2.26\\overlapPartition.obj");

        checkGSE8432SoybeanRustGenes(g,allNodeList, overlapPartition);
    }

    /**
     * 判断网络图全部节点中是否包含了GSE8432原始文献中提到的几个与大豆锈病相关的基因
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
        MyPrint.print("GSE8432原始论文中共提到 "+genesInvolvedSoybeanRust.size()+" 个与大豆锈病相关的基因");

        //确认这些基因出现在哪个社区里
        Iterator iterator = overlapPartition.getCommunities().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,List<String>> entry =(Map.Entry<String,List<String>>) iterator.next();

            Set<String> curCommunity = new HashSet<String>();
            curCommunity.addAll(entry.getValue());
            Set<String> set=MyUtils.findInteractionOf2Set(genesInvolvedSoybeanRust,curCommunity);
            if(set.size() > 0){
                MyPrint.print("当前社区："+entry.getKey()+",社区大小"+curCommunity.size()+" ,包含"+set.size()+" 个锈病相关基因：");
                for(String e :set){
                    MyPrint.print(e);
                }
            }

        }

        //看下 这几个锈病相关基因 在图g中的度
        for(String e :genesInvolvedSoybeanRust){
            VertexNode v = g.map.get(e);

            MyPrint.print("基因： "+e+" 有"+v.neighborList.size() +"个邻居");
            Map<String,Double> neighbors = v.neighborList;
            for(Map.Entry entry :neighbors.entrySet()){
                MyPrint.print(e+"---"+entry.getKey()+"之间的边权值="+entry.getValue());
            }
        }

    }

}
