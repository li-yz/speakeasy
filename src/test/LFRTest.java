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
        String realCommunityPath = "D:\\paperdata\\test network\\使用lfr生成的网络数据\\community.dat";
        OverlapPartition truthPartition = readAndOutPutRealCommunityFromTextFile(realCommunityPath);
        MySerialization mySerialization = new MySerialization();
        OverlapPartition speakEasyPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

        compareSpeakEasyResultWithTheTruthOfLFRNetwork(truthPartition,speakEasyPartition);

        //计算一下LFR生成的网络的真实社区结构的模块度！！！！！？？？？？？

        //计算speakeasy的划分结果与standard的NMI
        int n=0;//总的节点数
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        n=g.map.size();
        double nmi1 = NMI.getNMIValue(speakEasyPartition,truthPartition,n);
        MyPrint.print("speakeasy结果与真实结果的nmi值 = "+nmi1);

        //计算GANXiSw 的划分结果与真实结果的模块度
        String GANXisWCommunitiesPath = "E:\\毕业论文\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\LFRnetwork\\SLPAw_LFRnetwork_run1_r0.05_v3_T50.icpm";
        String GANXiSwOverlapNodesPath = "E:\\毕业论文\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\LFRnetwork\\SLPAw_LFRnetwork_run1_r0.05_v3_T50.icpm.ovnodes.txt";
//        Partition GANXiSwPartition = AnalysisGANXiSwResult.readCommunities(GANXisWCommunitiesPath);//非重叠的情况

        OverlapPartition GANXiSwPartition = AnalysisGANXiSwResult.readOverlapPartition(GANXisWCommunitiesPath,GANXiSwOverlapNodesPath);
        double nmi2 = NMI.getNMIValue(GANXiSwPartition,truthPartition,n);
        MyPrint.print("GANXiSw结果与真实结果的nmi值 = "+nmi2);
        compareSpeakEasyResultWithTheTruthOfLFRNetwork(truthPartition,GANXiSwPartition);
    }

    /**
     * 读取LFR benchmark网络的真实社区划分
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
                //先统计社区划分结果
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

                //统计重叠社区节点
                if(commuArray.length > 1){//说明当前节点是重叠社区节点
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

            //输出真实社区划分结果、重叠社区节点
            MyOutPut.outputCommunities(communities);
            MyOutPut.outputOverLapNodes(overlapNodeMapCommunities);
        }catch (Exception e){
            e.printStackTrace();
        }
        return trueOverlapPartition;
    }

    private static void compareSpeakEasyResultWithTheTruthOfLFRNetwork(OverlapPartition truth ,OverlapPartition speakEasyPartition){
        //先比较speakeasy找到的重叠节点覆盖率
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
        MyPrint.print("真实的重叠节点个数 = "+truthOverlapNodes.size());
        MyPrint.print("speakeasy得到的重叠节点个数 = "+speakeasyOverlapNodes.size());
        MyPrint.print("speakeasy算法覆盖到的真实重叠节点个数 = "+rnum +" ,覆盖率 = "+(double)17/20);
        MyPrint.print("speakeasy找到的非真实的重叠节点个数 = "+(speakeasyOverlapNodes.size()-rnum));
        //重叠节点比较 完

        //下面利用 归一化互信息指标来比较speakeasy得到的划分结果与真实划分结果


    }
}
