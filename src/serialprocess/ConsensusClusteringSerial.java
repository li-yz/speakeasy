package serialprocess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConsensusClusteringSerial {
	
	//Partition��nodeCommunityMap���Ա�ʾһ����������
	Map<String, List<String>> Partition=new HashMap<String, List<String>>();
	Map<String, String> nodeCommunityMap=new HashMap<String, String>();
	
	public static void main(String[] args){
//		Graph g=new Graph(1000, 105,5,"D:\\liyanzhen\\edges.txt");// dataset without repeat edges
		Graph g=new Graph(5,"D:\\paperdata\\soybean\\community detection\\input network\\genesNetworkOfDistanceThreshold5.txt");
		int n=g.map.size();
		ResultOutput tempRo=new ResultOutput();
		
		List<ConsensusClusteringSerial> ccList=new ArrayList<ConsensusClusteringSerial>();
		
		//��������A�Ķ����ʾ
		CooccurMatrix a=new CooccurMatrix();
		
		//�ڵ����ű�ʾ
		Map<String, Integer> vnameToVno=new HashMap<String, Integer>();
		
		//�������нڵ㣬����ѭ���������нڵ�
		List<String> allNodeList=new ArrayList<String>();
		
		//�ڵ㣺�������Ķ������
		Map<String, List<String>> nodeMapCommunities=new HashMap<String, List<String>>();
		
		Map<String, List<String>> finalPartition=new HashMap<String, List<String>>();
		
		Iterator iter=g.map.entrySet().iterator();
		int seqNo=0;
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String node=(String)entry.getKey();
			allNodeList.add(node);
			vnameToVno.put(node, seqNo);
			seqNo++;
		}
		System.out.println("�ڵ�����"+allNodeList.size());
		
		GraphSearch communityDetect=new GraphSearch();
		for(int numRuns=1;numRuns < 11;numRuns++){
			//�ڿ�ʼһ���µĳ�ʼ���������������֮ǰ��Ҫ��ͼg��ÿ���ڵ��labelbuffuer���ϴεõ�������������������
			g.Communities.clear();
			g.nodeCommunity.clear();
			
			//clear globalFrequencies before a new iteration
			g.globalFrequencies.clear();
			
			//clear the labelBuffer of every node before a new iteration
			Iterator ncIter=g.map.entrySet().iterator();
			while(ncIter.hasNext()){
				Map.Entry ncEntry=(Map.Entry)ncIter.next();
				VertexNode v=(VertexNode)ncEntry.getValue();
				v.labelBuffer.clear();
			}
			
			communityDetect.communityDetectProcedure(g);
			
			//����ÿһ�����еĻ��ֽ��
			ConsensusClusteringSerial cc=new ConsensusClusteringSerial();
			cc.Partition=g.Communities;
			cc.nodeCommunityMap=g.nodeCommunity;
			ccList.add(cc);
			//fill in the co-occurrence matrix
		}//for,numRuns
		System.out.println("�õ��Ļ��ָ���"+ccList.size());
		System.out.println("һ�»����࿪ʼ");
		//����õ���?0�����ֽ��?
		try {
			tempRo.outPutNodeMapCommunityTemp(ccList);
			tempRo.outputTempCommunities(ccList);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//������ͼ�Ķ���������Ϊ�գ�when GC work��free this memory  
		g=null;
		

		
		for(int partiIndex=0;partiIndex < 10;partiIndex++){
			ConsensusClusteringSerial partition=ccList.get(partiIndex);

			for(int uIndex=0;uIndex <allNodeList.size();uIndex++){
				String unode=allNodeList.get(uIndex);
				for(int vIndex=uIndex+1;vIndex <allNodeList.size();vIndex++){
					String vnode=allNodeList.get(vIndex);

					//if judge whether u and v belongs to the same community
					if(partition.nodeCommunityMap.get(unode) != null &&partition.nodeCommunityMap.get(vnode) != null && partition.nodeCommunityMap.get(unode).equals(partition.nodeCommunityMap.get(vnode))){
						a.addMatrixValue(unode, vnode, uIndex, vIndex);
					}//if 
				}//inner for
			}//outter for,co-occurrence matrix filled
			
		}//for 10 partition
		
		//make Co-occur matrix A symmetric
		a.symmetricMatrix();

		
		//calculate the ARI value between the every 2 of 10 partitions 
		//then using the ARI matrix to determine the final representative partition
		AdjustRandIndex myari=new AdjustRandIndex(10);
		for(int i=0;i < ccList.size();i++){
			for(int j=i;j < ccList.size();j++){
				//call the method
				myari.calcuARI(ccList.get(i).Partition, ccList.get(j).Partition, i,j, n);
			}
		}
		//symmetric the ARI matrix
		for(int i=0;i<10;i++){
			for(int j=0;j < 10;j++){
				if(i !=j)
					myari.R[j][i]=myari.R[i][j];
			}
		}
		
		System.out.println("ARIֵ������ϣ�");
		
		// using the ARI matrix determine the final representative partition
		double[] RrowSumAv=new double[10];
		for(int i=0;i<10;i++){
			double sum=0;
			for(int j=0;j < 10;j++){
				sum+=myari.R[i][j];
			}
			RrowSumAv[i]=(double)sum/10;
		}
		int index=0;
		double temp=0;
		for(int i=0;i < 10;i++){
			if(temp < RrowSumAv[i]){
				temp=RrowSumAv[i];
				index=i;
				}
		}
		System.out.println("finalpartition��ţ�"+index);
		finalPartition=ccList.get(index).Partition;
		// select the max community from the finalPartition,get r value
		Iterator it=finalPartition.entrySet().iterator();
		int maxCommuSize=0;
		int tsize=0;
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry)it.next();
			String commuName=(String)entry.getKey();
			tsize= finalPartition.get(commuName).size();
			if(tsize >maxCommuSize){
				maxCommuSize=tsize;
			}
		}
		double r=(double)1/maxCommuSize;
		
		//determine the overlapping nodes
//		Iterator finIter=finalPartition.entrySet().iterator();
//		while(finIter.hasNext()){
//			Map.Entry entry=(Map.Entry)finIter.next();
//			String commuName=(String)entry.getKey();
//			List<String> nodeInCList=finalPartition.get(commuName);
//			for(int i=0;i < allNodeList.size();i++){
//				String vnodeName=allNodeList.get(i);
//				if(nodeInCList.contains(vnodeName) || !a.matrix.containsKey(vnodeName)){
//					continue;
//				}else{
//					Map<String, Integer> vMap=a.matrix.get(vnodeName);
//					
//					int temp1=0;
//					try {
//						for(int j=0;j < nodeInCList.size();j++){
//							String uName=nodeInCList.get(j);
//							
//							if(uName!=null && vMap.containsKey(uName)){
//								temp1+=vMap.get(uName);
//							}else{
//								continue;
//							}
//						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					double temp2=0.0d;
//					if(temp1 >0){
//						System.out.println("temp1��ֵ"+temp1);
//					}
//					temp2=(double)temp1/(nodeInCList.size()*10);
//					if (temp2 > r){
//						System.out.println("temp2 > r,ʶ���ص�����ִ����");
//						//�ڵ�v�����ص�����ڵ�?�ŵ�nodeMapCommunitiesӳ��ṹ��?
//						if(nodeMapCommunities.containsKey(vnodeName)){
//							List<String> strInCom=nodeMapCommunities.get(vnodeName);
//							strInCom.add(commuName);
//							nodeMapCommunities.put(vnodeName, strInCom);
//						}else{
//							List<String> strInCom=new ArrayList<String>();
//							strInCom.add(commuName);
//							nodeMapCommunities.put(vnodeName, strInCom);
//						}//
//					}//if
//				}
//			}//for �����нڵ�ı���?
//		}//while  determine overlap nodes
		
		DetermineOverlapNodes.determine(finalPartition, allNodeList, a, r, nodeMapCommunities);
		
		System.out.println("nodeMapCommunities�Ĵ�С��"+nodeMapCommunities.size());
		
		//���finalRepresentition��nodeMapCommunities�е���Ϣ
		ResultOutput ro=new ResultOutput();
		try {
			System.out.println("print final result!!!");
			ro.outputCommunities(finalPartition);
			ro.outputOverLapNodes(nodeMapCommunities);
			System.out.println("OK! finish!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//main
	
}
