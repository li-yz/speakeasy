package postprocess;

import serialprocess.BasePartition;
import serialprocess.Graph;
import serialprocess.OverlapPartition;
import utils.MyPrint;
import utils.MySerialization;

import java.util.*;

/**
 * �������ⲿ����ָ�꣺��һ������Ϣ Normalized Mutual Information
 *
 * ���㹫ʽ�ο���http://nlp.stanford.edu/IR-book/html/htmledition/evaluation-of-clustering-1.html
 * Created by Liyanzhen on 2017/1/13.
 */
public class NMI {
    /**
     * ���������������ֵ�NMI��NMI��ʾ�������ֵ����ƶ�
     * @param speakPartition
     * @param realPartition
     * @param n ����ͼ�нڵ�����
     * @return
     */
    public static double getNMIValue(BasePartition speakPartition, BasePartition realPartition,int n){
        double nmi = 0.0d;
        double mi = getMIValue(speakPartition,realPartition,n);
        double Hw = getEntropyH(speakPartition,n);
        double Hc = getEntropyH(realPartition,n);

        nmi = (2*mi)/(Hw+Hc);
        MyPrint.print("����ϢMIֵ= "+mi+"Hw = "+Hw+"Hc = "+Hc);
        MyPrint.print("��һ������Ϣ NMI = "+nmi);
        return nmi;
    }

    /**
     * �ȼ��㻥��ϢMIֵ
     * @param speakPartition
     * @param realPartition
     * @param n �ڵ�����
     * @return
     */
    private static double getMIValue(BasePartition speakPartition, BasePartition realPartition,int n){
        double mi = 0.0d;
        //���㻥��Ϣ

        Iterator wIter = speakPartition.getCommunities().entrySet().iterator();
        while(wIter.hasNext()){
            List<String> wk=((Map.Entry<String,List<String>>)wIter.next()).getValue();
            Iterator cIter = realPartition.getCommunities().entrySet().iterator();
            while (cIter.hasNext()){
                List<String> cj = ((Map.Entry<String,List<String>>)cIter.next()).getValue();
                int wk_cj =getIntersectionNumOf2Sets(wk,cj);
                int numWk=wk.size();
                int numCj=cj.size();
                if(wk_cj != 0) {
                    double temp = (double) wk_cj / n;
                    double logTemp = Math.log((double) (n * wk_cj) / (numWk * numCj));
                    mi += temp*logTemp;
                }
            }
        }

        return mi;
    }

    private static double getEntropyH(BasePartition partition,int n){
        double h=0.0d;
        Iterator iter = partition.getCommunities().entrySet().iterator();
        while(iter.hasNext()){
            List<String> wk = ((Map.Entry<String,List<String>>)iter.next()).getValue();
            int numWk = wk.size();
            double temp = (double)numWk/n;
            h+=temp*Math.log(temp);
        }
        return 0-h;
    }

    /**
     * ��ȡ����list�Ľ����е�Ԫ�ظ���
     * @param list1
     * @param list2
     * @return
     */
    private static int getIntersectionNumOf2Sets(List<String>list1,List<String>list2){
        int num =0;
        Set<String>result = new HashSet<String>();
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        for(String v:list1){
            set1.add(v);
        }
        for(String v:list2){
            set2.add(v);
        }

        result.clear();
        result.addAll(set1);
        result.retainAll(set2);

        num = result.size();
        return num;
    }
}
