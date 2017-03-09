package postprocess;

import serialprocess.Graph;
import serialprocess.OverlapPartition;
import serialprocess.Partition;
import utils.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Liyanzhen on 2017/2/15.
 */
public class AnalysisCommunitiesDistribution {
    public static void main(String[] args){
        //�����л�֮ǰ���������ͼG�����ŷ��ص����֡��ص����ֽ��
//        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

        MyPrint.print("���ص����������� "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("�ص����������� "+overlapPartition.getCommunities().size());

        Iterator iterator = overlapPartition.getCommunities().entrySet().iterator();
        int sum = 0;
        while(iterator.hasNext()){
            Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)iterator.next();
            sum+=entry.getValue().size();
        }
        MyPrint.print("�ص�����ƽ��������С="+(double)sum/overlapPartition.getCommunities().size());

        compare1(bestNonOverlapPartition,overlapPartition);


    }

    private static void compare1(Partition bestNonOverlapPartition,OverlapPartition overlapPartition){
        Set<String> comNames = new HashSet<String>();
        int[]nonOverlapComSize = new int[bestNonOverlapPartition.getCommunities().size()];
        int[]overlapComSize = new int[overlapPartition.getCommunities().size()];
        Iterator iter = bestNonOverlapPartition.getCommunities().keySet().iterator();
        while(iter.hasNext()){
            String comName =(String) iter.next();
            comNames.add(comName);
        }
        Set<String> totalSeperateCommuNames = new HashSet<String>();
        Set<String> meaningfulSeparateCommuNames = new HashSet<String>();
        Set<String> meaningfullOverlapCommuNames = new HashSet<String>();
        Set<String> totalOverlapTagSet = new HashSet<String>();
        int i=0;
        for(String commuName :comNames){
            nonOverlapComSize[i] = bestNonOverlapPartition.getCommunities().get(commuName).size();
            overlapComSize[i] = overlapPartition.getCommunities().get(commuName).size();
            List<String> nonOverlapCommu = bestNonOverlapPartition.getCommunities().get(commuName);
            List<String> overlapCommu = overlapPartition.getCommunities().get(commuName);
            if(overlapCommu.size() <= nonOverlapCommu.size()){
                //��������û�л����ص��ڵ�, �����������Ƕ�������������ͳ����
                totalSeperateCommuNames.add(commuName);

                if(nonOverlapCommu.size() >= 10){ //��������������ڵĻ���������7����á�����������Ҳ����һ��������ѧ���壬ֵ�÷���
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
                if( overlapCommu.size() >= 10){//������ڵ�֮ǰ commuName��������С > 5������ڵ�֮����ΪcommuName���ص�������С > 7������Ϊ��commuName��Ӧ���������������
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
                            if(bestNonOverlapPartition.getCommunities().get(tag).size() >= 10) {//�Ҵ�С > 7������������������ص�����֮һ
                                meaningfullOverlapCommuNames.add(tag);
                            }
                            totalOverlapTagSet.add(tag);

                        }
                    }
                }
            }
            i++;
        }

        MyPrint.print("-----------------------------");
        //��ʱ�ҵ���totalSeperateCommuNames �� totalOverlapTagSet���ܴ��ڽ����������е�������Ӧ�����ص��ģ����Ҫ�ҵ���������Щ���������Ƕ��������ˣ����Ҫ��totalSeperateCommuNames��meaningfulSeperateCommuNum���Ƴ�����Ԫ��
        Set<String> intersection = getInterSectionOf2Set(totalSeperateCommuNames,totalOverlapTagSet);
        for(String e:intersection){
            totalSeperateCommuNames.remove(e);
            meaningfulSeparateCommuNames.remove(e);
        }


        MyPrint.print("�������� ���� = "+totalSeperateCommuNames.size());
        MyPrint.print("������Ķ����������������ġ����������Ļ������ >= 10������ "+meaningfulSeparateCommuNames.size()+" ��");

        MyPrint.print("-----------------------------");
        MyPrint.print("�ܵ��ص��������� = "+totalOverlapTagSet.size()+"������ "+meaningfullOverlapCommuNames.size()+"��������");
        //�ж���������ص������У���û��С������ȫ������������������������У���С����Ӧ�ñ�ɾ��
        List<String> list1 = new ArrayList<String>();
        Set<String> removeList = new HashSet<String>();
        list1.addAll(meaningfullOverlapCommuNames);
        for(int m=0;m < list1.size();m++){
            for(int n=m+1;n < list1.size();n++){
                String mName = list1.get(m);
                String nName = list1.get(n);
                if(overlapPartition.getCommunities().get(mName).containsAll(overlapPartition.getCommunities().get(nName))){
                    removeList.add(nName);
                }else if(overlapPartition.getCommunities().get(nName).containsAll(overlapPartition.getCommunities().get(mName))){
                    removeList.add(mName);
                }
            }
        }
        MyPrint.print("��"+removeList.size()+"����������ȫ�����������������ѱ�������С����Ӧ�ñ��Ƴ�");
        for(String e:removeList){
            meaningfullOverlapCommuNames.remove(e);
        }
        MyPrint.print("�Ƴ�"+removeList.size()+"������ȫ������С�����󣬻���"+meaningfullOverlapCommuNames.size()+"����������ص�����");

        MyPrint.print("��������ص�����������������--------------------------");
        for(String e :meaningfullOverlapCommuNames){
            MyPrint.print(e);
        }
        MyPrint.print("-----------------------------");

        //���л����� nonOverlapComSize����� overlapComSize����
        MySerialization.serializeObject(nonOverlapComSize,"D:\\paperdata\\soybean\\community detection\\community analysis\\nonOverlapComSizeArray.obj");
        MySerialization.serializeObject(overlapComSize,"D:\\paperdata\\soybean\\community detection\\community analysis\\overlapComSizeArray.obj");

        //����������ص������� ������Ķ�����������ʽ��ӡ���ı��ļ���
        saveMeaningfulComuunities(overlapPartition,meaningfullOverlapCommuNames,meaningfulSeparateCommuNames);
        saveTotalOverlapCommunities(overlapPartition,totalOverlapTagSet);
//        findTheSourceOfNewBigOverlapNodes(overlapPartition,totalOverlapTagSet);
        findIneractionBetweenTwoverlapCommunities(overlapPartition,meaningfullOverlapCommuNames);
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

    private static void findIneractionBetweenTwoverlapCommunities(OverlapPartition overlapPartition,Set<String>overlapTagSet){
        try {
            FileWriter writer = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\overlapNodesBetweenMeaningfulOverlapCommunities.txt");
            BufferedWriter bw = new BufferedWriter(writer);
            Set<String> allOverlapNodes = new HashSet<String>();
            List<String> totalOverlapCommunityNames = new ArrayList<String>();
            int oc = overlapTagSet.size();
            String[] overlapCommunityNames = new String[oc];
            int[] trueOverlapComSizeArray = new int[oc];//����True�ص�������������С�������������������˳��һ��
            int[][] intersectionMatrix = new int[oc][oc];
            totalOverlapCommunityNames.addAll(overlapTagSet);
            for (int i = 0; i < totalOverlapCommunityNames.size(); i++) {
                overlapCommunityNames[i] = totalOverlapCommunityNames.get(i);
                List<String> nodesOfCommunityI = overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(i));
                Set<String> setI = new HashSet<String>();
                setI.addAll(nodesOfCommunityI);
                trueOverlapComSizeArray[i] = nodesOfCommunityI.size();
                for (int j = i + 1; j < totalOverlapCommunityNames.size(); j++) {
                    List<String> nodesOfCommunityJ = overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(j));
                    Set<String> setJ = new HashSet<String>();
                    setJ.addAll(nodesOfCommunityJ);

                    Set<String> interactionOfCommunityIAndJ = getInterSectionOf2Set(setI, setJ);

                    if (interactionOfCommunityIAndJ.size() > 0) {
                        StringBuffer sb = new StringBuffer();
                        intersectionMatrix[i][j] = interactionOfCommunityIAndJ.size();
                        intersectionMatrix[j][i] = interactionOfCommunityIAndJ.size();
                        MyPrint.print("������� ("+i+") ������־" + totalOverlapCommunityNames.get(i) + "(" + overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(i)).size() + ")" + "��������� ("+j+") ������־" + totalOverlapCommunityNames.get(j) + "(" + overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(j)).size() + ")" + " ֮����ص��ڵ����=" + interactionOfCommunityIAndJ.size());
                        sb.append("�������("+i+") ������־" + totalOverlapCommunityNames.get(i) + "(" + overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(i)).size() + "��)" + "��������� ("+j+") ��������־" + totalOverlapCommunityNames.get(j) + "(" + overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(j)).size() + "��)" + " ֮����ص��ڵ����=" + interactionOfCommunityIAndJ.size()+" :");
                        sb.append("\n");
                        for(String e: interactionOfCommunityIAndJ){
                            sb.append(e);
                            sb.append(",");
                        }
                        sb.deleteCharAt(sb.lastIndexOf(","));
                        bw.write(sb.toString());
                        bw.newLine();
                        bw.write("--------------------------");
                        bw.newLine();
                    }

                    if((double)interactionOfCommunityIAndJ.size()/setI.size() < 0.85 && (double)interactionOfCommunityIAndJ.size()/setJ.size() < 0.85){
                        allOverlapNodes.addAll(interactionOfCommunityIAndJ);
                    }

                }
            }

            bw.close();
            writer.close();

            MySerialization.serializeObject(overlapCommunityNames,"D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapCommunitiesNames.obj");
            MySerialization.serializeObject(trueOverlapComSizeArray,"D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapComSizeArray.obj");
            MySerialization.serializeObject(intersectionMatrix,"D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapCommunitiesIntersection.obj");
            printOverlapNodes(allOverlapNodes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void printOverlapNodes(Set<String>allOverlapNodes){
        try {
            FileWriter writer = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\allOverlapNodes.txt");
            BufferedWriter bw = new BufferedWriter(writer);

            for(String e: allOverlapNodes){
                bw.write(e);
                bw.newLine();
            }

            bw.close();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * ����overlapPartition ����������ص������Լ�������ġ������ġ�����
     * @param overlapPartition
     * @param meaningfullOverlapCommuNames
     * @param meaningfulSeperateCommuNames
     */
    private static void saveMeaningfulComuunities(OverlapPartition overlapPartition,Set<String> meaningfullOverlapCommuNames,Set<String>meaningfulSeperateCommuNames){
        try {
            FileWriter writer1 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulSeperateResult.txt");
            BufferedWriter bw1 = new BufferedWriter(writer1);
            FileWriter writer2 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulOverlapResult.txt");
            BufferedWriter bw2 = new BufferedWriter(writer2);

            StringBuffer sb = new StringBuffer();
            sb.append("���ļ��е�������������Ķ����������������������ص��ڵ�ġ����������Ļ������ >= 10������ \n");
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
            sb.append("������ڵ�֮����ΪcommuName���ص�������С >= 10������Ϊ��commuName��Ӧ���������������");
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
//                    StringBuffer sb1 = new StringBuffer();
                    for(String e:overlapPartition.getCommunities().get(commuName)){
//                        sb1.append(e);
//                        sb1.append(",");
                        bw1.newLine();
                        bw1.write(e);
                    }
//                    bw1.newLine();
//                    sb1.deleteCharAt(sb1.lastIndexOf(","));
//                    bw1.write(sb1.toString());
                    bw1.newLine();
                    bw1.write("-----------------------------------------------------------------------------");
                }
                else if(meaningfullOverlapCommuNames.contains(commuName)){
                    bw2.newLine();
                    bw2.write("-----------------------------------------------------------------------------");
                    bw2.newLine();
                    bw2.write("�������� "+commuName+"����"+overlapPartition.getCommunities().get(commuName).size()+" ������");
//                    StringBuffer sb2 = new StringBuffer();
                    for(String e:overlapPartition.getCommunities().get(commuName)){
                        bw2.newLine();
                        bw2.write(e);
//                        sb2.append(e);
//                        sb2.append(",");
                    }
//                    bw2.newLine();
//                    sb2.deleteCharAt(sb2.lastIndexOf(","));
//                    bw2.write(sb2.toString());
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

    /**
     * txt����ȫ�����ص�����
     * @param overlapPartition
     * @param totalOverlapTagSet
     */
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

    /**
     * ���������ϵĽ���
     * @param totalSeperateCommuNames
     * @param totalOverlapCommuNames
     * @return ����
     */
    private static Set<String> getInterSectionOf2Set(Set<String>totalSeperateCommuNames,Set<String>totalOverlapCommuNames){
        Set<String> result = new HashSet<String>();

        result.clear();
        result.addAll(totalSeperateCommuNames);
        result.retainAll(totalOverlapCommuNames);
        return result;
    }

    /**
     * ���ط��ص��������ֵ�����size�ֲ�
     * @return
     */
    public int[] getNonoverlapCommunitiesSizeArray(){
        return (int[])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\nonOverlapComSizeArray.obj");
    }


    /**
     * �����ص��������ֽ��������size�ֲ�
     * @return
     */
    public int[] getOverlapCommunitiesSizeArray(){
        return (int[])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\overlapComSizeArray.obj");
    }

    /**
     * �������ġ��ص�����������֮��Ľ�����С
     * @return
     */
    public int[][] getTrueOverlapCommunitiesIntersectionMatrix(){
        return (int[][])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapCommunitiesIntersection.obj");
    }

    public String[] getTrueOverlapCommunitiesNames(){
        return (String[])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapCommunitiesNames.obj");
    }

    public int[] getTrueOverlapComSizeArray(){
        return (int[])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapComSizeArray.obj");
    }

}
