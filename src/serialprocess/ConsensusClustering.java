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
        List<Partition>partitionList = (List<Partition>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.3.9网络图G2\\partitionList.obj");
        List<String> allNodeList = (List<String>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.3.9网络图G2\\allNodeList.obj");
        List<String> allNodesOfBigCommunities = (List<String>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\allNodesInMeaningfulCom.obj");//毕业论文4.10改进2，新的输入节点列表

        //确定阈值r需要的节点平均权值Wv,c的分布
        double meanOfWvc = AnalysisRValue.readWvcDataAndReturnMean("D:\\paperdata\\soybean\\community detection\\筛选重叠节点Wv,c分布\\Wvc.txt");

        postProcessOfSpeakEasy(partitionList,allNodeList,allNodesOfBigCommunities,meanOfWvc);
    }

    public static void postProcessOfSpeakEasy(List<Partition>partitionList,List<String>allNodeList,List<String> allNodesOfBigCommunities, double meanOfWvc){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = sdf.format(date);

        System.out.println("一致化聚类开始");
        int n=allNodeList.size();

        //共生矩阵A的对象表示
        CooccurMatrix a=new CooccurMatrix();

        //节点：其所属的多个社区 ，保存找到的重叠社区节点结果
        Map<String, List<String>> nodeMapCommunities=new HashMap<String, List<String>>();

        Map<String, List<String>> bestPartitionCommunities;
        Map<String, String> bestPartitionNodeMapCommu;

        System.out.println("计算共生矩阵");
        //遍历所有的划分结果，计算共生矩阵
        for(int partiIndex=0;partiIndex < 10;partiIndex++){
            Partition partition=partitionList.get(partiIndex);

            for(int uIndex=0;uIndex <allNodeList.size();uIndex++){
                String unode=allNodeList.get(uIndex);
                for(int vIndex=0;vIndex <allNodeList.size();vIndex++){
                    if(uIndex == vIndex){//即共生矩阵对角线元素，元素值应该为0
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
        System.out.println("开始计算ARI矩阵");
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

        System.out.println("ARI值计算完毕！");

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
        System.out.println("最优聚类划分序号："+index);
        Partition bestPartition = partitionList.get(index);

        //将非重叠的 最优划分结果序列化保存
        MySerialization.serializeObject(bestPartition,"D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");

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

        double r=(double)1/maxCommuNum;//论文中作者提到 r可以这么设定，特别是在生物网络中
        r=0.2;

//        r = 3*meanOfWvc ;// 阈值r的值是可以适当调整的，r越大 得到的重叠节点就越少,取Wvc的均值

        MyPrint.print("筛选重叠社区节点的阈值r = "+r);

        //determine the overlapping nodes
        System.out.println("开始识别重叠社区节点");
        DetermineOverlapNodes.determine(bestPartitionCommunities,bestPartitionNodeMapCommu, allNodesOfBigCommunities, a, r, nodeMapCommunities);

        System.out.println("nodeMapCommunities的大小即重叠节点的个数："+nodeMapCommunities.size());

        //输出最终结果，包括社区划分、重叠节点集
        ResultOutput ro=new ResultOutput();
        try {
            System.out.println("print final result!!!");
            System.out.println("总的社区个数："+bestPartitionCommunities.size());
            System.out.println("重叠社区节点个数："+nodeMapCommunities.size());
            ro.outputCommunities(bestPartitionCommunities);
            ro.outputOverLapNodes(nodeMapCommunities);
            System.out.println("OK! finish!");
            date = new Date();
            String endDate = sdf.format(date);

            int processTime = (int)(sdf.parse(endDate).getTime() - sdf.parse(startDate).getTime())/(1000*60); //做差得出来的是毫秒，转成 分钟为单位
            System.out.println("总的执行时间 = "+processTime +" 分钟");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将重叠社区划分结果保存到OverlapPartition对象中，便于序列化，后续可以直接反序列化，针对社区发现结果进行分析
        OverlapPartition overlapPartition = new OverlapPartition();
        overlapPartition.setCommunities(bestPartitionCommunities);
        overlapPartition.setNodeMapCommunities(nodeMapCommunities);

        MySerialization.serializeObject(overlapPartition,"D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");
    }
}
