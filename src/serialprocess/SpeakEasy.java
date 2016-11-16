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
	 * 全局变量
	 * 定义节点的缓冲池的大小为常量
	 */
	
	//初始化各个节点的historical label buffer中的第一个标签
	public void initializeLabelBuffer(Graph g,VertexNode v){
		int bufferSize=g.bufferSize;
		v.labelBuffer.add(v.vertexName);
//		System.out.println("第一步初始化节点："+v.vertexName);
	}
	//初始化节的historical label buffer中的后bufferSize-1个标签
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
			
		System.out.println("labelbuffer初始化完毕"+v.labelBuffer);
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
	 * @param globalFrequency 形参，接受main方法中定义的全局globalFrequencies的引用
	 * 空返回值，通过改变引用globalFrequency所指的map结构达到返回值的效果。
	 */
	public void calcuGlobalFrequency(Graph g,VertexNode v,Map<String, Double>globalFrequency){
		
		int totalLabelNum=getTotalNumLabels(g);//先计算当前时刻全局标签数量
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
		System.out.println("全局概率分布计算完毕");
	}
	
	/**
	 * 计算一个节点更新时应该采取的标签，即mostUnexpectedLabel,并更新其缓冲池
	 * @param g
	 * @param v
	 * @param globalFrequency
	 */
	public void determineAndAlterLabel(Graph g,VertexNode v,Map<String, Double>globalFrequency){
		double maxSpecity=0.0d;
		String specifyLabel="";
		int bufferSize=g.bufferSize;
		Map<String, Integer>actualLabel=new HashMap<String, Integer>();
		for(int actIndex=0;actIndex < v.neighborList.size();actIndex++){//外层循环，遍历所有邻居节点
			String nbName=v.neighborList.get(actIndex);
			VertexNode neighbor=g.map.get(nbName);
			
			for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){//内层循环，遍历每个邻居节点的buffer
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
		//所有邻居节点中的buffer中的标签总数量
		int actualLabelNum=v.neighborList.size()*bufferSize;
		Iterator it=actualLabel.keySet().iterator();
		while(it.hasNext()){
			String key=(String)it.next();//actualLabel中的标签
			
			System.out.println("label which v can get from its neibors:"+key);
			System.out.println("标签的全局概率:"+globalFrequency.get(key));
			System.out.println("标签的实际数量:"+actualLabelNum);
			
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
		System.out.println("更新前："+v.labelBuffer);
		v.labelBuffer.remove(0);
		v.labelBuffer.add(specifyLabel);
		System.out.println("更新后："+v.labelBuffer);
		System.out.println("节点"+v.vertexName+"buffer中有："+v.labelBuffer.size());
	}
	
}
