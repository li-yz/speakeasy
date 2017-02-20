package test;

import postprocess.ConvertAndSaveOverlapResultToMatrix;
import utils.MyPrint;

/**
 * Created by Liyanzhen on 2017/2/18.
 */
public class CalculateEQWithDataInMatrixStyle {
    public static void main(String[] args){
        ConvertAndSaveOverlapResultToMatrix obj = new ConvertAndSaveOverlapResultToMatrix();
        int A[][] = obj.getMatrixA();
        int[][] communities = obj.getOverlapCommunities();
        calculateEQ(A, communities);
    }

    public static double calculateEQ(int[][] A, int[][] communities) {
        int c = communities.length;//社区总数
        int n=communities[0].length;//节点总数
        int m=0;//边数
        for(int i = 0; i < A.length; i++){
            for(int j = 0; j < A[0].length; j++){
                m+= A[i][j];
            }
        }
        m=m/2;

        int[] o = new int[n];//每个节点归属于几个社区
        for(int j=0;j < n;j++){
            for(int i=0;i < c;i++){
                o[j] = o[j]+communities[i][j];
            }
        }

        int[] k=new int[n];
        for(int i=0;i < n;i++){
            for(int j=0;j < n;j++){
                k[i] = k[i]+ A[i][j];
            }
        }
        double eq=0.0d;
        for(int i=0;i < c;i++){
            for(int v=0; v < n;v++){
                if(communities[i][v] != 1)
                    continue;;
                for(int w=v+1;w < n;w++){
                    if(communities[i][w] != 1){
                        continue;
                    }
                    double s1 = (double)1/(o[v]*o[w]);
                    double s2 = (double)(k[v]*k[w])/(2*m);
                    eq = eq + s1*(A[v][w] - s2);
                }
            }
        }
        MyPrint.print(":"+eq);
        eq = eq/(2*m);

        MyPrint.print("eq = "+eq);
        return eq;
    }
}
