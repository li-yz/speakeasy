package utils;

/**
 * Created by Liyanzhen on 2017/3/3.
 */
public class CalculateSimilarity {

    public static double[][] getCosSimilarity(double[][] differExpArrayData) {
        int rowNum = differExpArrayData.length;
        int columNum = differExpArrayData[0].length;

        double [][] cosDistance = new double[rowNum][rowNum];
        //计算两两基因之间的相似度
        for(int i=0;i < rowNum;i++){
            for(int j=i+1;j < rowNum;j++){
                double mm = 0.0d;
                double modei = 0.0d;
                double modej = 0.0d;
                for(int k=0;k < columNum;k++){
                    mm += differExpArrayData[i][k]*differExpArrayData[j][k];
                    modei +=differExpArrayData[i][k]*differExpArrayData[i][k];
                    modej +=differExpArrayData[j][k]*differExpArrayData[j][k];
                }
                cosDistance[i][j] = mm/(Math.sqrt(modei) * Math.sqrt(modej));
            }

        }
        return cosDistance;
    }

    public static double[][] getPearsonSimilarity(double[][] differExpArrayData) {
        int rowNum = differExpArrayData.length;
        int columNum = differExpArrayData[0].length;

        double [][] cosDistance = new double[rowNum][rowNum];
        //计算两两基因之间的相似度
        for(int i=0;i < rowNum;i++){
            for(int j=i+1;j < rowNum;j++){
                double xySum = 0.0d;
                double xSum=0.0d;
                double ySum=0.0d;
                double x2Sum=0.0d;
                double y2Sum=0.0d;
                for(int k=0;k < columNum;k++){
                    xySum+=differExpArrayData[i][k]*differExpArrayData[j][k];
                    xSum+=differExpArrayData[i][k];
                    ySum+=differExpArrayData[j][k];
                    x2Sum+=differExpArrayData[i][k]*differExpArrayData[i][k];
                    y2Sum+=differExpArrayData[j][k]*differExpArrayData[j][k];
                }
                double fenzi = xySum-(xSum*ySum)/columNum;
                double fenmu = Math.sqrt((x2Sum - (xSum*xSum)/columNum)*(y2Sum - (ySum*ySum)/columNum));
                cosDistance[i][j] = fenzi/fenmu;
            }

        }
        return cosDistance;
    }
}
