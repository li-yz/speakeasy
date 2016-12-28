package postprocess;

import serialprocess.OverlapPartition;
import serialprocess.Partition;
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
        testSerialization();
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition)mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");



    }

    private void getCommunitySizeDistribute(OverlapPartition overlapPartition){
        List<Integer> communitySizeDistribute = new ArrayList<Integer>();
        Map<String,List<String>> communities = overlapPartition.getCommunities();
        Iterator iterator = communities.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry<String,List<String>>)iterator.next();
            int size = ((List<String>)entry.getValue()).size();

            communitySizeDistribute.add(size);
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
        mySerialization.serializeOverlapResult(overlapPartition,"D:\\paperdata\\soybean\\community detection\\最终结果\\test.obj");

        OverlapPartition testObject = (OverlapPartition) mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\最终结果\\test.obj");

        MyPrint.print("反序列化： "+testObject.getCommunities().get("first").get(0));
    }
}
