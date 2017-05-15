package serialprocess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SpeakEasy�㷨�ĵ�һ������������
 * �Խڵ��visit����Ӧ�÷��ڴ����У�����ͼ���еı����㷨��Ȼ��visitÿ���ڵ�
 * @author Administrator
 *
 */
public class GraphSearch {
	public void communityDetectProcedure(Graph g ,Partition partition) {
		System.out.println("һ���������ֵ�������ʼ");

		/**
		 * ȫ�ֱ�������ʼ���Ժ�����ͼ���ܵı�ǩ��
		 * @param v
		 */
	//repeat;
		
		//����SpeakEasy�㷨�Ĳ����ͼ���б�����
		//�����Ĺ���������Ӧ�Ĵ�����ʼ���ڵ��buffer��������0��1��������ȫ�ָ��ʷֲ�2��ȷ��Ҫ���µı�ǩ3��
		for(int funcIndex=0;funcIndex < 4;funcIndex++){
			g.BFSTraverse(g,funcIndex,partition);
			
			//ע��ÿ����һ��֮�����нڵ㶼�����ʹ��ˣ�Ϊ���ٽ�����������Ҫ�Ѵ���ѷ��ʽڵ�ļ������
			g.visited.clear();
		}
		//������ǩ���µĹ��̣�ִ�ж�Σ�ֱ���ڵ��buffer�����������仯��������
		for(int repeatTime=0;repeatTime < 50;repeatTime++){
			g.globalFrequencies.clear();
			g.BFSTraverse(g,2,partition);
			g.visited.clear();
			g.BFSTraverse(g,3,partition);
			g.visited.clear();
		}
	//repeat����
		
		//ʶ������
		g.BFSTraverse(g, 4,partition);
		g.visited.clear();
		
		//�����ǰ����ͼ��״̬���ı�
		ResultOutput ro=new ResultOutput();
		 try {
			ro.outputResult(g);//��������50�κ󣬱��浱ǰ����ͼg��״̬����ÿ���ڵ��buffer�еı�ǩ
			 ro.outputCommunities(g,partition);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("һ���������֣����");
	}
}



