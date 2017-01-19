package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2017/1/11.
 */
public class MyOutPut {
    //����������ֽ��
    public static void outputCommunities(Map<String, List<String>> partition) throws IOException {
        FileWriter writer = new FileWriter("D:\\paperdata\\test network\\ʹ��lfr���ɵ���������\\speakeasy result\\realPartition.txt");
        BufferedWriter bw = new BufferedWriter(writer);

        Iterator it=partition.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String communityTag=(String)entry.getKey();
            List<String> nodesList=partition.get(communityTag);
            StringBuffer sb=new StringBuffer();

            sb.append("������־��"+communityTag+"�������ڣ�");
            sb.append(nodesList);

            bw.newLine();
            bw.write(sb.toString());
        }
        bw.close();
        writer.close();
    }

    //����ص��ڵ�
    public static void outputOverLapNodes(Map<String, List<String>> nodeAndCommunities) throws IOException{
        FileWriter writer = new FileWriter("D:\\paperdata\\test network\\ʹ��lfr���ɵ���������\\speakeasy result\\realOverlapNodes.txt");
        BufferedWriter bw = new BufferedWriter(writer);

        Iterator it=nodeAndCommunities.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String nodeName=(String)entry.getKey();
            List<String> communitiesList=nodeAndCommunities.get(nodeName);
            StringBuffer sb=new StringBuffer();

            sb.append("�ڵ㣺"+nodeName+"ͬʱ���� "+communitiesList.size()+" ��������");
            for(String community :communitiesList){
                sb.append(community+"\t");
            }

            bw.newLine();
            bw.write(sb.toString());
        }
        bw.close();
        writer.close();
    }

    //����ɸѡ�ص������ڵ�ʱ�� ÿһ���ڵ��ƽ��Ȩֵ�ֲ�
    public static void saveStringResultToTxt(String result,String path){
        try{
            FileWriter writer = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(writer);

            bw.write(result);
            bw.newLine();

            bw.close();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
