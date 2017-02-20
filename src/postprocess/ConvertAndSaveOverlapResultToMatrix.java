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
 * 把网络图g用邻接矩阵A来表示，得到的 OverlapPartion形式的结果也用矩阵Communities来描述，便于使用matlab进行计算，
 * Created by Liyanzhen on 2017/2/17.
 */
public class ConvertAndSaveOverlapResultToMatrix {
    public static void main(String[] args){
        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        OverlapPartition overlapPartition = (OverlapPartition) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

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
        int m=g.map.size();//节点总数
        TreeMap<Integer,String> indexMapName = new TreeMap<Integer, String>();
        TreeMap<String,Integer> nameMapIndex = new TreeMap<String, Integer>();
        int[][] A = new int[m][m];
        //第一步：先建立 序号-节点名 对应关系，并将这种对应关系序列化
        Iterator iterator = g.map.entrySet().iterator();
        int index=0;
        while(iterator.hasNext()){
            Map.Entry<String, VertexNode> entry =(Map.Entry<String, VertexNode>) iterator.next();
            String vname = entry.getKey();
            indexMapName.put(index,vname);
            nameMapIndex.put(vname,index);
            index++;
        }
        //序列化节点序号-节点名 对应关系;解释基因信息的时候需要通过序号来找到 基因名，并要注意，matlab中矩阵下标从1开始
        MySerialization.serializeObject(indexMapName,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\nodeIndexMapNodeName.obj");
        MySerialization.serializeObject(nameMapIndex,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\nodeNameMapNodeIndex.obj");

        //第二步：转换生成邻接矩阵
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

        //检验转换是否正确，即邻接矩阵A中是否有 总边数*2个1
        int total1=0;
        for(int i=0;i < m;i++){
            for(int j=0;j < m;j++){
                total1+=A[i][j];
            }
        }
        if(total1 == 2*g.totalEdgesList.size()){
            MyPrint.print("转换成邻接矩阵表示，success!!!");
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
        //序列化社区序号-社区标志名 对应关系;解释基因信息的时候需要通过序号来找到 基因名，并要注意，matlab中矩阵下标从1开始
        MySerialization.serializeObject(commuNameMapIndex,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\communityNameMapIndex.obj");
        MySerialization.serializeObject(communityIndexMapName,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\communityIndexMapCommunityName.obj");
    }

    public int[][] convertOverlapPartitionToMatrix(Graph g,OverlapPartition overlapPartition){
        Map<String,Integer> commuNameMapIndex = (Map<String,Integer>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\communityNameMapIndex.obj");
        Map<String,Integer> nodeNameMapIndex = (Map<String,Integer>)MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\nodeNameMapNodeIndex.obj");

        Map<String,List<String>> commnities = overlapPartition.getCommunities();
        int m = g.map.size();//节点总数
        int c = commnities.size();//社区总数
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
     * 返回 用矩阵形式表示的重叠社区划分结果
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
