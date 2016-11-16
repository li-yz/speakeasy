package serialprocess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class SpeakEasy {
	/**
	 * ȫ�ֱ���
	 * ����ڵ�Ļ���صĴ�СΪ����
	 */
	
	//��ʼ�������ڵ��historical label buffer�еĵ�һ����ǩ
	public void initializeLabelBuffer(Graph g,VertexNode v){
		int bufferSize=g.bufferSize;
		v.labelBuffer.add(v.vertexName);
//		System.out.println("��һ����ʼ���ڵ㣺"+v.vertexName);
	}
	//��ʼ���ڵ�historical label buffer�еĺ�bufferSize-1����ǩ
	public void fillLabelBuffer(Graph g,VertexNode v){
		int numNeighbor=v.neighborList.size();
		int bufferSize=g.bufferSize;
		
		for(int m=0;m <bufferSize-1;m++){
			if(numNeighbor > 0){
				Random rand=new Random();
				int c=rand.nextInt(numNeighbor);
				String label=v.neighborList.get(c);
				v.labelBuffer.add(label);
			}else if(numNeighbor == 0){
				v.labelBuffer.add(v.vertexName);
			}
		}
			
		System.out.println("labelbuffer��ʼ�����"+v.labelBuffer);
	}
	
	public int getTotalNumLabels(Graph g){
		int totalNum=0;
		totalNum=g.bufferSize*g.map.size();
		return totalNum;	
	}
	
	/**
	 * �������б�ǩ��ȫ�ָ���
	 * @param g ͼg
	 * @param v ��ǰ�������Ľڵ�
	 * @param globalFrequency �βΣ�����main�����ж����ȫ��globalFrequencies������
	 * �շ���ֵ��ͨ���ı�����globalFrequency��ָ��map�ṹ�ﵽ����ֵ��Ч����
	 */
	public void calcuGlobalFrequency(Graph g,VertexNode v,Map<String, Double>globalFrequency){
		
		int totalLabelNum=getTotalNumLabels(g);//�ȼ��㵱ǰʱ��ȫ�ֱ�ǩ����
		int bufferSize=g.bufferSize;
		
		for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){
			String label=v.labelBuffer.get(bufferIndex);
			if(globalFrequency.containsKey(label)){
				double value=globalFrequency.get(label)+(double)1/totalLabelNum;
				globalFrequency.put(label, value);
			}else{
				double value=(double)1/totalLabelNum;
				globalFrequency.put(label, value);
			}
		}
		System.out.println("ȫ�ָ��ʷֲ��������");
	}
	
	/**
	 * ����һ���ڵ����ʱӦ�ò�ȡ�ı�ǩ����mostUnexpectedLabel,�������仺���
	 * @param g
	 * @param v
	 * @param globalFrequency
	 */
	public void determineAndAlterLabel(Graph g,VertexNode v,Map<String, Double>globalFrequency){
		double maxSpecity=0.0d;
		String specifyLabel="";
		int bufferSize=g.bufferSize;
		Map<String, Integer>actualLabel=new HashMap<String, Integer>();
		for(int actIndex=0;actIndex < v.neighborList.size();actIndex++){//���ѭ�������������ھӽڵ�
			String nbName=v.neighborList.get(actIndex);
			VertexNode neighbor=g.map.get(nbName);
			
			for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){//�ڲ�ѭ��������ÿ���ھӽڵ��buffer
				String label=neighbor.labelBuffer.get(bufferIndex);
				if(actualLabel.containsKey(label)){
					int value=actualLabel.get(label)+1;
					actualLabel.put(label, value);
				}else{
					int value=1;
					actualLabel.put(label, value);
				}
			}
		}
		//�����ھӽڵ��е�buffer�еı�ǩ������
		int actualLabelNum=v.neighborList.size()*bufferSize;
		Iterator it=actualLabel.keySet().iterator();
		while(it.hasNext()){
			String key=(String)it.next();//actualLabel�еı�ǩ
			
			System.out.println("label which v can get from its neibors:"+key);
			System.out.println("��ǩ��ȫ�ָ���:"+globalFrequency.get(key));
			System.out.println("��ǩ��ʵ������:"+actualLabelNum);
			
			double expectLabelNum=0.0d;
			if(globalFrequency.get(key) != null){
				expectLabelNum=globalFrequency.get(key)*actualLabelNum;
			}
			double specity=actualLabel.get(key)-expectLabelNum;
			if(specity >maxSpecity){
				maxSpecity=specity;
				specifyLabel=key;
			}
		}
		System.out.println("����ǰ��"+v.labelBuffer);
		v.labelBuffer.remove(0);
		v.labelBuffer.add(specifyLabel);
		System.out.println("���º�"+v.labelBuffer);
		System.out.println("�ڵ�"+v.vertexName+"buffer���У�"+v.labelBuffer.size());
	}
	
}
