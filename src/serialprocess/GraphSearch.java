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
	public void communityDetectProcedure(Graph g) {
		System.out.println("�鿴������"+g.totalEdgesList.size());
		System.out.println("�鿴�ڵ�����"+g.map.size());//ע����ͼ���й����Ľڵ�
		
		/**
		 * ȫ�ֱ�������ʼ���Ժ�����ͼ���ܵı�ǩ��
		 * @param v
		 */
		int totalLabelNum=0;
	//repeat;
		
		//����SpeakEasy�㷨�Ĳ����ͼ���б�����
		//�����Ĺ���������Ӧ�Ĵ�����ʼ���ڵ��buffer��������0��1��������ȫ�ָ��ʷֲ�2��ȷ��Ҫ���µı�ǩ3��
		for(int funcIndex=0;funcIndex < 4;funcIndex++){
			g.BFSTraverse(g,funcIndex);
			
			//ע��û����һ��֮�����нڵ㶼�����ʹ��ˣ�Ϊ���ٽ�����������Ҫ�Ѵ���ѷ��ʽڵ�ļ������
			g.visited.clear();
		}
		//������ǩ���µĹ��̣�ִ�ж�Σ�ֱ���ڵ��buffer�����������仯��������
		for(int repeatTime=0;repeatTime <2;repeatTime++){
			g.globalFrequencies.clear();
			g.BFSTraverse(g,2);
			g.visited.clear();
			g.BFSTraverse(g,3);
			g.visited.clear();
		}
	//repeat����
		
		//ʶ������
		g.BFSTraverse(g, 4);
		g.visited.clear();
		
		//���������ı�
		System.out.println("һ�δ����������");
		ResultOutput ro=new ResultOutput();
		 try {
			ro.outputResult(g);
			ro.outputCommunities(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}



