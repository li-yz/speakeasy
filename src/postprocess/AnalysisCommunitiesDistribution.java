package postprocess;

import serialprocess.Graph;
import serialprocess.OverlapPartition;
import serialprocess.Partition;
import utils.MyPrint;
import utils.MySerialization;
import utils.MyUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by Liyanzhen on 2017/2/15.
 */
public class AnalysisCommunitiesDistribution {
    public static void main(String[] args){
        //�����л�֮ǰ���������ͼG�����ŷ��ص����֡��ص����ֽ��
//        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.2.18\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\��ʷ������\\2017.2.18\\overlapPartition.obj");

        MyPrint.print("���ص����������� "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("�ص����������� "+overlapPartition.getCommunities().size());

        compare1(bestNonOverlapPartition,overlapPartition);


    }

    private static void compare1(Partition bestNonOverlapPartition,OverlapPartition overlapPartition){
        Set<String> comNames = new HashSet<String>();
        Iterator iter = bestNonOverlapPartition.getCommunities().keySet().iterator();
        while(iter.hasNext()){
            String comName =(String) iter.next();
            comNames.add(comName);
        }
        Set<String> totalSeperateCommuNames = new HashSet<String>();
        Set<String> meaningfulSeparateCommuNames = new HashSet<String>();
        int meaningfulSeperateCommuNum = 0;
        Set<String> meaningfullOverlapCommuNames = new HashSet<String>();
        Set<String> totalOverlapTagSet = new HashSet<String>();
        for(String commuName :comNames){
            List<String> nonOverlapCommu = bestNonOverlapPartition.getCommunities().get(commuName);
            List<String> overlapCommu = overlapPartition.getCommunities().get(commuName);
            if(overlapCommu.size() <= nonOverlapCommu.size()){
                //��������û�л����ص��ڵ�, �����������Ƕ�������������ͳ����
                totalSeperateCommuNames.add(commuName);

                if(nonOverlapCommu.size() > 7){ //��������������ڵĻ���������7����á�����������Ҳ����һ��������ѧ���壬ֵ�÷���
                    meaningfulSeperateCommuNum++;
                    meaningfulSeparateCommuNames.add(commuName);
                }
            }else{
                //���ص��ڵ㱻������commuName������
                totalOverlapTagSet.add(commuName);
                Set<String> overlapCommunities = new HashSet<String>();
                overlapCommunities.add(commuName);
                MyPrint.print("������־"+commuName+" ������ �����ص��ڵ㣺"+(overlapCommu.size()-nonOverlapCommu.size())+"��");
                MyPrint.print("������־"+commuName+"���ص�ʱ��С="+nonOverlapCommu.size()+"; �ص��´�С="+overlapCommu.size());
                MyPrint.print("---------------------------------------");
                if(nonOverlapCommu.size() > 5 && overlapCommu.size() > 7){//������ڵ�֮ǰ commuName��������С > 5������ڵ�֮����ΪcommuName���ص�������С > 7������Ϊ��commuName��Ӧ���������������
                    meaningfullOverlapCommuNames.add(commuName);
                }


                //ĪҪ������һ�㣺�������롱�Ľڵ�v����ԭ����������cҲ�ͳ����ص���������cû�������µĽڵ㣬���������ͳ�������ֲ���������c
                //��˻�Ҫ�ҳ�����Щ �������롱���ص��ڵ�ԭ���������������Ƿ�ͬ��ֵ�÷���
                List<String> overlapNodes = MyUtils.getOverlapNodes(overlapCommu,nonOverlapCommu);
                for(String node: overlapNodes){
                    List<String> commuTags = overlapPartition.getNodeMapCommunities().get(node);
                    //�����ڵ�node�����Ķ��������ɸѡ�������ڵ�ǰ����commuName�� ������С > 7������
                    for(String tag: commuTags){
                        if(!tag.equals(commuName)){//����tag���������ǵ�ǰ������
                            if(bestNonOverlapPartition.getCommunities().get(tag).size() > 7) {//�Ҵ�С > 7������������������ص�����֮һ
                                meaningfullOverlapCommuNames.add(tag);
                            }
                            totalOverlapTagSet.add(tag);

                        }
                    }
                }
            }
        }
        MyPrint.print("�ܵ��ص��������� = "+totalOverlapTagSet.size()+"������ "+meaningfullOverlapCommuNames.size()+"��������");
        MyPrint.print("��������ص�����������������--------------------------");
        for(String e :meaningfullOverlapCommuNames){
            MyPrint.print(e);
        }
        MyPrint.print("-----------------------------");
        //��ʱ�ҵ���totalSeperateCommuNames �� totalOverlapTagSet���ܴ��ڽ����������е�������Ӧ�����ص��ģ����Ҫ�ҵ���������Щ���������Ƕ��������ˣ����Ҫ��totalSeperateCommuNames��meaningfulSeperateCommuNum���Ƴ�����Ԫ��
        Set<String> intersection = getInterSectionOf2Set(totalSeperateCommuNames,totalOverlapTagSet);
        for(String e:intersection){
            totalSeperateCommuNames.remove(e);
            meaningfulSeparateCommuNames.remove(e);
        }


        MyPrint.print("�������� ���� = "+totalSeperateCommuNames.size());
        MyPrint.print("������Ķ����������������ġ����������Ļ������ > 7������ "+meaningfulSeperateCommuNum+" ��");


        //����������ص������� ������Ķ�����������ʽ��ӡ���ı��ļ���
//        saveMeaningfulComuunities(overlapPartition,meaningfullOverlapCommuNames,meaningfulSeparateCommuNames);
//        saveTotalOverlapCommunities(overlapPartition,totalOverlapTagSet);
        findTheSourceOfNewBigOverlapNodes(overlapPartition,totalOverlapTagSet);
        findIneractionBetweenTwoverlapCommunities(overlapPartition,totalOverlapTagSet);
    }

    /**
     * �ж�ԭ��ֻ��һ���ڵ�������� �����˺ܶ�ڵ�֮����ɴ��ص�����������Щ�����յĽڵ���������
     * @param overlapPartition
     * @param totalOverlapTagSet
     */
    private static void findTheSourceOfNewBigOverlapNodes(OverlapPartition overlapPartition,Set<String>totalOverlapTagSet){
        List<String> newBigOverlapCommunity = overlapPartition.getCommunities().get("Gma.1043.2.S1_at");
        for(String tag :totalOverlapTagSet){
            List<String> nodes = overlapPartition.getCommunities().get(tag);
            int num = 0;
            for(String node :nodes){
                if(newBigOverlapCommunity.contains(node)){
                    num++;

                }
            }
            if(num > 0 && !"Gma.1043.2.S1_at".equals(tag)){
                MyPrint.print("1297���ڵ�Ĵ�����Gma.1043.2.S1_at�������� "+tag+"�е� "+num+" ���ڵ�");
            }
        }
    }

    private static void findIneractionBetweenTwoverlapCommunities(OverlapPartition overlapPartition,Set<String>totalOverlapTagSet){
        List<String>totalOverlapCommunityNames = new ArrayList<String>();
        totalOverlapCommunityNames.addAll(totalOverlapTagSet);
        for(int i=0;i < totalOverlapCommunityNames.size();i++){
            List<String> nodesOfCommunityI = overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(i));
            Set<String> setI = new HashSet<String>();
            setI.addAll(nodesOfCommunityI);
            for(int j=i+1;j < totalOverlapCommunityNames.size();j++){
                List<String> nodesOfCommunityJ = overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(j));
                Set<String> setJ = new HashSet<String>();
                setJ.addAll(nodesOfCommunityJ);

                Set<String> interactionOfCommunityIAndJ = getInterSectionOf2Set(setI,setJ);
                if(interactionOfCommunityIAndJ.size() > 0) {
                    MyPrint.print("������־" + totalOverlapCommunityNames.get(i) + " ��������־" + totalOverlapCommunityNames.get(j) + " ֮����ص��ڵ����=" + interactionOfCommunityIAndJ.size());
                }
            }
        }
    }

    private static void saveMeaningfulComuunities(OverlapPartition overlapPartition,Set<String> meaningfullOverlapCommuNames,Set<String>meaningfulSeperateCommuNames){
        try {
            FileWriter writer1 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulSeperateResult.txt");
            BufferedWriter bw1 = new BufferedWriter(writer1);
            FileWriter writer2 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulOverlapResult.txt");
            BufferedWriter bw2 = new BufferedWriter(writer2);

            StringBuffer sb = new StringBuffer();
            sb.append("���ļ��е�������������Ķ����������������������ص��ڵ�ġ����������Ļ������ > 7������ \n");
            bw1.write(sb.toString());
            sb = new StringBuffer();
            sb.append("��������������� ="+meaningfulSeperateCommuNames.size()+"\n");
            bw1.newLine();
            bw1.write(sb.toString());

            sb = new StringBuffer();
            sb.append("���ļ������������ص����� \n");
            bw2.write(sb.toString());
            sb = new StringBuffer();
            sb.append("������������ص��������� = "+meaningfullOverlapCommuNames.size()+"\n");
            bw2.newLine();
            bw2.write(sb.toString());
            sb = new StringBuffer();
            sb.append("������ڵ�֮ǰ commuName��������С > 5������ڵ�֮����ΪcommuName���ص�������С > 7������Ϊ��commuName��Ӧ���������������");
            bw2.newLine();
            bw2.write(sb.toString());
            sb = new StringBuffer();
            sb.append("��Щԭ��ֻ��1������ٽڵ������������ʶ���ص��ڵ㣬��������С�����ܴ���ԭ��ֻ��1���ڵ㣬�����������1297��������ԭ�����е�1���ڵ�������������ѧ����");
            bw2.newLine();
            bw2.write(sb.toString());
            Iterator it = overlapPartition.getCommunities().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)it.next();
                String commuName = entry.getKey();
                if(meaningfulSeperateCommuNames.contains(commuName)){
                    //��������������Ķ��������������������ص��ڵ�ġ����������Ļ������ > 7������
                    bw1.newLine();
                    bw1.write("-----------------------------------------------------------------------------");
                    bw1.newLine();
                    bw1.write("�������� "+commuName+"����"+overlapPartition.getCommunities().get(commuName).size()+" ������");
                    for(String e:overlapPartition.getCommunities().get(commuName)){
                        bw1.newLine();
                        bw1.write(e);
                    }
                    bw1.newLine();
                    bw1.write("-----------------------------------------------------------------------------");
                }
                else if(meaningfullOverlapCommuNames.contains(commuName)){
                    bw2.newLine();
                    bw2.write("-----------------------------------------------------------------------------");
                    bw2.newLine();
                    bw2.write("�������� "+commuName+"����"+overlapPartition.getCommunities().get(commuName).size()+" ������");
                    for(String e:overlapPartition.getCommunities().get(commuName)){
                        bw2.newLine();
                        bw2.write(e);
                    }
                    bw2.newLine();
                    bw2.write("-----------------------------------------------------------------------------");
                }

            }
            bw1.close();
            writer1.close();
            bw2.close();
            writer2.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void saveTotalOverlapCommunities(OverlapPartition overlapPartition,Set<String> totalOverlapTagSet){
        try {
            FileWriter writer = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\totalOverlapCommunitiesResult.txt");
            BufferedWriter bw = new BufferedWriter(writer);

            StringBuffer sb = new StringBuffer();
            sb.append("���ļ�����ȫ�����ص����������� = "+totalOverlapTagSet.size());
            bw.write(sb.toString());
            sb = new StringBuffer();
            Iterator it = overlapPartition.getCommunities().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)it.next();
                String commuName = entry.getKey();
                if(totalOverlapTagSet.contains(commuName)){
                    bw.newLine();
                    bw.write("-----------------------------------------------------------------------------");
                    bw.newLine();
                    bw.write("�������� "+commuName+"����"+overlapPartition.getCommunities().get(commuName).size()+" ������");
                    for(String e:overlapPartition.getCommunities().get(commuName)){
                        bw.newLine();
                        bw.write(e);
                    }
                    bw.newLine();
                    bw.write("-----------------------------------------------------------------------------");
                }

            }
            bw.close();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static Set<String> getInterSectionOf2Set(Set<String>totalSeperateCommuNames,Set<String>totalOverlapCommuNames){
        Set<String> result = new HashSet<String>();

        result.clear();
        result.addAll(totalSeperateCommuNames);
        result.retainAll(totalOverlapCommuNames);
        return result;
    }
}
