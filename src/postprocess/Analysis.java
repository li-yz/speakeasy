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
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

        MyPrint.print("���ص����������� "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("�ص����������� "+overlapPartition.getCommunities().size());

        analysis.getNonOverlapCommunitySizeDistribution(bestNonOverlapPartition);
        analysis.getCommunitySizeDistribution(overlapPartition);

        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
//        double mudularity = CalculateModularity.calculateModularity(g,bestNonOverlapPartition);
//        MyPrint.print("���ص�����£��õ��Ļ��ֽ����ģ��ȣ�"+mudularity);

//        rankNodeCommunities(overlapPartition);
        analysisSmallCommunity(bestNonOverlapPartition,g);
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

    /**
     * ��ȡ���ص�������size�ֲ�
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
        MyPrint.print("���ص�����size�ֲ��� "+communitySizeDistribution);

        String outputPath = "D:\\paperdata\\soybean\\community detection\\community analysis\\���ص�����size�ֲ�.txt";
        try {
            CommunityAnalysisResultOutput.outputCommunitiesDistribution(communitySizeDistribution, outputPath);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ�ص�������size�ֲ�ͼ
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
        MyPrint.print("�ص�����size�ֲ��� "+communitySizeDistribution);
        String outputPath = "D:\\paperdata\\soybean\\community detection\\community analysis\\�ص�����size�ֲ�.txt";
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

        MyPrint.print("------------�������С�����ڵĽڵ��------------");
        MyPrint.print("����sizeС�ڵ���3�ĸ�����"+numSmallCommunity);
        MyPrint.print("��С�ȣ�"+min);
        MyPrint.print("���ȣ�"+max);
        MyPrint.print("ƽ���ȣ�"+sum/numNode);
        StringBuffer sb = new StringBuffer();
        for(int d :degrees){
            sb.append(d+",");
        }
        MyPrint.print(sb.toString());
    }

}
