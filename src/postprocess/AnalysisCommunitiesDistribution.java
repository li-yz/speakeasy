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
        //反序列化之前保存的网络图G、最优非重叠划分、重叠划分结果
//        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        Partition bestNonOverlapPartition = (Partition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.2.18\\bestNonOverlapPartition.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\历史计算结果\\2017.2.18\\overlapPartition.obj");

        MyPrint.print("非重叠社区个数： "+bestNonOverlapPartition.getCommunities().size());
        MyPrint.print("重叠社区个数： "+overlapPartition.getCommunities().size());

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
                //该社区中没有划入重叠节点, 该社区可能是独立的社区，先统计了
                totalSeperateCommuNames.add(commuName);

                if(nonOverlapCommu.size() > 7){ //如果独立的社区内的基因数大于7，则该“独立”社区也具有一定的生物学意义，值得分析
                    meaningfulSeperateCommuNum++;
                    meaningfulSeparateCommuNames.add(commuName);
                }
            }else{
                //有重叠节点被划进了commuName的社区
                totalOverlapTagSet.add(commuName);
                Set<String> overlapCommunities = new HashSet<String>();
                overlapCommunities.add(commuName);
                MyPrint.print("社区标志"+commuName+" 的社区 吸入重叠节点："+(overlapCommu.size()-nonOverlapCommu.size())+"个");
                MyPrint.print("社区标志"+commuName+"非重叠时大小="+nonOverlapCommu.size()+"; 重叠下大小="+overlapCommu.size());
                MyPrint.print("---------------------------------------");
                if(nonOverlapCommu.size() > 5 && overlapCommu.size() > 7){//若吸入节点之前 commuName的社区大小 > 5，吸入节点之后名为commuName的重叠社区大小 > 7，则认为该commuName对应的社区是有意义的
                    meaningfullOverlapCommuNames.add(commuName);
                }


                //莫要忽略了一点：被“吸入”的节点v，其原来所属社区c也就成了重叠社区，若c没有吸收新的节点，则在上面的统计中体现不出来社区c
                //因此还要找出来这些 “被吸入”的重叠节点原来所属社区，看是否同样值得分析
                List<String> overlapNodes = MyUtils.getOverlapNodes(overlapCommu,nonOverlapCommu);
                for(String node: overlapNodes){
                    List<String> commuTags = overlapPartition.getNodeMapCommunities().get(node);
                    //遍历节点node所属的多个社区，筛选出不属于当前社区commuName的 社区大小 > 7的社区
                    for(String tag: commuTags){
                        if(!tag.equals(commuName)){//即该tag的社区不是当前社区，
                            if(bestNonOverlapPartition.getCommunities().get(tag).size() > 7) {//且大小 > 7，该社区是有意义的重叠社区之一
                                meaningfullOverlapCommuNames.add(tag);
                            }
                            totalOverlapTagSet.add(tag);

                        }
                    }
                }
            }
        }
        MyPrint.print("总的重叠社区个数 = "+totalOverlapTagSet.size()+"；其中 "+meaningfullOverlapCommuNames.size()+"个有意义");
        MyPrint.print("有意义的重叠社区，社区名如下--------------------------");
        for(String e :meaningfullOverlapCommuNames){
            MyPrint.print(e);
        }
        MyPrint.print("-----------------------------");
        //此时找到的totalSeperateCommuNames 与 totalOverlapTagSet可能存在交集，交集中的社区名应该是重叠的，因此要找到交集，这些交集不再是独立社区了，因此要从totalSeperateCommuNames与meaningfulSeperateCommuNum中移除交集元素
        Set<String> intersection = getInterSectionOf2Set(totalSeperateCommuNames,totalOverlapTagSet);
        for(String e:intersection){
            totalSeperateCommuNames.remove(e);
            meaningfulSeparateCommuNames.remove(e);
        }


        MyPrint.print("独立社区 总数 = "+totalSeperateCommuNames.size());
        MyPrint.print("有意义的独立社区：“独立的”且所包含的基因个数 > 7的社区 "+meaningfulSeperateCommuNum+" 个");


        //将有意义的重叠社区、 有意义的独立社区按格式打印到文本文件中
//        saveMeaningfulComuunities(overlapPartition,meaningfullOverlapCommuNames,meaningfulSeparateCommuNames);
//        saveTotalOverlapCommunities(overlapPartition,totalOverlapTagSet);
        findTheSourceOfNewBigOverlapNodes(overlapPartition,totalOverlapTagSet);
        findIneractionBetweenTwoverlapCommunities(overlapPartition,totalOverlapTagSet);
    }

    /**
     * 判断原来只有一个节点的社区在 吸收了很多节点之后而成大重叠社区，看这些被吸收的节点来自哪里
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
                MyPrint.print("1297个节点的大社区Gma.1043.2.S1_at包含社区 "+tag+"中的 "+num+" 个节点");
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
                    MyPrint.print("社区标志" + totalOverlapCommunityNames.get(i) + " 与社区标志" + totalOverlapCommunityNames.get(j) + " 之间的重叠节点个数=" + interactionOfCommunityIAndJ.size());
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
            sb.append("本文件中的社区是有意义的独立社区，即“独立的无重叠节点的”且所包含的基因个数 > 7的社区 \n");
            bw1.write(sb.toString());
            sb = new StringBuffer();
            sb.append("有意义的社区个数 ="+meaningfulSeperateCommuNames.size()+"\n");
            bw1.newLine();
            bw1.write(sb.toString());

            sb = new StringBuffer();
            sb.append("本文件中是真正的重叠社区 \n");
            bw2.write(sb.toString());
            sb = new StringBuffer();
            sb.append("其中有意义的重叠社区个数 = "+meaningfullOverlapCommuNames.size()+"\n");
            bw2.newLine();
            bw2.write(sb.toString());
            sb = new StringBuffer();
            sb.append("若吸入节点之前 commuName的社区大小 > 5，吸入节点之后名为commuName的重叠社区大小 > 7，则认为该commuName对应的社区是有意义的");
            bw2.newLine();
            bw2.write(sb.toString());
            sb = new StringBuffer();
            sb.append("那些原本只有1个或很少节点的社区，经过识别重叠节点，其社区大小增长很大；如原来只有1个节点，最后增长到了1297的社区，原来仅有的1个节点基因可能有生物学意义");
            bw2.newLine();
            bw2.write(sb.toString());
            Iterator it = overlapPartition.getCommunities().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>)it.next();
                String commuName = entry.getKey();
                if(meaningfulSeperateCommuNames.contains(commuName)){
                    //该社区是有意义的独立社区，“独立的无重叠节点的”且所包含的基因个数 > 7的社区
                    bw1.newLine();
                    bw1.write("-----------------------------------------------------------------------------");
                    bw1.newLine();
                    bw1.write("社区名： "+commuName+"包含"+overlapPartition.getCommunities().get(commuName).size()+" 个基因");
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
                    bw2.write("社区名： "+commuName+"包含"+overlapPartition.getCommunities().get(commuName).size()+" 个基因");
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
            sb.append("本文件中是全部的重叠社区，个数 = "+totalOverlapTagSet.size());
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
                    bw.write("社区名： "+commuName+"包含"+overlapPartition.getCommunities().get(commuName).size()+" 个基因");
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
