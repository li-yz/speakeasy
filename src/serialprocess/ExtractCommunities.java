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
		if(v.neighborList.size() >0){	//�ڵ�v���ǹ����ڵ�
			for(int nbIndex=0;nbIndex < v.neighborList.size();nbIndex++){//���ѭ�������������ھӽڵ�
				String nbName=v.neighborList.get(nbIndex);
				VertexNode neighbor=g.map.get(nbName);
				
				for(int bufferIndex=0;bufferIndex < bufferSize;bufferIndex++){//�ڲ�ѭ��������ÿ���ھӽڵ��buffer
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
		
			//maxLabel���ǽڵ�vӦ�����ڵ������ı�־���������������Ѵ��ڣ���ѽڵ�v���룬�����½�����
			if(partition.communities.containsKey(maxLabel)){
				List<String> nodesList=partition.communities.get(maxLabel);
				nodesList.add(v.vertexName);
				partition.communities.put(maxLabel, nodesList);//�Ը��ǵķ�ʽput��ȥ
			}else{
				List<String> nodeList=new ArrayList<String>();
				nodeList.add(v.vertexName);
				partition.communities.put(maxLabel, nodeList);
			}
			//����ϢͬʱҲ���浽  �ڵ�-������map������
			partition.nodeCommunityMap.put(v.vertexName, maxLabel);
		}else{	
			//�ڵ�v�ǹ����ڵ㣬û�������ڵ����������������Ľڵ��Լ���һ������
			List<String> nodelist=new ArrayList<String>();
			nodelist.add(v.vertexName);
			partition.communities.put(v.vertexName,nodelist );
			partition.nodeCommunityMap.put(v.vertexName, v.vertexName);
		}
	}
}
