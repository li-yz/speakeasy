package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 跑过speakeasy算法，对社区分析，分析结果输出工具类
 * Created by Liyanzhen on 2017/1/3.
 */
public class CommunityAnalysisResultOutput {
    //保存社区的大小分布情况
    public static void outputCommunitiesDistribution(List<Integer> distrbution,String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(writer);

        StringBuffer sb=new StringBuffer();
        sb.append("社区大小分布情况： ");
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
