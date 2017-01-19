package test;

import postprocess.CalculateModularity;
import postprocess.NMI;
import serialprocess.Graph;
import serialprocess.Partition;
import utils.MyPrint;
import utils.MySerialization;

/**
 * ѡ��ĳһ���ݼ�������karate���ݼ������Ա�GANXiSw��speakeasy����������Ч��
 * Created by Liyanzhen on 2017/1/16.
 */
public class CompareGANXiSwAndSpeakEasy {
    public static void main(String[] args){

        compareModularity();
        getNMIOfTwoPartition();

    }

    //���������㷨 �õ��Ľ����ģ���
    public static void compareModularity(){
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");

        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        double mudularity = CalculateModularity.calculateModularity(g,bestNonOverlapPartition);
        MyPrint.print("karate���ݼ������ص�����£�speakeasy�õ��Ļ��ֽ����ģ��ȣ�"+mudularity);

        String GANXiSwResultPath = "C:\\Users\\Liyanzhen\\Desktop\\�Ա�GANXiSw�㷨�� speakeasy�㷨\\GANXiSw��karate���ݼ�No2.txt";
        Partition GANXiSwPartition= AnalysisGANXiSwResult.readCommunities(GANXiSwResultPath);
        double GANXiSwModularity = CalculateModularity.calculateModularity(g,GANXiSwPartition);
        MyPrint.print("karate���ݼ������ص�����£�GANXiSw�õ��Ļ��ֽ����ģ��ȣ�"+GANXiSwModularity);
    }

    /**
     * �����������ֵ�NMIֵ��NMIֵԽ�ߣ���������Խ�ӽ�
     */
    public static void getNMIOfTwoPartition(){
        MySerialization mySerialization = new MySerialization();
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");

        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        int n=g.map.size();

        String GANXiSwResultPath = "C:\\Users\\Liyanzhen\\Desktop\\�Ա�GANXiSw�㷨�� speakeasy�㷨\\GANXiSw��karate���ݼ�No2.txt";
        Partition GANXiSwPartition= AnalysisGANXiSwResult.readCommunities(GANXiSwResultPath);

        double nmi = NMI.getNMIValue(bestNonOverlapPartition,GANXiSwPartition,n);
        MyPrint.print("karate���ݼ������ص�����£�Speakeasy��GANXiSw�õ��Ļ��ֽ����nmi��"+nmi);
    }
}
