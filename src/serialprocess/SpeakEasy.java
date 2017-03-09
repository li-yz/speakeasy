package serialprocess;

import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

/**
 *
 */
public class SpeakEasy {
	/**
	 * 全局变量
	 * 定义节点的缓冲池的大小为常量
	 */
	
	//初始化各个节点的historical label buffer中的第一个标签
	public void initializeLabelBuffer(Graph g,VertexNode v){
		v.labelBuffer.add(v.vertexName);
	}
	//初始化节点的historical label buffer中的后bufferSize-1个标签
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
	 * 计算所有标签的全局概率
	 * @param g 图g
	 * @param v 当前遍历到的节点
	 */
	public void calcuGlobalFrequency(Graph g,VertexNode v){
		
		int totalLabelNum=getTotalNumLabels(g);//先计算当前时刻全局标签数量
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
	 * 计算一个节点更新时应该采取的标签，即mostUnexpectedLabel,并更新其缓冲池
	 * @param g
	 * @param v
	 */
	public void determineAndAlterLabel(Graph g,VertexNode v){
		double maxSpecity=0.0d;
		String specifyLabel="";
		int bufferSize=g.bufferSize;
		Map<String, Double>actualLabel=new HashMap<String, Double>();
		Iterator nbIter = v.neighborList.entrySet().iterator();

		while (nbIter.hasNext()){//外层循环，遍历所有邻居节点
			Map.Entry<String,Double> entry = (Map.Entry<String,Double>)nbIter.next();
			String nbName=entry.getKey();
			double weight = entry.getValue();
			VertexNode neighbor=g.map.get(nbName);
			
			for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){//内层循环，遍历每个邻居节点的buffer
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
		//所有邻居节点中的buffer中的标签总数量
		int actualLabelNum=v.neighborList.size()*bufferSize;
		Iterator it=actualLabel.keySet().iterator();
		while(it.hasNext()){
			String key=(String)it.next();//actualLabel中的标签
			
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
