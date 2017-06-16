package postprocess;

import serialprocess.Graph;
import serialprocess.Partition;
import serialprocess.VertexNode;
import utils.MyPrint;
import utils.MySerialization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ����ģ���
 *
 * Created by Liyanzhen on 2017/1/10.
 */
public class CalculateModularity {



    public static double calculateModularity(Graph g, Partition partition){
        double q = 0.0d;
        int m = g.totalEdgesList.size();//����ͼg�е��ܱ���
        //        ���(ԭʼ����ͼ�ļ��� ͬһ������߱���ʾ��2��)��mҪ���Զ�
        //m=m/2;

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

        Map<String, String> nodeCommunityMap = partition.getNodeCommunityMap();

        for(int i=0;i < n;i++){
            for(int j=i+1;j < n;j++){
                int Aij = 0;
                VertexNode nodei = g.map.get(allNodeList.get(i));
                VertexNode nodej = g.map.get(allNodeList.get(j));
                if(nodei.neighborList.containsKey(allNodeList.get(j)) ){
                    Aij=1;
                }
                int Ki=nodei.neighborList.size();//�ڵ�i�Ķ�
                int Kj=nodej.neighborList.size();//�ڵ�j�Ķ�

                //����ڵ�i��j����ͬһ�����������ģ���ֵ��һ������ֵ
                if(nodeCommunityMap.get(nodei.vertexName).equals(nodeCommunityMap.get(nodej.vertexName))){
                    double temp = Aij - ((double)(Ki*Kj)/(2*m));
                    q += temp;
                }
            }
        }
        q= q/(2*m);

        return q;
    }
}
