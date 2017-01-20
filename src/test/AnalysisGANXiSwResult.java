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
 * ʹ��SLPA�㷨��ʵ��jar��������󶹲������������
 * �����������ֲ����
 * Created by Liyanzhen on 2017/1/13.
 */
public class AnalysisGANXiSwResult {

    public static void main(String []args){
        String GANXisWCommunitiesPath = "E:\\��ҵ����\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\soybean\\SLPAw_soybean-network_run1_r0.01_v3_T50.icpm";
        Partition GANXiSwPartition = readCommunities(GANXisWCommunitiesPath);

    }

    /**
     *
     * @param path GANXiSw�ķ��ص��������ֽ�� ·��
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

//            MyPrint.print("GANXiSw����󶹻�������õ�������size�ֲ���"+sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        GANXiSwPartition.setNodeCommunityMap(nodeMapCommunity);
        GANXiSwPartition.setCommunities(communities);
        return GANXiSwPartition;
    }

    /**
     * ��ȡ GANXiSw���ص��������ֽ��
     * @param communityPath
     * @param overlapNodesPath
     * @return OverlapPartition����
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

            ///��ʼ��ȡ�ص��ڵ�
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


//            MyPrint.print("GANXiSw����󶹻�������õ�������size�ֲ���"+sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        GANXiSwPartition.setCommunities(communities);
        GANXiSwPartition.setNodeMapCommunities(nodeMapCommunities);
        return GANXiSwPartition;
    }
}
