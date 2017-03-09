package serialprocess;

import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

/**
 *
 */
public class SpeakEasy {
	/**
	 * ȫ�ֱ���
	 * ����ڵ�Ļ���صĴ�СΪ����
	 */
	
	//��ʼ�������ڵ��historical label buffer�еĵ�һ����ǩ
	public void initializeLabelBuffer(Graph g,VertexNode v){
		v.labelBuffer.add(v.vertexName);
	}
	//��ʼ���ڵ��historical label buffer�еĺ�bufferSize-1����ǩ
	public void fillLabelBuffer(Graph g,VertexNode v){
		int numNeighbor=v.neighborList.size();
		int bufferSize=g.bufferSize;
		
		for(int m=0;m <bufferSize-1;m++){
			if(numNeighbor > 0){
				Random rand=new Random();
				int c=rand.nextInt(numNeighbor);
				List<String>nbNamesList = new ArrayList<String>();
				for(Map.Entry entry :v.neighborList.entrySet()){
					nbNamesList.add((String) entry.getKey());
				}
				String label=nbNamesList.get(c);
				v.labelBuffer.add(label);
			}else if(numNeighbor == 0){
				v.labelBuffer.add(v.vertexName);
			}
		}
			
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
	 */
	public void calcuGlobalFrequency(Graph g,VertexNode v){
		
		int totalLabelNum=getTotalNumLabels(g);//�ȼ��㵱ǰʱ��ȫ�ֱ�ǩ����
		int bufferSize=g.bufferSize;
		
		for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){
			String label=v.labelBuffer.get(bufferIndex);
			if(g.globalFrequencies.containsKey(label)){
				double value=g.globalFrequencies.get(label)+(double)1/totalLabelNum;
				g.globalFrequencies.put(label, value);
			}else{
				double value=(double)1/totalLabelNum;
				g.globalFrequencies.put(label, value);
			}
		}
	}
	
	/**
	 * ����һ���ڵ����ʱӦ�ò�ȡ�ı�ǩ����mostUnexpectedLabel,�������仺���
	 * @param g
	 * @param v
	 */
	public void determineAndAlterLabel(Graph g,VertexNode v){
		double maxSpecity=0.0d;
		String specifyLabel="";
		int bufferSize=g.bufferSize;
		Map<String, Double>actualLabel=new HashMap<String, Double>();
		Iterator nbIter = v.neighborList.entrySet().iterator();

		while (nbIter.hasNext()){//���ѭ�������������ھӽڵ�
			Map.Entry<String,Double> entry = (Map.Entry<String,Double>)nbIter.next();
			String nbName=entry.getKey();
			double weight = entry.getValue();
			VertexNode neighbor=g.map.get(nbName);
			
			for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){//�ڲ�ѭ��������ÿ���ھӽڵ��buffer
				String label=neighbor.labelBuffer.get(bufferIndex);
				if(actualLabel.containsKey(label)){
					double value=actualLabel.get(label)+weight;
					actualLabel.put(label, value);
				}else{
					double value=weight;
					actualLabel.put(label, value);
				}
			}
		}
		//�����ھӽڵ��е�buffer�еı�ǩ������
		int actualLabelNum=v.neighborList.size()*bufferSize;
		Iterator it=actualLabel.keySet().iterator();
		while(it.hasNext()){
			String key=(String)it.next();//actualLabel�еı�ǩ
			
			double expectLabelNum=0.0d;
			if(g.globalFrequencies.get(key) != null){
				expectLabelNum=g.globalFrequencies.get(key)*actualLabelNum;
			}
			double specity=actualLabel.get(key)-expectLabelNum;
			if(specity >maxSpecity){
				maxSpecity=specity;
				specifyLabel=key;
			}
		}
		v.labelBuffer.remove(0);
		v.labelBuffer.add(specifyLabel);
	}
	
}
