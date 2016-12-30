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
 * �������������㷨�� �Եõ����������������������
 */
public class Analysis {
    public static void main(String[] args){
        Analysis analysis = new Analysis();
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition)mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

        MyPrint.print("���ص����������� "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("�ص����������� "+overlapPartition.getCommunities().size());

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
        MyPrint.print("���ص�����size�ֲ��� "+communitySizeDistribution);
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
        MyPrint.print("�ص�����size�ֲ��� "+communitySizeDistribution);
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
        mySerialization.serializeOverlapResult(overlapPartition,"D:\\paperdata\\soybean\\community detection\\���ս��\\test.obj");

        OverlapPartition testObject = (OverlapPartition) mySerialization.antiSerializeOverlapPartition("D:\\paperdata\\soybean\\community detection\\���ս��\\test.obj");

        MyPrint.print("�����л��� "+testObject.getCommunities().get("first").get(0));
    }
}
