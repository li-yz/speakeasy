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
//        saveMeaningfulComuunities(overlapPartition,meaningfullOverlapCommuNames,meaningfulSeparateCommuNames);
//        saveTotalOverlapCommunities(overlapPartition,totalOverlapTagSet);
        findIneractionBetweenTwoverlapCommunities(overlapPartition,meaningfullOverlapCommuNames ,bestNonOverlapPartition);
    }

    private static void findIneractionBetweenTwoverlapCommunities(OverlapPartition overlapPartition,Set<String>overlapTagSet ,Partition bestNonOverlapPartition){
        try {
            Map<String,String> geneIdMapEntrezId =(Map<String,String>) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\����ID-gene name\\geneIdMapEntrezId.obj");//���� affy ID�� entrez ID֮��ӳ���ϵ

            FileWriter writer = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\overlapNodesBetweenMeaningfulOverlapCommunities.txt");
            BufferedWriter bw = new BufferedWriter(writer);
            Set<String> allOverlapNodes = new HashSet<String>();
            List<String> totalOverlapCommunityNames = new ArrayList<String>();
            int oc = overlapTagSet.size();
            String[] overlapCommunityNames = new String[oc];
            int[] trueOverlapComSizeArray = new int[oc];//����True�ص�������������С�������������������˳��һ��
            int[] comSizeOfTrueOverlapBeforeOverlapDetect = new int[oc];
            int[][] intersectionMatrix = new int[oc][oc];
            totalOverlapCommunityNames.addAll(overlapTagSet);
            for (int i = 0; i < totalOverlapCommunityNames.size(); i++) {
                overlapCommunityNames[i] = totalOverlapCommunityNames.get(i);
                List<String> nodesOfCommunityI = overlapPartition.getCommunities().get(totalOverlapCommunityNames.get(i));
                Set<String> setI = new HashSet<String>();
                setI.addAll(nodesOfCommunityI);
                trueOverlapComSizeArray[i] = nodesOfCommunityI.size();
                comSizeOfTrueOverlapBeforeOverlapDetect[i] = bestNonOverlapPartition.getCommunities().get(totalOverlapCommunityNames.get(i)).size();
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
                            String entrezId = geneIdMapEntrezId.get(e);
                            sb.append(entrezId);
                            sb.append("\n");
                        }
                        sb.deleteCharAt(sb.lastIndexOf("\n"));
                        bw.write(sb.toString());
                        bw.newLine();
                        bw.write("--------------------------");
                        bw.newLine();
                    }

//                    if((double)interactionOfCommunityIAndJ.size()/setI.size() < 0.85 && (double)interactionOfCommunityIAndJ.size()/setJ.size() < 0.85){
                        allOverlapNodes.addAll(interactionOfCommunityIAndJ);
//                    }

                }
            }

            bw.close();
            writer.close();

            MySerialization.serializeObject(overlapCommunityNames,"D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapCommunitiesNames.obj");
            MySerialization.serializeObject(trueOverlapComSizeArray,"D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapComSizeArray.obj");
            MySerialization.serializeObject(intersectionMatrix,"D:\\paperdata\\soybean\\community detection\\community analysis\\trueOverlapCommunitiesIntersection.obj");
            MySerialization.serializeObject(comSizeOfTrueOverlapBeforeOverlapDetect,"D:\\paperdata\\soybean\\community detection\\community analysis\\comSizeOfTrueOverlapBeforeOverlapDetect.obj");
            printOverlapNodes(allOverlapNodes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void printOverlapNodes(Set<String>allOverlapNodes){
        try {
            Set<String> set = new HashSet<String>();
            set.add("Gma.16735.2.S1_at");
            set.add("GmaAffx.21211.1.S1_at");
            set.add("GmaAffx.92386.1.S1_at");
            set.add("GmaAffx.91805.1.S1_s_at");
            set.add("Gma.6606.1.S1_at");
            set.add("GmaAffx.91805.1.S1_at");
            set.add("GmaAffx.80951.1.S1_at");
            set.add("Gma.7559.1.S1_s_at");
            set.add("GmaAffx.92383.1.S1_at");
            Set<String> intersection = getInterSectionOf2Set(set,allOverlapNodes);
            MyPrint.print("+++++++++++++��������ص������ڵ��ص��ڵ�����������������ֵ���Ҫ���򡱣�"+intersection.size());

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
            Map<String,String> geneIdMapEntrezId =(Map<String,String>) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\����ID-gene name\\geneIdMapEntrezId.obj");

            FileWriter writer1 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulSeperateResult.txt");
            BufferedWriter bw1 = new BufferedWriter(writer1);
            FileWriter entrezWriter1 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulSeperateResultInEntrezId.txt");
            BufferedWriter bwEntrez1 = new BufferedWriter(entrezWriter1);

            FileWriter writer2 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulOverlapResult.txt");
            BufferedWriter bw2 = new BufferedWriter(writer2);
            FileWriter entrezWriter2 = new FileWriter("D:\\paperdata\\soybean\\community detection\\community analysis\\meaningfulOverlapResultInEntrezId.txt");
            BufferedWriter bwEntrez2 = new BufferedWriter(entrezWriter2);

            StringBuffer sb = new StringBuffer();
            sb.append("���ļ��е�������������Ķ����������������������ص��ڵ�ġ����������Ļ������ >= 10������ \n");
            bw1.write(sb.toString());

            sb = new StringBuffer();
            sb.append("��������������� ="+meaningfulSeperateCommuNames.size()+"\n");
            bw1.newLine();
            bw1.write(sb.toString());
            bwEntrez1.write("���ļ�����������Ķ�����������֮ͬ���ǣ���entrez gene ID������һ�����򣬱��ڽ���pathway����"+"\n");
            bwEntrez1.write("��������������� ="+meaningfulSeperateCommuNames.size()+"\n");

            //���������ġ�����������
            for(String name :meaningfulSeperateCommuNames){
                bw1.newLine();
                bw1.write("-----------------------------------------------------------------------------");
                bw1.newLine();
                bw1.write("�������� "+name+"����"+overlapPartition.getCommunities().get(name).size()+" ������");

                bwEntrez1.newLine();
                bwEntrez1.write("-------------------------------------------------------------------------");
                bwEntrez1.newLine();
                bwEntrez1.write("�������� "+name+"����"+overlapPartition.getCommunities().get(name).size()+" ������");
                for(String e:overlapPartition.getCommunities().get(name)){
                    bw1.newLine();
                    bw1.write(e);

                    if(geneIdMapEntrezId.containsKey(e)){
                        bwEntrez1.newLine();
                        bwEntrez1.write(geneIdMapEntrezId.get(e));
                    }

                }
                bw1.newLine();
                bw1.write("-----------------------------------------------------------------------------");
            }

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

            bwEntrez2.write("���ļ������������ص����� \n");
            bwEntrez2.write("������������ص��������� = "+meaningfullOverlapCommuNames.size()+"\n");

            //�����������ص�����
            List<String> list = new ArrayList<String>();
            list.addAll(meaningfullOverlapCommuNames);
            for(int i=0;i < list.size();i++){
                String comName = list.get(i);
                bw2.newLine();
                bw2.write("-----------------------------------------------------------------------------");
                bw2.newLine();
                bw2.write("�������� "+comName+"����"+overlapPartition.getCommunities().get(comName).size()+" ������");

                bwEntrez2.newLine();
                bwEntrez2.write("--------------------------------------------------------------------------");
                bwEntrez2.newLine();
                bwEntrez2.write("�������� "+comName+"����"+overlapPartition.getCommunities().get(comName).size()+" ������");
                for(String e:overlapPartition.getCommunities().get(comName)){
                    bw2.newLine();
                    bw2.write(e);

                    if(geneIdMapEntrezId.containsKey(e)){
                        bwEntrez2.newLine();
                        bwEntrez2.write(geneIdMapEntrezId.get(e));
                    }
                }
                bw2.newLine();
                bw2.write("-----------------------------------------------------------------------------");
            }

            bw1.close();
            writer1.close();
            bw2.close();
            writer2.close();
            bwEntrez1.close();
            entrezWriter1.close();
            bwEntrez2.close();
            entrezWriter2.close();
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

    public int[] getComSizeOfTrueOverlapBeforeOverlapDetect(){
        return (int[])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\community analysis\\comSizeOfTrueOverlapBeforeOverlapDetect.obj");
    }

}
