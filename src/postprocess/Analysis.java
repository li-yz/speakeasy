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
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition)mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

        MyPrint.print("非重叠社区个数： "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("重叠社区个数： "+overlapPartition.getCommunities().size());

        analysis.getNonOverlapCommunitySizeDistribution(bestNonOverlapPartition);
        analysis.getCommunitySizeDistribution(overlapPartition);

//        Graph g = (Graph)mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
//        double mudularity = CalculateModularity.calculateModularity(g,bestNonOverlapPartition);
//        MyPrint.print("非重叠情况下，得到的划分结果的模块度："+mudularity);

        rankNodeCommunities(overlapPartition);
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

    private void analysisSmallCommunity(){

    }

    private static void testSerialization(){
        OverlapPartition overlapPartition = new OverlapPartition();
        Map<String,List<String>> communities = new HashMap<String, List<String>>();
        List<String> list = new ArrayList<String>();
        list.add("first");
        communities.put("first",list);
        overlapPartition.setCommunities(communities);

        MySerialization mySerialization = new MySerialization();
        mySerialization.serializeObject(overlapPartition,"D:\\paperdata\\soybean\\community detection\\最终结果\\test.obj");

        OverlapPartition testObject = (OverlapPartition) mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\test.obj");

        MyPrint.print("反序列化： "+testObject.getCommunities().get("first").get(0));
    }
}
