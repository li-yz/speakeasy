package serialprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExtractCommunities {
	public void extractCommunity(Graph g,VertexNode v){
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
			if(g.Communities.containsKey(maxLabel)){
				List<String> nodesList=g.Communities.get(maxLabel);
				nodesList.add(v.vertexName);
				g.Communities.put(maxLabel, nodesList);//�Ը��ǵķ�ʽput��ȥ
			}else{
				List<String> nodeList=new ArrayList<String>();
				nodeList.add(v.vertexName);
				g.Communities.put(maxLabel, nodeList);
			}
			//����ϢͬʱҲ���浽  �ڵ�-������map������
			g.nodeCommunity.put(v.vertexName, maxLabel);
		}else{	
			//�ڵ�v�ǹ����ڵ㣬û�������ڵ����������������Ľڵ��Լ���һ������
			List<String> nodelist=new ArrayList<String>();
			nodelist.add(v.vertexName);
			g.Communities.put(v.vertexName,nodelist );
			g.nodeCommunity.put(v.vertexName, v.vertexName);
		}
	}
}
