package postprocess;

import serialprocess.Graph;
import serialprocess.OverlapPartition;
import serialprocess.Partition;
import utils.CommunityAnalysisResultOutput;
import utils.FastSort;
import utils.MyPrint;
import utils.MySerialization;

import java.io.IOException;
import java.util.*;

/**
 * Created by Liyanzhen on 2016/12/28.
 *
 * 跑完社区发现算法后， 对得到的社区结果进行量化分析
 */
public class Analysis {
    public static void main(String[] args){
        Analysis analysis = new Analysis();
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

        MyPrint.print("非重叠社区个数： "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("重叠社区个数： "+overlapPartition.getCommunities().size());

        analysis.getNonOverlapCommunitySizeDistribution(bestNonOverlapPartition);
        analysis.getCommunitySizeDistribution(overlapPartition);

        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
//        double mudularity = CalculateModularity.calculateModularity(g,bestNonOverlapPartition);
//        MyPrint.print("非重叠情况下，得到的划分结果的模块度："+mudularity);

//        rankNodeCommunities(overlapPartition);
        analysisSmallCommunity(bestNonOverlapPartition,g);
    }

    /**
     * 统计重叠社区结果中，重叠社区节点及其所属社区个数 的分布情况
     * @param overlapPartition
     */
    public static void rankNodeCommunities(OverlapPartition overlapPartition){
        Map<String,List<String>> nodeMapCommunities = overlapPartition.getNodeMapCommunities();
        List<Integer> communityNumOfNodeBelong = new ArrayList<Integer>();
        Map<String,Integer> nodeMapCommunitiesNum = new HashMap<String, Integer>();

        Iterator iter = nodeMapCommunities.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry<String,List<String>>)iter.next();
            List<String> communities = (List<String>)entry.getValue();
            int size = communities.size();
            communityNumOfNodeBelong.add(size);
            nodeMapCommunitiesNum.put((String)entry.getKey(),size);
        }
        FastSort.fastSort(communityNumOfNodeBelong,0,communityNumOfNodeBelong.size()-1);

        MyPrint.print("重叠节点个数："+communityNumOfNodeBelong.size());
        int length = communityNumOfNodeBelong.size();
        MyPrint.print("一个节点所属的最多社区个数"+communityNumOfNodeBelong.get(length-1));
        MyPrint.print("重叠社区节点---节点属于的社区个数排序： "+communityNumOfNodeBelong);
    }

    /**
     * 获取非重叠社区的size分布
     * @param partition
     */
    private void getNonOverlapCommunitySizeDistribution(Partition partition){
        List<Integer> communitySizeDistribution = new ArrayList<Integer>();
        Map<String,List<String>> communities = partition.getCommunities();
        Iterator iterator = communities.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry<String,List<String>>)iterator.next();
            int size = ((List<String>)entry.getValue()).size();

            communitySizeDistribution.add(size);
        }
        FastSort.fastSort(communitySizeDistribution,0,communitySizeDistribution.size()-1);
        MyPrint.print("非重叠社区size分布： "+communitySizeDistribution);

        String outputPath = "D:\\paperdata\\soybean\\community detection\\community analysis\\非重叠社区size分布.txt";
        try {
            CommunityAnalysisResultOutput.outputCommunitiesDistribution(communitySizeDistribution, outputPath);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 获取重叠社区的size分布图
     * @param overlapPartition
     */
    private void getCommunitySizeDistribution(OverlapPartition overlapPartition){
        List<Integer> communitySizeDistribution = new ArrayList<Integer>();
        Map<String,List<String>> communities = overlapPartition.getCommunities();
        Iterator iterator = communities.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry<String,List<String>>)iterator.next();
            int size = ((List<String>)entry.getValue()).size();

            communitySizeDistribution.add(size);
        }
        FastSort.fastSort(communitySizeDistribution,0,communitySizeDistribution.size()-1);
        MyPrint.print("重叠社区size分布： "+communitySizeDistribution);
        String outputPath = "D:\\paperdata\\soybean\\community detection\\community analysis\\重叠社区size分布.txt";
        try {
            CommunityAnalysisResultOutput.outputCommunitiesDistribution(communitySizeDistribution, outputPath);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void analysisSmallCommunity(Partition bestNonOverlapPartition ,Graph g){
        double min = 10000;
        double max = 0.0d;
        double sum = 0.0d;
        int numNode = 0;
        int numSmallCommunity = 0;
        List<Integer> degrees = new ArrayList<Integer>();
        Iterator iterator = bestNonOverlapPartition.getCommunities().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)iterator.next();
            if(entry.getValue().size() <=3){
                numSmallCommunity++;
                for(String v :entry.getValue()){
                    numNode++;
                    int degree = g.map.get(v).neighborList.size();
                    degrees.add(degree);
                    if(degree < min){
                        min = degree;
                    }
                    if(degree > max){
                        max = degree;
                    }
                    sum+=degree;
                }
            }
        }

        MyPrint.print("------------下面分析小社区内的节点度------------");
        MyPrint.print("社区size小于等于3的个数："+numSmallCommunity);
        MyPrint.print("最小度："+min);
        MyPrint.print("最大度："+max);
        MyPrint.print("平均度："+sum/numNode);
        StringBuffer sb = new StringBuffer();
        for(int d :degrees){
            sb.append(d+",");
        }
        MyPrint.print(sb.toString());
    }

}
