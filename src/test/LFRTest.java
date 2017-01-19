package test;

import postprocess.NMI;
import serialprocess.Graph;
import serialprocess.OverlapPartition;
import utils.MyOutPut;
import utils.MyPrint;
import utils.MySerialization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by Liyanzhen on 2017/1/11.
 */
public class LFRTest {
    public static void main(String[] args){
        String realCommunityPath = "D:\\paperdata\\test network\\ʹ��lfr���ɵ���������\\community.dat";
        OverlapPartition truthPartition = readAndOutPutRealCommunityFromTextFile(realCommunityPath);
        MySerialization mySerialization = new MySerialization();
        OverlapPartition speakEasyPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

        compareSpeakEasyResultWithTheTruthOfLFRNetwork(truthPartition,speakEasyPartition);

        //����һ��LFR���ɵ��������ʵ�����ṹ��ģ��ȣ���������������������

        //����speakeasy�Ļ��ֽ����standard��NMI
        int n=0;//�ܵĽڵ���
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        n=g.map.size();
        double nmi1 = NMI.getNMIValue(speakEasyPartition,truthPartition,n);
        MyPrint.print("speakeasy�������ʵ�����nmiֵ = "+nmi1);

        //����GANXiSw �Ļ��ֽ������ʵ�����ģ���
        String GANXisWCommunitiesPath = "E:\\��ҵ����\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\LFRnetwork\\SLPAw_LFRnetwork_run1_r0.05_v3_T50.icpm";
        String GANXiSwOverlapNodesPath = "E:\\��ҵ����\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\LFRnetwork\\SLPAw_LFRnetwork_run1_r0.05_v3_T50.icpm.ovnodes.txt";
//        Partition GANXiSwPartition = AnalysisGANXiSwResult.readCommunities(GANXisWCommunitiesPath);//���ص������

        OverlapPartition GANXiSwPartition = AnalysisGANXiSwResult.readOverlapPartition(GANXisWCommunitiesPath,GANXiSwOverlapNodesPath);
        double nmi2 = NMI.getNMIValue(GANXiSwPartition,truthPartition,n);
        MyPrint.print("GANXiSw�������ʵ�����nmiֵ = "+nmi2);
        compareSpeakEasyResultWithTheTruthOfLFRNetwork(truthPartition,GANXiSwPartition);
    }

    /**
     * ��ȡLFR benchmark�������ʵ��������
     * @param path
     * @return
     */
    public static OverlapPartition readAndOutPutRealCommunityFromTextFile(String path){
        OverlapPartition trueOverlapPartition = new OverlapPartition();
        Map<String,List<String>> communities=new HashMap<String, List<String>>();
        Map<String,List<String>> overlapNodeMapCommunities = new HashMap<String, List<String>>();
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line="";
            while((line=br.readLine()) != null){
                String[] array = line.split("\t");
                String vertexName = array[0];
                String [] commuArray = array[1].split(" ");
                //��ͳ���������ֽ��
                for(int i=0;i < commuArray.length;i++){
                    String communityName = commuArray[i];
                    if(communities.containsKey(communityName)){
                        communities.get(communityName).add(vertexName);
                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(vertexName);
                        communities.put(communityName,list);
                    }
                }

                //ͳ���ص������ڵ�
                if(commuArray.length > 1){//˵����ǰ�ڵ����ص������ڵ�
                    List<String> list = new ArrayList<String>();
                    for(int i=0;i < commuArray.length;i++){
                        list.add(commuArray[i]);
                    }
                    overlapNodeMapCommunities.put(vertexName,list);
                }
            }

            br.close();
            fr.close();

            trueOverlapPartition.setCommunities(communities);
            trueOverlapPartition.setNodeMapCommunities(overlapNodeMapCommunities);

            //�����ʵ�������ֽ�����ص������ڵ�
            MyOutPut.outputCommunities(communities);
            MyOutPut.outputOverLapNodes(overlapNodeMapCommunities);
        }catch (Exception e){
            e.printStackTrace();
        }
        return trueOverlapPartition;
    }

    private static void compareSpeakEasyResultWithTheTruthOfLFRNetwork(OverlapPartition truth ,OverlapPartition speakEasyPartition){
        //�ȱȽ�speakeasy�ҵ����ص��ڵ㸲����
        Set<String> truthOverlapNodes = new HashSet<String>();
        Iterator truthIter = truth.getNodeMapCommunities().entrySet().iterator();
        while(truthIter.hasNext()){
            Map.Entry entry = (Map.Entry<String,List<String>>) truthIter.next();
            truthOverlapNodes.add((String)entry.getKey());
        }

        Set<String> speakeasyOverlapNodes = new HashSet<String>();
        Iterator speakeasyIter = speakEasyPartition.getNodeMapCommunities().entrySet().iterator();
        while(speakeasyIter.hasNext()){
            Map.Entry entry = (Map.Entry<String,List<String>>) speakeasyIter.next();
            speakeasyOverlapNodes.add((String)entry.getKey());
        }

        int rnum = 0;
        for(String v :truthOverlapNodes){
            if(speakeasyOverlapNodes.contains(v)){
                rnum++;
            }
        }
        MyPrint.print("��ʵ���ص��ڵ���� = "+truthOverlapNodes.size());
        MyPrint.print("speakeasy�õ����ص��ڵ���� = "+speakeasyOverlapNodes.size());
        MyPrint.print("speakeasy�㷨���ǵ�����ʵ�ص��ڵ���� = "+rnum +" ,������ = "+(double)17/20);
        MyPrint.print("speakeasy�ҵ��ķ���ʵ���ص��ڵ���� = "+(speakeasyOverlapNodes.size()-rnum));
        //�ص��ڵ�Ƚ� ��

        //�������� ��һ������Ϣָ�����Ƚ�speakeasy�õ��Ļ��ֽ������ʵ���ֽ��


    }
}
