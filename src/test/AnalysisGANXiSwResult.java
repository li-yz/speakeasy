package test;

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

            MyPrint.print("GANXiSw����󶹻�������õ�������size�ֲ���"+sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        GANXiSwPartition.setNodeCommunityMap(nodeMapCommunity);
        GANXiSwPartition.setCommunities(communities);
        return GANXiSwPartition;
    }
}
