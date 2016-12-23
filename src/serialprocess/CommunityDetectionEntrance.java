package serialprocess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommunityDetectionEntrance {
	
	public static void main(String[] args){
		int whetherRepeat = 0;//网络图中的表示方式，是否有重复边,0：无重复边，1：有重复边
		String networkPath = "D:\\paperdata\\soybean\\community detection\\input network\\testnetwork-overlap.txt";
		Graph g=new Graph(5,networkPath,whetherRepeat);
		int n=g.map.size();
		ResultOutput tempRo=new ResultOutput();

		List<serialprocess.Partition> partitionList=new ArrayList<serialprocess.Partition>();

		//共生矩阵A的对象表示
		CooccurMatrix a=new CooccurMatrix();

		//保存所有节点，便于循环遍历所有节点
		List<String> allNodeList=new ArrayList<String>();

		//节点：其所属的多个社区
		Map<String, List<String>> nodeMapCommunities=new HashMap<String, List<String>>();

		Map<String, List<String>> bestPartitionCommunities;


		g=null;

		GraphSearch communityDetect=new GraphSearch();
		System.out.println("开始重复迭代聚类");
		for(int numRuns=1;numRuns < 11;numRuns++){
			System.out.println("迭代聚类第 "+numRuns+"次");
			//每一次repeat都要重新 new一下g,因为Java中的赋值操作，操作的始终是对象的引用，若不new重新分配地址，ccList中的元素都将指向相同对象g的地址空间
			g=new Graph(5,networkPath,whetherRepeat);

			communityDetect.communityDetectProcedure(g);

			//保存每一次运行的划分结果
			serialprocess.Partition partition=new Partition();
			partition.communities=g.Communities;
			partition.nodeCommunityMap=g.nodeCommunity;
			partitionList.add(partition);

			//fill in the co-occurrence matrix
		}//for,numRuns


		System.out.println("得到的划分个数"+partitionList.size());

		Iterator iter=g.map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String node=(String)entry.getKey();
			allNodeList.add(node);
		}
		System.out.println("把网络图的节点名都保存到1个list中，方便遍历，节点总数"+allNodeList.size());

		try {
			tempRo.outPutNodeMapCommunityTemp(partitionList);
			tempRo.outputTempCommunities(partitionList);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//将网络图的对象引用置为空，when GC work，free this memory
		g=null;


		System.out.println("一致化聚类开始");

		System.out.println("计算共生矩阵");
		//遍历所有的划分结果，计算共生矩阵
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
		System.out.println("开始计算ARI矩阵");
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

		System.out.println("ARI值计算完毕！");

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
		System.out.println("最有聚类划分序号："+index);
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
		System.out.println("开始识别重叠社区节点");
		DetermineOverlapNodes.determine(bestPartitionCommunities,bestPartitionNodeMapCommu, allNodeList, a, r, nodeMapCommunities);

		System.out.println("nodeMapCommunities的大小即重叠节点的个数："+nodeMapCommunities.size());

		//输出最终结果，包括社区划分、重叠节点集
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
