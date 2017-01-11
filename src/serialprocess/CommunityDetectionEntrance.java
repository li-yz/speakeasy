package serialprocess;

import utils.MySerialization;
import utils.MyPrint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommunityDetectionEntrance {
	
	public static void main(String[] args){
		MySerialization mySerialization = new MySerialization();//���л����߶���

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startDate = sdf.format(date);

		int whetherRepeat = 0;//����ͼ�еı�ʾ��ʽ���Ƿ����ظ���,0�����ظ��ߣ�1�����ظ��� ������������������
		String networkPath = "D:\\paperdata\\test network\\ʹ��lfr���ɵ���������\\network.dat";
		Graph g = new Graph(5,networkPath,whetherRepeat);

		//���л�����ͼg����ҪĿ���Ǳ�������ͼ�ṹ�������������ģ�����
		mySerialization.serializeObject(g,"D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");

		//�������нڵ㣬����ѭ���������нڵ�
		List<String> allNodeList=new ArrayList<String>();

		int n=g.map.size();
		Iterator iter=g.map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String node=(String)entry.getKey();
			allNodeList.add(node);
		}
		System.out.println("������ͼ�Ľڵ��������浽1��list�У�����������ڵ�����"+allNodeList.size());

		ResultOutput tempRo=new ResultOutput();

		List<Partition> partitionList=new ArrayList<Partition>();

		//��������A�Ķ����ʾ
		CooccurMatrix a=new CooccurMatrix();

		//�ڵ㣺�������Ķ������ �������ҵ����ص������ڵ���
		Map<String, List<String>> nodeMapCommunities=new HashMap<String, List<String>>();

		Map<String, List<String>> bestPartitionCommunities;
		Map<String, String> bestPartitionNodeMapCommu;

		GraphSearch communityDetect=new GraphSearch();
		System.out.println("��ʼ�ظ���������");
		for(int numRuns=1;numRuns < 11;numRuns++){
			System.out.println("��������� "+numRuns+"��");
			//ÿһ��repeat��Ҫ�������ͼ�� �� buffer���� �� globalFrequency�����ֵ
			//���������ÿ���ڵ��buffer
			Iterator netIter = g.map.entrySet().iterator();
			while(netIter.hasNext()){
				Map.Entry<String,VertexNode> entry = (Map.Entry<String,VertexNode>)netIter.next();
				entry.getValue().labelBuffer.clear();
			}
			//�������ͼ ��globalFrequency��
			g.globalFrequencies.clear();

			Partition partition=new Partition();//ÿһ�ξ��඼Ҫnew Partition���¿���һ���ڴ�ռ��������������ֽ��
			communityDetect.communityDetectProcedure(g,partition);

			//����ÿһ�����еĻ��ֽ��
			partitionList.add(partition);

		}//for,numRuns


		System.out.println("�õ��Ļ��ָ���"+partitionList.size());

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
		System.out.println("���ž��໮����ţ�"+index);
		Partition bestPartition = partitionList.get(index);

		//�����ص��� ���Ż��ֽ�����л�����
		mySerialization.serializeObject(bestPartition,"D:\\paperdata\\soybean\\community detection\\���ս��\\bestNonOverlapPartition.obj");

		bestPartitionCommunities=partitionList.get(index).communities;
		bestPartitionNodeMapCommu=partitionList.get(index).nodeCommunityMap;
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
			System.out.println("�ܵ�����������"+bestPartitionCommunities.size());
			System.out.println("�ص������ڵ������"+nodeMapCommunities.size());
			ro.outputCommunities(bestPartitionCommunities);
			ro.outputOverLapNodes(nodeMapCommunities);
			System.out.println("OK! finish!");
			date = new Date();
			String endDate = sdf.format(date);

			int processTime = (int)(sdf.parse(endDate).getTime() - sdf.parse(startDate).getTime())/(1000*60); //����ó������Ǻ��룬ת�� ����Ϊ��λ
			System.out.println("�ܵ�ִ��ʱ�� = "+processTime +" ����");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//����ͳ�������Ĵ�С�ֲ����
		int numOf2_3=0,numOf4_5=0,numOf6_9=0,numOf10_15=0,numOf16_25=0,numOf26_35=0,numOf36_50=0,numOf50_OO=0;
		Iterator bestIter = bestPartitionCommunities.entrySet().iterator();
		while(bestIter.hasNext()){
			Map.Entry entry = (Map.Entry<String,List<String>>) bestIter.next();
			String key =(String) entry.getKey();
			List<String> list = (List<String>) entry.getValue();
			int size = list.size();
			if(size >=2 && size <=3){
				numOf2_3++;
			}else if(size >=4 && size <=5){
				numOf4_5++;
			}else if(size >=6 && size <=9){
				numOf6_9++;
			}else if(size >=10 && size <=15){
				numOf10_15++;
			}else if(size >=16 && size <=25){
				numOf16_25++;
			}else if(size >=26 && size <=35){
				numOf26_35++;
			}else if(size >=36 && size <=50){
				numOf36_50++;
			}else if(size > 50){
				numOf50_OO++;
			}
		}
		MyPrint.print("������С[2,3]���У� "+numOf2_3+" ��");
		MyPrint.print("������С[4,5]���У� "+numOf4_5+" ��");
		MyPrint.print("������С[6,9]���У� "+numOf6_9+" ��");
		MyPrint.print("������С[10,15]���У� "+numOf10_15+" ��");
		MyPrint.print("������С[16,25]���У� "+numOf16_25+" ��");
		MyPrint.print("������С[26,35]���У� "+numOf26_35+" ��");
		MyPrint.print("������С[36,50]���У� "+numOf36_50+" ��");
		MyPrint.print("������С[50,+��]���У� "+numOf50_OO+" ��");

		//���ص��������ֽ�����浽OverlapPartition�����У��������л�����������ֱ�ӷ����л�������������ֽ�����з���
		OverlapPartition overlapPartition = new OverlapPartition();
		overlapPartition.setCommunities(bestPartitionCommunities);
		overlapPartition.setNodeMapCommunities(nodeMapCommunities);

		mySerialization.serializeObject(overlapPartition,"D:\\paperdata\\soybean\\community detection\\���ս��\\overlapPartition.obj");

	}//main

}
