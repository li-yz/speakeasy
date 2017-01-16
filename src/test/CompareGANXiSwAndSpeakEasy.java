package test;

import postprocess.CalculateModularity;
import postprocess.NMI;
import serialprocess.Graph;
import serialprocess.Partition;
import utils.MyPrint;
import utils.MySerialization;

import java.util.List;
import java.util.Map;

/**
 * 选定某一数据集（例如karate数据集），对比GANXiSw与speakeasy的社区发现效果
 * Created by Liyanzhen on 2017/1/16.
 */
public class CompareGANXiSwAndSpeakEasy {
    public static void main(String[] args){

        compareModularity();
        getNMIOfTwoPartition();

    }

    //计算两种算法 得到的结果的模块度
    public static void compareModularity(){
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");

        Graph g = (Graph)mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        double mudularity = CalculateModularity.calculateModularity(g,bestNonOverlapPartition);
        MyPrint.print("karate数据集，非重叠情况下，speakeasy得到的划分结果的模块度："+mudularity);

        String GANXiSwResultPath = "C:\\Users\\Liyanzhen\\Desktop\\对比GANXiSw算法与 speakeasy算法\\GANXiSw跑karate数据集No2.txt";
        Partition GANXiSwPartition= AnalysisGANXiSwResult.readCommunities(GANXiSwResultPath);
        double GANXiSwModularity = CalculateModularity.calculateModularity(g,GANXiSwPartition);
        MyPrint.print("karate数据集，非重叠情况下，GANXiSw得到的划分结果的模块度："+GANXiSwModularity);
    }

    /**
     * 计算两个划分的NMI值，NMI值越高，两个划分越接近
     */
    public static void getNMIOfTwoPartition(){
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");

        Graph g = (Graph)mySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        int n=g.map.size();

        String GANXiSwResultPath = "C:\\Users\\Liyanzhen\\Desktop\\对比GANXiSw算法与 speakeasy算法\\GANXiSw跑karate数据集No2.txt";
        Partition GANXiSwPartition= AnalysisGANXiSwResult.readCommunities(GANXiSwResultPath);

        double nmi = NMI.getNMIValue(bestNonOverlapPartition,GANXiSwPartition,n);
        MyPrint.print("karate数据集，非重叠情况下，Speakeasy与GANXiSw得到的划分结果的nmi："+nmi);
    }
}
