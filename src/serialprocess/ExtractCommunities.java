package serialprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExtractCommunities {
	public void extractCommunity(Graph g,VertexNode v, Partition partition){
		int bufferSize=g.bufferSize;
		Map<String, Integer>labelsFromNbBuffer=new HashMap<String, Integer>();
		if(v.neighborList.size() >0){	//节点v不是孤立节点
			for(int nbIndex=0;nbIndex < v.neighborList.size();nbIndex++){//外层循环，遍历所有邻居节点
				String nbName=v.neighborList.get(nbIndex);
				VertexNode neighbor=g.map.get(nbName);
				
				for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){//内层循环，遍历每个邻居节点的buffer
					String label=neighbor.labelBuffer.get(bufferIndex);
					if(labelsFromNbBuffer.containsKey(label)){
						int value=labelsFromNbBuffer.get(label)+1;
						labelsFromNbBuffer.put(label, value);
					}else{
						int value=1;
						labelsFromNbBuffer.put(label, value);
					}
				}//for
			}//for
		
		
			Iterator it=labelsFromNbBuffer.keySet().iterator();
			int max=0;
			int labelNum=0;
			String maxLabel="";
			while(it.hasNext()){
				String key=(String)it.next();
				labelNum=labelsFromNbBuffer.get(key);
				if(max < labelNum){
					max=labelNum;
					maxLabel=key;
				}
			}
		
			//maxLabel就是节点v应该属于的社区的标志，若这样的社区已存在，则把节点v并入，否则新建并入
			if(partition.communities.containsKey(maxLabel)){
				List<String> nodesList=partition.communities.get(maxLabel);
				nodesList.add(v.vertexName);
				partition.communities.put(maxLabel, nodesList);//以覆盖的方式put进去
			}else{
				List<String> nodeList=new ArrayList<String>();
				nodeList.add(v.vertexName);
				partition.communities.put(maxLabel, nodeList);
			}
			//把信息同时也保存到  节点-社区的map集合中
			partition.nodeCommunityMap.put(v.vertexName, maxLabel);
		}else{	
			//节点v是孤立节点，没有其他节点与其相连，孤立的节点自己成一个社区
			List<String> nodelist=new ArrayList<String>();
			nodelist.add(v.vertexName);
			partition.communities.put(v.vertexName,nodelist );
			partition.nodeCommunityMap.put(v.vertexName, v.vertexName);
		}
	}
}
