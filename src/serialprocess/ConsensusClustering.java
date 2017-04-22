package serialprocess;

import postprocess.AnalysisRValue;
import utils.MyPrint;
import utils.MySerialization;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Liyanzhen on 2017/1/18.
 */
public class ConsensusClustering {
    public static void main(String[] args){
        List<Partition>partitionList = (List<Partition>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.3.9����ͼG2\\partitionList.obj");
        List<String> allNodeList = (List<String>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.3.9����ͼG2\\allNodeList.obj");
        Graph g = (Graph)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.3.9����ͼG2\\graph.obj");
        MyPrint.print("����ͼg��"+g.map.size());
//        List<String> allNodesOfBigCommunities = (List<String>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\allNodesInMeaningfulCom.obj");//��ҵ����4.10�Ľ�2���µ�����ڵ��б�

        //ȷ����ֵr��Ҫ�Ľڵ�ƽ��ȨֵWv,c�ķֲ�
        double meanOfWvc = AnalysisRValue.readWvcDataAndReturnMean("D:\\paperdata\\soybean\\community detection\\ɸѡ�ص��ڵ�Wv,c�ֲ�\\Wvc.txt");

        postProcessOfSpeakEasy(partitionList,allNodeList,allNodeList,meanOfWvc);
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
                for(int vIndex=uIndex+1;vIndex <allNodeList.size();vIndex++){
                    String vnode=allNodeList.get(vIndex);

                    //if judge whether u and v belongs to the same community
                    if(partition.nodeCommunityMap.get(unode) != null &&partition.nodeCommunityMap.get(vnode) != null && partition.nodeCommunityMap.get(unode).equals(partition.nodeCommunityMap.get(vnode))){
                        a.addMatrixValue(unode, vnode, uIndex, vIndex);
                    }//if
                }//inner for
            }//outter for,co-occurrence matrix filled

        }//for 10 partition

        //make Co-occur matrix A symmetric
        a.symmetricMatrix();


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

        //���ϲ������Ĺ����ŵ�ǰ�棬�����¸Ľ�1,2017.4.13
        mergeCommunities(bestPartition,a,allNodeList);
        //���ϲ������Ĺ����ŵ�ǰ�棬�����ϸĽ�1,2017.4.13

        bestPartitionCommunities=bestPartition.communities;
        bestPartitionNodeMapCommu=bestPartition.nodeCommunityMap;
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

    private static void mergeCommunities(Partition bestPartition,CooccurMatrix a,List<String>allNodesList){
        double r = 0.6d;
        Map<String,List<String>> communities = bestPartition.getCommunities();
        List<String> comNames = new ArrayList<String>();
        comNames.addAll(communities.keySet());
        double[][] B=new double[comNames.size()][comNames.size()];
        Map<String,Integer> comNameMapIndex = new HashMap<String, Integer>();
        Map<Integer,String> comIndexMapName = new HashMap<Integer, String>();
        for(int i=0;i < comNames.size();i++){
            comNameMapIndex.put(comNames.get(i),i);
            comIndexMapName.put(i,comNames.get(i));
        }
        for(int i=0;i < comNames.size();i++){
            String iName = comNames.get(i);
            List<String> nodesInCi = communities.get(iName);
            for(int j=0;j < comNames.size();j++){
                if(j == i)
                    continue;
                String jName = comNames.get(j);
                List<String> nodesInCj = communities.get(jName);
                double Bij = 0.0d;
                for(int u=0;u < nodesInCi.size();u++){
                    for(int v=0;v < nodesInCj.size();v++){
                        String uNode = nodesInCi.get(u);
                        String vNode = nodesInCj.get(v);

                        if(a.matrix.containsKey(uNode) && a.matrix.get(uNode).containsKey(vNode)) {
                            int t = a.matrix.get(uNode).get(vNode);
                            if(a.matrix.get(vNode).get(uNode) != t){
                                MyPrint.print("******------��������A FALSE");
                            }
                            Bij += t;
                        }
                    }
                }
                B[i][j] = Bij;
            }
            //�������B�Խ���Ԫ��
            double Bii = 0.0d;
            for(int u=0;u < nodesInCi.size();u++){
                for(int k=0;k < allNodesList.size();k++){
                    String uNode = nodesInCi.get(u);
                    String kNode = allNodesList.get(k);
                    if(!uNode.equals(kNode)){
                        if(a.matrix.containsKey(uNode) && a.matrix.get(uNode).containsKey(kNode)) {
                            Bii += a.matrix.get(uNode).get(kNode);
                        }
                    }
                }
            }
            B[i][i]=Bii;
            //����B�Խ���Ԫ�ؼ������
        }

        //��������������ϣ����Ƿ�ϲ�
        fixBug(allNodesList,B,comNames,bestPartition);

        //����ϣ���������ǡ�С�������������������ϲ������ϲ���ʱ���Ƴ�С����������ڵ㲢�������
        //�п��ܳ����������������һ����С������Cj���ܻᱻ����������������ϲ������˴����ǲ���ϣ�������ص������
        // ������жϼ���Ĺ����У���С����Cj����������Ci���ߵĳ̶ȳ�����ֵr�����¼��������������������Ci�������ѡ���̶߳�����Ci������Cj���̶߳�����Ci���ϲ�Cj
        Map<String,Map<String,Double>> cooccurMap = new HashMap<String, Map<String, Double>>();
        for(int i=0;i < comNames.size();i++){
            for(int j= 0;j < comNames.size();j++){
                if(i==j)
                    continue;
                if(B[j][j]!=0 && B[i][j]/B[j][j] > r){
                    //�ϲ�����i������j
                    String iName = comIndexMapName.get(i);
                    String jName = comIndexMapName.get(j);
                    if(!cooccurMap.containsKey(jName)) {
                        Map<String, Double> map = new HashMap<String, Double>();
                        map.put(iName, B[i][j]);
                        cooccurMap.put(jName, map);
                    }else {
                        cooccurMap.get(jName).put(iName,B[i][j]);
                    }
                }
            }
        }
        MyPrint.print("�ܹ� "+comNames.size()+" ������");
        MyPrint.print("-*-*-*-*-*-*-*-���㱻�ϲ������������� "+cooccurMap.size()+" ��");

    }

    private static void fixBug(List<String>allNodesList, double[][]B,List<String>comNames,Partition partition){
        Set<String> nodesSet = new HashSet<String>();
        nodesSet.addAll(allNodesList);
        MyPrint.print("�ж��Ƿ��������Ļ���allNodesList.size = "+allNodesList.size()+" ��nodesSet.size = "+nodesSet.size());

        //��������˵����B�Խ����ϵ�ֵӦ�����������к��е����ֵ
        int n = B[0].length;
        int num=0;
        int numCol=0;
        for (int i=0;i < n;i++){
            double Bii = B[i][i];
            for(int j=0;j < n;j++){
                if(B[i][j] > Bii && Bii > 0){
                    num++;
                }
            }
        }
        for(int i=0;i < n;i++){
            for(int j=0;j < n;j++){
                if(j==i)
                    continue;
                if(B[j][i] > B[i][i]){
                    numCol++;
                }
            }
        }
        MyPrint.print("*****------���д��ڶԽ���Ԫ��ֵ���� "+num+" ��");
        MyPrint.print("*****------���д��ڶԽ���Ԫ��ֵ���� "+numCol+" ��");
    }
}
