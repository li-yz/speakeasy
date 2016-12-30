package postprocess;

import serialprocess.OverlapPartition;
import serialprocess.Partition;
import utils.FastSort;
import utils.MyPrint;
import utils.MySerialization;

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
        Partition bestNonOverlapPartition = (Partition) mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition)mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

        MyPrint.print("非重叠社区个数： "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("重叠社区个数： "+overlapPartition.getCommunities().size());

        analysis.getNonOverlapCommunitySizeDistribution(bestNonOverlapPartition);
        analysis.getCommunitySizeDistribution(overlapPartition);

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
        mySerialization.serializeOverlapResult(overlapPartition,"D:\\paperdata\\soybean\\community detection\\最终结果\\test.obj");

        OverlapPartition testObject = (OverlapPartition) mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\最终结果\\test.obj");

        MyPrint.print("反序列化： "+testObject.getCommunities().get("first").get(0));
    }
}
