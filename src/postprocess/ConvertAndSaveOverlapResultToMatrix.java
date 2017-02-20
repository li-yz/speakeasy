package postprocess;

import serialprocess.Graph;
import serialprocess.OverlapPartition;
import serialprocess.VertexNode;
import utils.MyPrint;
import utils.MySerialization;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ������ͼg���ڽӾ���A����ʾ���õ��� OverlapPartion��ʽ�Ľ��Ҳ�þ���Communities������������ʹ��matlab���м��㣬
 * Created by Liyanzhen on 2017/2/17.
 */
public class ConvertAndSaveOverlapResultToMatrix {
    public static void main(String[] args){
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

        ConvertAndSaveOverlapResultToMatrix obj = new ConvertAndSaveOverlapResultToMatrix();
        int[][]A = obj.convertGraphToMatrixA(g);
        MySerialization.serializeObject(A,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\A.obj");

        obj.setupCommunityNameMapIndex(overlapPartition);
        int[][]Communities = obj.convertOverlapPartitionToMatrix(g,overlapPartition);

        MySerialization.serializeObject(Communities,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\overlapCommunities.obj");
        validate(obj.getOverlapCommunities());
    }
    public int[][] getMatrixA(){
        int[][] A = (int[][])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\A.obj");
        return A;
    }

    public int[][] convertGraphToMatrixA(Graph g){
        int m=g.map.size();//�ڵ�����
        TreeMap<Integer,String> indexMapName = new TreeMap<Integer, String>();
        TreeMap<String,Integer> nameMapIndex = new TreeMap<String, Integer>();
        int[][] A = new int[m][m];
        //��һ�����Ƚ��� ���-�ڵ��� ��Ӧ��ϵ���������ֶ�Ӧ��ϵ���л�
        Iterator iterator = g.map.entrySet().iterator();
        int index=0;
        while(iterator.hasNext()){
            Map.Entry<String, VertexNode> entry =(Map.Entry<String, VertexNode>) iterator.next();
            String vname = entry.getKey();
            indexMapName.put(index,vname);
            nameMapIndex.put(vname,index);
            index++;
        }
        //���л��ڵ����-�ڵ��� ��Ӧ��ϵ;���ͻ�����Ϣ��ʱ����Ҫͨ��������ҵ� ����������Ҫע�⣬matlab�о����±��1��ʼ
        MySerialization.serializeObject(indexMapName,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\nodeIndexMapNodeName.obj");
        MySerialization.serializeObject(nameMapIndex,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\nodeNameMapNodeIndex.obj");

        //�ڶ�����ת�������ڽӾ���
        Iterator it = g.map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, VertexNode> entry =(Map.Entry<String, VertexNode>) it.next();
            String vname = entry.getKey();
            int vi = nameMapIndex.get(vname);
            List<String> neighbors = g.map.get(vname).neighborList;
            for(String nodej: neighbors){
                int vj = nameMapIndex.get(nodej);
                A[vi][vj] = 1;
                A[vj][vi] = 1;
            }
        }

        //����ת���Ƿ���ȷ�����ڽӾ���A���Ƿ��� �ܱ���*2��1
        int total1=0;
        for(int i=0;i < m;i++){
            for(int j=0;j < m;j++){
                total1+=A[i][j];
            }
        }
        if(total1 == 2*g.totalEdgesList.size()){
            MyPrint.print("ת�����ڽӾ����ʾ��success!!!");
        }
        return A;
    }


    public void setupCommunityNameMapIndex(OverlapPartition overlapPartition){
        Map<String,List<String>> communities = overlapPartition.getCommunities();

        Map<String,Integer> commuNameMapIndex = new TreeMap<String, Integer>();
        Map<Integer,String> communityIndexMapName = new TreeMap<Integer, String>();

        Iterator it = communities.entrySet().iterator();
        int index =0;
        while(it.hasNext()){
            Map.Entry<String,List<String>> entry =(Map.Entry<String,List<String>>) it.next();
            String commuName = entry.getKey();
            commuNameMapIndex.put(commuName,index);
            communityIndexMapName.put(index,commuName);
            index++;
        }
        //���л��������-������־�� ��Ӧ��ϵ;���ͻ�����Ϣ��ʱ����Ҫͨ��������ҵ� ����������Ҫע�⣬matlab�о����±��1��ʼ
        MySerialization.serializeObject(commuNameMapIndex,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\communityNameMapIndex.obj");
        MySerialization.serializeObject(communityIndexMapName,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\communityIndexMapCommunityName.obj");
    }

    public int[][] convertOverlapPartitionToMatrix(Graph g,OverlapPartition overlapPartition){
        Map<String,Integer> commuNameMapIndex = (Map<String,Integer>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\communityNameMapIndex.obj");
        Map<String,Integer> nodeNameMapIndex = (Map<String,Integer>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\nodeNameMapNodeIndex.obj");

        Map<String,List<String>> commnities = overlapPartition.getCommunities();
        int m = g.map.size();//�ڵ�����
        int c = commnities.size();//��������
        int[][] Communities = new int[c][m];

        Iterator iterator = commnities.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,List<String>> entry =(Map.Entry<String,List<String>>) iterator.next();
            String communityName = entry.getKey();
            List<String> nodesInCommunity = entry.getValue();
            int communityIndex = commuNameMapIndex.get(communityName);
            for(String nodeName:nodesInCommunity){
                int nodeIndex = nodeNameMapIndex.get(nodeName);
                Communities[communityIndex][nodeIndex] = 1;
            }
        }

        return Communities;
    }

    /**
     * ���� �þ�����ʽ��ʾ���ص��������ֽ��
     * @return
     */
    public int[][] getOverlapCommunities(){
        int[][] communities = (int[][])MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\overlapCommunities.obj");
        return communities;
    }

    private static void validate(int[][]communities){
        for(int i=0;i < communities.length;i++){
            int sum=0;
            for(int j=0;j < communities[0].length;j++){
                sum+=communities[i][j];
            }
            MyPrint.print(sum+"");
        }
    }
}
