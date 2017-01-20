package test;

import serialprocess.OverlapPartition;
import serialprocess.Partition;
import utils.MyPrint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用SLPA算法的实现jar包来处理大豆差异表达基因网络
 * 分析其社区分布情况
 * Created by Liyanzhen on 2017/1/13.
 */
public class AnalysisGANXiSwResult {

    public static void main(String []args){
        String GANXisWCommunitiesPath = "E:\\毕业论文\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\soybean\\SLPAw_soybean-network_run1_r0.01_v3_T50.icpm";
        Partition GANXiSwPartition = readCommunities(GANXisWCommunitiesPath);

    }

    /**
     *
     * @param path GANXiSw的非重叠社区划分结果 路径
     * @return
     */
    public static Partition readCommunities(String path){
        Partition GANXiSwPartition = new Partition();
        Map<String,List<String>> communities = new HashMap<String, List<String>>();
        Map<String,String> nodeMapCommunity = new HashMap<String, String>();

        try{
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line="";
            int lineIndex=0;
            StringBuffer sb = new StringBuffer();
            while((line = br.readLine()) != null){
                String[] array = line.split(" ");
                List<String> list = new ArrayList<String>();
                for(String v:array){
                    list.add(v);
                    nodeMapCommunity.put(v,lineIndex+"");
                }
                communities.put(lineIndex+"",list);
                sb.append(list.size()+" ");
                lineIndex++;
            }

            br.close();
            fr.close();

//            MyPrint.print("GANXiSw处理大豆基因网络得到的社区size分布："+sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        GANXiSwPartition.setNodeCommunityMap(nodeMapCommunity);
        GANXiSwPartition.setCommunities(communities);
        return GANXiSwPartition;
    }

    /**
     * 读取 GANXiSw的重叠社区划分结果
     * @param communityPath
     * @param overlapNodesPath
     * @return OverlapPartition对象
     */
    public static OverlapPartition readOverlapPartition(String communityPath,String overlapNodesPath){
        OverlapPartition GANXiSwPartition = new OverlapPartition();
        Map<String,List<String>> communities = new HashMap<String, List<String>>();
        Map<String,List<String>> nodeMapCommunities = new HashMap<String, List<String>>();

        try{
            File communityFile = new File(communityPath);
            FileReader fr = new FileReader(communityFile);
            BufferedReader br = new BufferedReader(fr);

            String line="";
            int lineIndex=0;
            StringBuffer sb = new StringBuffer();
            while((line = br.readLine()) != null){
                String[] array = line.split(" ");
                List<String> list = new ArrayList<String>();
                for(String v:array){
                    list.add(v);
                }
                communities.put(lineIndex+"",list);
                sb.append(list.size()+" ");
                lineIndex++;
            }

            br.close();
            fr.close();

            ///开始读取重叠节点
            File overlapNodesFile = new File(overlapNodesPath);
            FileReader fr2 = new FileReader(overlapNodesFile);
            BufferedReader br2 = new BufferedReader(fr2);
            String row ="";
            while((row=br2.readLine()) != null){
                String[] arr = row.split(" ");
                String vertexName = arr[0];
                List<String> list = new ArrayList<String>();
                String[] arr1 = arr[1].split("->");
                list.add(arr1[1]);
                for(int i=2;i < arr.length;i++){
                    list.add(arr[i]);
                }
                nodeMapCommunities.put(vertexName,list);
            }
            br2.close();
            fr2.close();


//            MyPrint.print("GANXiSw处理大豆基因网络得到的社区size分布："+sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        GANXiSwPartition.setCommunities(communities);
        GANXiSwPartition.setNodeMapCommunities(nodeMapCommunities);
        return GANXiSwPartition;
    }
}
