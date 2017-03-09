package test;

import postprocess.ConvertAndSaveOverlapResultToMatrix;
import serialprocess.Graph;
import serialprocess.OverlapPartition;
import utils.MySerialization;

/**
 * Created by Liyanzhen on 2017/2/18.
 */
public class AnalysisGANXiSwOverlapResultEQ {
    public static void main(String[] args){
        String GANXisWCommunitiesPath = "E:\\毕业论文\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\weightedsoybean\\SLPAw_weighted-soybean-network_run1_r0.1_v3_T50.icpm";
        String GANXiSwOverlapNodesPath = "E:\\毕业论文\\GANXiS_v3.0.2\\GANXiS_v3.0.2\\output\\weightedsoybean\\SLPAw_weighted-soybean-network_run1_r0.1_v3_T50.icpm.ovnodes.txt";

        OverlapPartition GANXiSwOverlapPartition = AnalysisGANXiSwResult.readOverlapPartition(GANXisWCommunitiesPath,GANXiSwOverlapNodesPath);

        Graph g = (Graph) MySerialization.antiSerializeObject("D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");
        ConvertAndSaveOverlapResultToMatrix obj = new ConvertAndSaveOverlapResultToMatrix();
        int[][]A = obj.convertGraphToMatrixA(g);
        MySerialization.serializeObject(A,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\A.obj");
        obj.setupCommunityNameMapIndex(GANXiSwOverlapPartition);
        int[][]communities = obj.convertOverlapPartitionToMatrix(g,GANXiSwOverlapPartition);
        MySerialization.serializeObject(communities,"D:\\paperdata\\soybean\\community detection\\GraphAndResultOfMatrixStyle\\overlapCommunities.obj");
        A = obj.getMatrixA();

        double eq = CalculateEQWithDataInMatrixStyle.calculateEQ(A,communities);
    }
}
