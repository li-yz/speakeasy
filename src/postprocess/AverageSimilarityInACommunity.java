package postprocess;

import serialprocess.BasePartition;
import serialprocess.OverlapPartition;
import serialprocess.Partition;
import utils.MyPrint;
import utils.MySerialization;

import java.util.*;

/**
 * Created by Liyanzhen on 2017/3/3.
 */
public class AverageSimilarityInACommunity {
    public static void main(String[] args){
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

        double[][]differExpArrayData = (double[][])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\differExpressionGenes\\differExpArrayData.obj");
        String[] rowGeneIds = (String[])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\differExpressionGenes\\rowGeneIds.obj");

        averageSimilarity(bestNonOverlapPartition,differExpArrayData,rowGeneIds);
    }

    public static void averageSimilarity(BasePartition partition,double[][]differExpArrayData,String[] rowGeneIds){
        //第一步：先建立 “基因ID----基因在differExpArrayData行序号”对应关系
        Map<String,Integer> nameMapRowIndex = new HashMap<String, Integer>();
        for(int i=0;i < rowGeneIds.length;i++){
            nameMapRowIndex.put(rowGeneIds[i],i);
        }

        double [] averageSimilarities = new double[partition.getCommunities().size()];
        int columNum = differExpArrayData[0].length;
        //第二步：计算每一个社区内的基因之间的相关系数的均值
        double Sij = 0.0d;
        Map<String,List<String>> communities = partition.getCommunities();
        Iterator iterator = communities.entrySet().iterator();
        int c = 0;
        while (iterator.hasNext()){
            List<Double>similarities = new ArrayList<Double>();
            Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)iterator.next();
            List<String> nodes = entry.getValue();
            if(nodes.size() < 2){
                continue;
            }
            for(int m=0;m < nodes.size();m++){
                String nodeI = nodes.get(m);
                for(int n=m+1;n < nodes.size();n++){
                    String nodeJ = nodes.get(n);
                    int i = nameMapRowIndex.get(nodeI);
                    int j = nameMapRowIndex.get(nodeJ);
                    double xySum = 0.0d;
                    double xSum=0.0d;
                    double ySum=0.0d;
                    double x2Sum=0.0d;
                    double y2Sum=0.0d;
                    for(int k=0;k < columNum;k++){
                        xySum+=differExpArrayData[i][k]*differExpArrayData[j][k];
                        xSum+=differExpArrayData[i][k];
                        ySum+=differExpArrayData[j][k];
                        x2Sum+=differExpArrayData[i][k]*differExpArrayData[i][k];
                        y2Sum+=differExpArrayData[j][k]*differExpArrayData[j][k];
                    }
                    double fenzi = xySum-(xSum*ySum)/columNum;
                    double fenmu = Math.sqrt((x2Sum - (xSum*xSum)/columNum)*(y2Sum - (ySum*ySum)/columNum));
                    Sij = fenzi/fenmu;
                    similarities.add(Sij);
                }
            }

            int count=0;
            double sum = 0.0d;
            for(Double e :similarities){
                sum+=e;
                count++;
            }
            averageSimilarities[c]= sum/count;
            MyPrint.print("社区大小="+nodes.size()+"     :"+averageSimilarities[c]+"");
            c++;
        }

        MyPrint.print("各社区内相似度均值："+averageSimilarities);
    }
}
