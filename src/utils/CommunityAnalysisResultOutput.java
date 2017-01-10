package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * �ܹ�speakeasy�㷨������������������������������
 * Created by Liyanzhen on 2017/1/3.
 */
public class CommunityAnalysisResultOutput {
    //���������Ĵ�С�ֲ����
    public static void outputCommunitiesDistribution(List<Integer> distrbution,String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(writer);

        StringBuffer sb=new StringBuffer();
        sb.append("������С�ֲ������ ");
        for(int v :distrbution){
            sb.append(v);
            sb.append("\t");
        }
        sb.append("\n");
        bw.write(sb.toString());

        bw.close();
        writer.close();
    }
}
