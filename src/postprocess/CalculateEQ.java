package postprocess;

import serialprocess.Graph;
import serialprocess.OverlapPartition;
import serialprocess.Partition;
import serialprocess.VertexNode;
import utils.MyPrint;
import utils.MySerialization;

import java.util.*;

/**
 * EQ��ģ��ȵĸĽ��汾��ר�����������ص����� ��ģ����
 * Created by Liyanzhen on 2017/2/16.
 */
public class CalculateEQ {
    public static void main(String[] args){
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

//        double eq = getEQ(g,overlapPartition);
//        MyPrint.print("�����İ�EQ = "+eq);
//
        double eq2 = calculateEQOfShen(g,overlapPartition);
        MyPrint.print("shenԭ������ EQ = "+eq2);
    }

    public static double getEQ(Graph g, OverlapPartition overlapPartition){
        //
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");

        double q = 0.0d;
        int m = g.totalEdgesList.size();//����ͼg�е��ܱ��� ע�⣡����ע�⣡����ע�⣡����ע�⣡����ע�⣡����ע�⣡������������ͼԭʼ����ͼ�����ļ��� ͬһ���߱�ʾ�����Σ���Ҫ�� m ���Զ�������ʵ�ı���
        int n=0;//n��ʾ�ڵ�����

        //�������нڵ㣬����ѭ���������нڵ�
        List<String> allNodeList=new ArrayList<String>();

        n=g.map.size();
        Iterator iter=g.map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry=(Map.Entry)iter.next();
            String node=(String)entry.getKey();
            allNodeList.add(node);
        }
//        System.out.println("������ͼ�Ľڵ��������浽1��list�У�����������ڵ�����"+allNodeList.size());

        Map<String, List<String>> nodeMapCommunities = overlapPartition.getNodeMapCommunities();

        //����֮ǰ��Ƶ�OverlapPartition�����ݽṹ��̫����nodeMapCommunitiesֻ�������ص��ڵ㼰��������������û��ֱ�ӱ�����ص��ڵ㼰������������������ֱ���ж������ڵ� nodei �� nodej�Ƿ�����ͬһ��������
        //��� ��bestNonOverlapPartition֮���ұ���ķ��ص��ڵ�-�����������������ϲ���һ���Ľṹ���ϲ���allNodeMapCommunities֮�С�
        Map<String, List<String>> allNodeMapCommunities= mergeAndGetTotalNodemapCommunities(bestNonOverlapPartition,nodeMapCommunities);

        for(int i=0;i < n;i++){
            for(int j=i+1;j < n;j++){
                int Aij = 0;
                VertexNode nodei = g.map.get(allNodeList.get(i));
                VertexNode nodej = g.map.get(allNodeList.get(j));
                if(nodei.neighborList.contains(allNodeList.get(j)) || nodej.neighborList.contains(allNodeList.get(i))){
                    Aij=1;
                }
                int Ki=nodei.neighborList.size();//�ڵ�i�Ķ�
                int Kj=nodej.neighborList.size();//�ڵ�j�Ķ�

                //����ڵ�i��j����ͬһ�����������ģ���ֵ��һ������ֵ
                if(isInTheSameCommunity(allNodeMapCommunities.get(nodei.vertexName),allNodeMapCommunities.get(nodej.vertexName))){
                    int oi = allNodeMapCommunities.get(nodei.vertexName).size();//�ڵ�i������������
                    int oj = allNodeMapCommunities.get(nodej.vertexName).size();//�ڵ�j������������

                    double temp =  ((double)1/(oi*oj))*(Aij - ((double)(Ki*Kj)/(2*m)));
                    q += temp;
                }
            }
        }
        q= q/(2*m);

        return q;
    }
    private static boolean isInTheSameCommunity(List<String>ci,List<String>cj){
        boolean r = false;

        Set<String> result = new HashSet<String>();
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();

        for(String e:ci){
            set1.add(e);
        }
        for(String e :cj){
            set2.add(e);
        }

        result.clear();
        result.addAll(set1);
        result.retainAll(set2);
        if(result.size() > 0){
            r = true;
        }

        return r;
    }

    private static Map<String,List<String>> mergeAndGetTotalNodemapCommunities(Partition bestNonOverlapPartition, Map<String, List<String>> nodeMapCommunities) {
        Map<String,List<String>> totalNodeMapCommunities = new HashMap<String, List<String>>();
        totalNodeMapCommunities.putAll(nodeMapCommunities);
        Iterator iter = bestNonOverlapPartition.getNodeCommunityMap().entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)iter.next();
            String vertexName=entry.getKey();
            if(totalNodeMapCommunities.containsKey(vertexName)){//����ǰ�ڵ�vertexName���ص��ڵ�
                continue;
            }
            List<String> list = new ArrayList<String>();
            list.add(bestNonOverlapPartition.getNodeCommunityMap().get(vertexName));
            totalNodeMapCommunities.put(vertexName,list);
        }
        return totalNodeMapCommunities;
    }

    public static double calculateEQOfShen(Graph g, OverlapPartition overlapPartition){
        //
        double q = 0.0d;
        int m = g.totalEdgesList.size();//����ͼg�е��ܱ��� ע�⣡����ע�⣡����ע�⣡����ע�⣡����ע�⣡����ע�⣡������������ͼԭʼ����ͼ�����ļ��� ͬһ���߱�ʾ�����Σ���Ҫ�� m ���Զ�������ʵ�ı���
//        ���(ԭʼ����ͼ�ļ��� ͬһ������߱���ʾ��2��)��mҪ���Զ�
//        m=m/2;
        int n=0;//n��ʾ�ڵ�����

        Map<String, List<String>> nodeMapCommunities = overlapPartition.getNodeMapCommunities();

        Map<String,List<String>> communities = overlapPartition.getCommunities();
        Iterator iterator = communities.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String commuName =(String) entry.getKey();
            List<String> nodes = (List<String>) entry.getValue();
            for(int i=0;i < nodes.size();i++){
                for(int j=i+1;j <nodes.size();j++){
                    int Aij = 0;
                    VertexNode nodei = g.map.get(nodes.get(i));
                    VertexNode nodej = g.map.get(nodes.get(j));
                    if(nodei.neighborList.contains(nodes.get(j)) || nodej.neighborList.contains(nodes.get(i))){
                        Aij=1;//�ڵ�i��j֮���б�����
                    }
                    int Ki=nodei.neighborList.size();//�ڵ�i�Ķ�
                    int Kj=nodej.neighborList.size();//�ڵ�j�Ķ�

                    int oi=1;
                    int oj=1;
                    if(overlapPartition.getNodeMapCommunities().containsKey(nodei.vertexName)){
                        oi = overlapPartition.getNodeMapCommunities().get(nodei.vertexName).size();
                    }
                    if(overlapPartition.getNodeMapCommunities().containsKey(nodej.vertexName)){
                        oj = overlapPartition.getNodeMapCommunities().get(nodej.vertexName).size();
                    }

                    double temp = ((double)1/(oi*oj))*(Aij - (double)(Ki*Kj)/(2*m));
                    q+=temp;
                }
            }
        }

        q= q/(2*m);

        return q;
    }

}
