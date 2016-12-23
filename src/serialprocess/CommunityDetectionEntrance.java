package serialprocess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommunityDetectionEntrance {
	
	public static void main(String[] args){
		int whetherRepeat = 0;//����ͼ�еı�ʾ��ʽ���Ƿ����ظ���,0�����ظ��ߣ�1�����ظ���
		String networkPath = "D:\\paperdata\\soybean\\community detection\\input network\\testnetwork-overlap.txt";
		Graph g=new Graph(5,networkPath,whetherRepeat);
		int n=g.map.size();
		ResultOutput tempRo=new ResultOutput();

		List<serialprocess.Partition> partitionList=new ArrayList<serialprocess.Partition>();

		//��������A�Ķ����ʾ
		CooccurMatrix a=new CooccurMatrix();

		//�������нڵ㣬����ѭ���������нڵ�
		List<String> allNodeList=new ArrayList<String>();

		//�ڵ㣺�������Ķ������
		Map<String, List<String>> nodeMapCommunities=new HashMap<String, List<String>>();

		Map<String, List<String>> bestPartitionCommunities;


		g=null;

		GraphSearch communityDetect=new GraphSearch();
		System.out.println("��ʼ�ظ���������");
		for(int numRuns=1;numRuns < 11;numRuns++){
			System.out.println("��������� "+numRuns+"��");
			//ÿһ��repeat��Ҫ���� newһ��g,��ΪJava�еĸ�ֵ������������ʼ���Ƕ�������ã�����new���·����ַ��ccList�е�Ԫ�ض���ָ����ͬ����g�ĵ�ַ�ռ�
			g=new Graph(5,networkPath,whetherRepeat);

			communityDetect.communityDetectProcedure(g);

			//����ÿһ�����еĻ��ֽ��
			serialprocess.Partition partition=new Partition();
			partition.communities=g.Communities;
			partition.nodeCommunityMap=g.nodeCommunity;
			partitionList.add(partition);

			//fill in the co-occurrence matrix
		}//for,numRuns


		System.out.println("�õ��Ļ��ָ���"+partitionList.size());

		Iterator iter=g.map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String node=(String)entry.getKey();
			allNodeList.add(node);
		}
		System.out.println("������ͼ�Ľڵ��������浽1��list�У�����������ڵ�����"+allNodeList.size());

		try {
			tempRo.outPutNodeMapCommunityTemp(partitionList);
			tempRo.outputTempCommunities(partitionList);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//������ͼ�Ķ���������Ϊ�գ�when GC work��free this memory
		g=null;


		System.out.println("һ�»����࿪ʼ");

		System.out.println("���㹲������");
		//�������еĻ��ֽ�������㹲������
		for(int partiIndex=0;partiIndex < 10;partiIndex++){
			serialprocess.Partition partition=partitionList.get(partiIndex);

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
		System.out.println("��ʼ����ARI����");
		AdjustRandIndex myari=new AdjustRandIndex(10);
		for(int i=0;i < partitionList.size();i++){
			for(int j=i;j < partitionList.size();j++){
				//call the method
				myari.calcuARI(partitionList.get(i).communities, partitionList.get(j).communities, i,j, n);
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
			RrowSumAv[i]=sum/10;
		}
		int index=0;
		double temp=0;
		for(int i=0;i < 10;i++){
			if(temp < RrowSumAv[i]){
				temp=RrowSumAv[i];
				index=i;
				}
		}
		System.out.println("���о��໮����ţ�"+index);
		bestPartitionCommunities=partitionList.get(index).communities;
		Map<String, String> bestPartitionNodeMapCommu=partitionList.get(index).nodeCommunityMap;
		// select the max community from the bestPartitionCommunities,get r value
		int maxCommuNum=0;
		int tsize=0;

		for(serialprocess.Partition cc :partitionList){
			tsize=cc.communities.size();
			if(tsize > maxCommuNum)
				maxCommuNum=tsize;
		}

		double r=(double)1/maxCommuNum;

		//determine the overlapping nodes
		System.out.println("��ʼʶ���ص������ڵ�");
		DetermineOverlapNodes.determine(bestPartitionCommunities,bestPartitionNodeMapCommu, allNodeList, a, r, nodeMapCommunities);

		System.out.println("nodeMapCommunities�Ĵ�С���ص��ڵ�ĸ�����"+nodeMapCommunities.size());

		//������ս���������������֡��ص��ڵ㼯
		ResultOutput ro=new ResultOutput();
		try {
			System.out.println("print final result!!!");
			ro.outputCommunities(bestPartitionCommunities);
			ro.outputOverLapNodes(nodeMapCommunities);
			System.out.println("OK! finish!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//main

}
