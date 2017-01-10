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
 * �������������㷨�� �Եõ����������������������
 */
public class Analysis {
    public static void main(String[] args){
        Analysis analysis = new Analysis();
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition)mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

        MyPrint.print("���ص����������� "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("�ص����������� "+overlapPartition.getCommunities().size());

        analysis.getNonOverlapCommunitySizeDistribution(bestNonOverlapPartition);
        analysis.getCommunitySizeDistribution(overlapPartition);

//        Graph g = (Graph)mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
//        double mudularity = CalculateModularity.calculateModularity(g,bestNonOverlapPartition);
//        MyPrint.print("���ص�����£��õ��Ļ��ֽ����ģ��ȣ�"+mudularity);

        rankNodeCommunities(overlapPartition);
    }

    /**
     * ͳ���ص���������У��ص������ڵ㼰�������������� �ķֲ����
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

        MyPrint.print("�ص��ڵ������"+communityNumOfNodeBelong.size());
        int length = communityNumOfNodeBelong.size();
        MyPrint.print("һ���ڵ������������������"+communityNumOfNodeBelong.get(length-1));
        MyPrint.print("�ص������ڵ�---�ڵ����ڵ������������� "+communityNumOfNodeBelong);
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

        String outputPath = "D:\\paperdata\\soybean\\community detection\\community analysis\\���ص�����size�ֲ�.txt";
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
        MyPrint.print("�ص�����size�ֲ��� "+communitySizeDistribution);
        String outputPath = "D:\\paperdata\\soybean\\community detection\\community analysis\\�ص�����size�ֲ�.txt";
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
        mySerialization.serializeObject(overlapPartition,"D:\\paperdata\\soybean\\community detection\\���ս��\\test.obj");

        OverlapPartition testObject = (OverlapPartition) mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\test.obj");

        MyPrint.print("�����л��� "+testObject.getCommunities().get("first").get(0));
    }
}
