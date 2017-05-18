package serialprocess;

import postprocess.AnalysisRValue;
import utils.MyPrint;
import utils.MySerialization;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2017/1/18.
 */
public class ConsensusClustering {
    public static void main(String[] args){
        List<Partition>partitionList = (List<Partition>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.3.9����ͼG2\\partitionList.obj");
        List<String> allNodeList = (List<String>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.3.9����ͼG2\\allNodeList.obj");
        List<String> allNodesOfBigCommunities = (List<String>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\allNodesInMeaningfulCom.obj");//��ҵ����4.10�Ľ�2���µ�����ڵ��б�

        //ȷ����ֵr��Ҫ�Ľڵ�ƽ��ȨֵWv,c�ķֲ�
        double meanOfWvc = AnalysisRValue.readWvcDataAndReturnMean("D:\\paperdata\\soybean\\community detection\\ɸѡ�ص��ڵ�Wv,c�ֲ�\\Wvc.txt");

        postProcessOfSpeakEasy(partitionList,allNodeList,allNodesOfBigCommunities,meanOfWvc);
    }

    public static void postProcessOfSpeakEasy(List<Partition>partitionList,List<String>allNodeList,List<String> allNodesOfBigCommunities, double meanOfWvc){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = sdf.format(date);

        System.out.println("һ�»����࿪ʼ");
        int n=allNodeList.size();

        //��������A�Ķ����ʾ
        CooccurMatrix a=new CooccurMatrix();

        //�ڵ㣺�������Ķ������ �������ҵ����ص������ڵ���
        Map<String, List<String>> nodeMapCommunities=new HashMap<String, List<String>>();

        Map<String, List<String>> bestPartitionCommunities;
        Map<String, String> bestPartitionNodeMapCommu;

        System.out.println("���㹲������");
        //�������еĻ��ֽ�������㹲������
        for(int partiIndex=0;partiIndex < 10;partiIndex++){
            Partition partition=partitionList.get(partiIndex);

            for(int uIndex=0;uIndex <allNodeList.size();uIndex++){
                String unode=allNodeList.get(uIndex);
                for(int vIndex=0;vIndex <allNodeList.size();vIndex++){
                    if(uIndex == vIndex){//����������Խ���Ԫ�أ�Ԫ��ֵӦ��Ϊ0
                        continue;
                    }
                    String vnode=allNodeList.get(vIndex);

                    //if judge whether u and v belongs to the same community
                    if(partition.nodeCommunityMap.get(unode) != null &&partition.nodeCommunityMap.get(vnode) != null && partition.nodeCommunityMap.get(unode).equals(partition.nodeCommunityMap.get(vnode))){
                        a.addMatrixValue(unode, vnode, uIndex, vIndex);
                    }//if
                }//inner for
            }//outter for,co-occurrence matrix filled

        }//for 10 partition

        //make Co-occur matrix A symmetric
//        a.symmetricMatrix();


        //calculate the ARI value between the every 2 of 10 partitions
        //then using the ARI matrix to determine the final representative partition
        System.out.println("��ʼ����ARI����");
        AdjustRandIndex myari=new AdjustRandIndex(10);
        for(int i=0;i < partitionList.size();i++){
            for(int j=i;j < partitionList.size();j++){
                //call the method
                myari.calcuARI(partitionList.get(i).communities, partitionList.get(j).communities, i,j, n);
            }
        }
        //symmetric the ARI matrix
        for(int i=0;i<10;i++){
            for(int j=0;j < 10;j++){
                if(i !=j)
                    myari.R[j][i]=myari.R[i][j];
            }
        }

        System.out.println("ARIֵ������ϣ�");

        // using the ARI matrix determine the final representative partition
        double[] RrowSumAv=new double[10];
        for(int i=0;i<10;i++){
            double sum=0;
            for(int j=0;j < 10;j++){
                sum+=myari.R[i][j];
            }
            RrowSumAv[i]=sum/10;
        }
        int index=0;
        double temp=0;
        for(int i=0;i < 10;i++){
            if(temp < RrowSumAv[i]){
                temp=RrowSumAv[i];
                index=i;
            }
        }
        System.out.println("���ž��໮����ţ�"+index);
        Partition bestPartition = partitionList.get(index);

        //�����ص��� ���Ż��ֽ�����л�����
        MySerialization.serializeObject(bestPartition,"D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");

        bestPartitionCommunities=partitionList.get(index).communities;
        bestPartitionNodeMapCommu=partitionList.get(index).nodeCommunityMap;
        // select the max number of communities from all partitions,get r value
        int maxCommuNum=0;
        int tsize=0;

        for(Partition cc :partitionList){
            tsize=cc.communities.size();
            if(tsize > maxCommuNum)
                maxCommuNum=tsize;
        }

        double r=(double)1/maxCommuNum;//�����������ᵽ r������ô�趨���ر���������������
        r=0.2;

//        r = 3*meanOfWvc ;// ��ֵr��ֵ�ǿ����ʵ������ģ�rԽ�� �õ����ص��ڵ��Խ��,ȡWvc�ľ�ֵ

        MyPrint.print("ɸѡ�ص������ڵ����ֵr = "+r);

        //determine the overlapping nodes
        System.out.println("��ʼʶ���ص������ڵ�");
        DetermineOverlapNodes.determine(bestPartitionCommunities,bestPartitionNodeMapCommu, allNodesOfBigCommunities, a, r, nodeMapCommunities);

        System.out.println("nodeMapCommunities�Ĵ�С���ص��ڵ�ĸ�����"+nodeMapCommunities.size());

        //������ս���������������֡��ص��ڵ㼯
        ResultOutput ro=new ResultOutput();
        try {
            System.out.println("print final result!!!");
            System.out.println("�ܵ�����������"+bestPartitionCommunities.size());
            System.out.println("�ص������ڵ������"+nodeMapCommunities.size());
            ro.outputCommunities(bestPartitionCommunities);
            ro.outputOverLapNodes(nodeMapCommunities);
            System.out.println("OK! finish!");
            date = new Date();
            String endDate = sdf.format(date);

            int processTime = (int)(sdf.parse(endDate).getTime() - sdf.parse(startDate).getTime())/(1000*60); //����ó������Ǻ��룬ת�� ����Ϊ��λ
            System.out.println("�ܵ�ִ��ʱ�� = "+processTime +" ����");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //���ص��������ֽ�����浽OverlapPartition�����У��������л�����������ֱ�ӷ����л�������������ֽ�����з���
        OverlapPartition overlapPartition = new OverlapPartition();
        overlapPartition.setCommunities(bestPartitionCommunities);
        overlapPartition.setNodeMapCommunities(nodeMapCommunities);

        MySerialization.serializeObject(overlapPartition,"D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");
    }
}
