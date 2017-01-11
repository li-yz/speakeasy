package test;

import utils.MyOutPut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liyanzhen on 2017/1/11.
 */
public class LFRTest {
    public static void main(String[] args){
        String realCommunityPath = "D:\\paperdata\\test network\\ʹ��lfr���ɵ���������\\community.dat";
        readAndOutPutRealCommunityFromTextFile(realCommunityPath);

    }

    private static void readAndOutPutRealCommunityFromTextFile(String path){
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
                //��ͳ���������ֽ��
                for(int i=1;i < array.length;i++){
                    String communityName = array[i];
                    if(communities.containsKey(communityName)){
                        communities.get(communityName).add(vertexName);
                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(vertexName);
                        communities.put(communityName,list);
                    }
                }

                //ͳ���ص������ڵ�
                if(array.length > 2){//˵����ǰ�ڵ����ص������ڵ�
                    for(int i=1;i < array.length;i++){
                        List<String> list = new ArrayList<String>();
                        list.add(array[i]);
                        overlapNodeMapCommunities.put(array[0],list);
                    }
                }
            }

            br.close();
            fr.close();

            //�����ʵ�������ֽ�����ص������ڵ�
            MyOutPut.outputCommunities(communities);
            MyOutPut.outputOverLapNodes(overlapNodeMapCommunities);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
