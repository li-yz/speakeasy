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
    //输出社区划分结果
    public static void outputCommunities(Map<String, List<String>> partition) throws IOException {
        FileWriter writer = new FileWriter("D:\\paperdata\\test network\\使用lfr生成的网络数据\\speakeasy result\\realPartition.txt");
        BufferedWriter bw = new BufferedWriter(writer);

        Iterator it=partition.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String communityTag=(String)entry.getKey();
            List<String> nodesList=partition.get(communityTag);
            StringBuffer sb=new StringBuffer();

            sb.append("社区标志："+communityTag+"的社区内：");
            sb.append(nodesList);

            bw.newLine();
            bw.write(sb.toString());
        }
        bw.close();
        writer.close();
    }

    //输出重叠节点
    public static void outputOverLapNodes(Map<String, List<String>> nodeAndCommunities) throws IOException{
        FileWriter writer = new FileWriter("D:\\paperdata\\test network\\使用lfr生成的网络数据\\speakeasy result\\realOverlapNodes.txt");
        BufferedWriter bw = new BufferedWriter(writer);

        Iterator it=nodeAndCommunities.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String nodeName=(String)entry.getKey();
            List<String> communitiesList=nodeAndCommunities.get(nodeName);
            StringBuffer sb=new StringBuffer();

            sb.append("节点："+nodeName+"同时属于 "+communitiesList.size()+" 个社区：");
            for(String community :communitiesList){
                sb.append(community+"\t");
            }

            bw.newLine();
            bw.write(sb.toString());
        }
        bw.close();
        writer.close();
    }

    //保存筛选重叠社区节点时的 每一个节点的平均权值分布
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
