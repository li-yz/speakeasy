package serialprocess;

import utils.MySerialization;
import utils.MyPrint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommunityDetectionEntrance {
	
	public static void main(String[] args){
		MySerialization mySerialization = new MySerialization();//序列化工具对象

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startDate = sdf.format(date);

		int whetherRepeat = 0;//网络图中的表示方式，是否有重复边,0：无重复边，1：有重复边 ！！！！！！！！！
		String networkPath = "D:\\paperdata\\test network\\使用lfr生成的网络数据\\network.dat";
		Graph g = new Graph(5,networkPath,whetherRepeat);

		//序列化网络图g，主要目的是保存网络图结构，方便后续计算模块度用
		mySerialization.serializeObject(g,"D:\\paperdata\\soybean\\community detection\\original graph structure\\graph.obj");

		//保存所有节点，便于循环遍历所有节点
		List<String> allNodeList=new ArrayList<String>();

		int n=g.map.size();
		Iterator iter=g.map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String node=(String)entry.getKey();
			allNodeList.add(node);
		}
		System.out.println("把网络图的节点名都保存到1个list中，方便遍历，节点总数"+allNodeList.size());

		ResultOutput tempRo=new ResultOutput();

		List<Partition> partitionList=new ArrayList<Partition>();

		//共生矩阵A的对象表示
		CooccurMatrix a=new CooccurMatrix();

		//节点：其所属的多个社区 ，保存找到的重叠社区节点结果
		Map<String, List<String>> nodeMapCommunities=new HashMap<String, List<String>>();

		Map<String, List<String>> bestPartitionCommunities;
		Map<String, String> bestPartitionNodeMapCommu;

		GraphSearch communityDetect=new GraphSearch();
		System.out.println("开始重复迭代聚类");
		for(int numRuns=1;numRuns < 11;numRuns++){
			System.out.println("迭代聚类第 "+numRuns+"次");
			//每一次repeat都要清空网络图中 的 buffer内容 和 globalFrequency区域的值
			//清空网络中每个节点的buffer
			Iterator netIter = g.map.entrySet().iterator();
			while(netIter.hasNext()){
				Map.Entry<String,VertexNode> entry = (Map.Entry<String,VertexNode>)netIter.next();
				entry.getValue().labelBuffer.clear();
			}
			//清空网络图 的globalFrequency域
			g.globalFrequencies.clear();

			Partition partition=new Partition();//每一次聚类都要new Partition。新开辟一段内存空间来保存社区划分结果
			communityDetect.communityDetectProcedure(g,partition);

			//保存每一次运行的划分结果
			partitionList.add(partition);

		}//for,numRuns


		System.out.println("得到的划分个数"+partitionList.size());

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
		System.out.println("最优聚类划分序号："+index);
		Partition bestPartition = partitionList.get(index);

		//将非重叠的 最优划分结果序列化保存
		mySerialization.serializeObject(bestPartition,"D:\\paperdata\\soybean\\community detection\\最终结果\\bestNonOverlapPartition.obj");

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
		System.out.println("开始识别重叠社区节点");
		DetermineOverlapNodes.determine(bestPartitionCommunities,bestPartitionNodeMapCommu, allNodeList, a, r, nodeMapCommunities);

		System.out.println("nodeMapCommunities的大小即重叠节点的个数："+nodeMapCommunities.size());

		//输出最终结果，包括社区划分、重叠节点集
		ResultOutput ro=new ResultOutput();
		try {
			System.out.println("print final result!!!");
			System.out.println("总的社区个数："+bestPartitionCommunities.size());
			System.out.println("重叠社区节点个数："+nodeMapCommunities.size());
			ro.outputCommunities(bestPartitionCommunities);
			ro.outputOverLapNodes(nodeMapCommunities);
			System.out.println("OK! finish!");
			date = new Date();
			String endDate = sdf.format(date);

			int processTime = (int)(sdf.parse(endDate).getTime() - sdf.parse(startDate).getTime())/(1000*60); //做差得出来的是毫秒，转成 分钟为单位
			System.out.println("总的执行时间 = "+processTime +" 分钟");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//粗略统计社区的大小分布情况
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
		MyPrint.print("社区大小[2,3]的有： "+numOf2_3+" 个");
		MyPrint.print("社区大小[4,5]的有： "+numOf4_5+" 个");
		MyPrint.print("社区大小[6,9]的有： "+numOf6_9+" 个");
		MyPrint.print("社区大小[10,15]的有： "+numOf10_15+" 个");
		MyPrint.print("社区大小[16,25]的有： "+numOf16_25+" 个");
		MyPrint.print("社区大小[26,35]的有： "+numOf26_35+" 个");
		MyPrint.print("社区大小[36,50]的有： "+numOf36_50+" 个");
		MyPrint.print("社区大小[50,+∞]的有： "+numOf50_OO+" 个");

		//将重叠社区划分结果保存到OverlapPartition对象中，便于序列化，后续可以直接反序列化，针对社区发现结果进行分析
		OverlapPartition overlapPartition = new OverlapPartition();
		overlapPartition.setCommunities(bestPartitionCommunities);
		overlapPartition.setNodeMapCommunities(nodeMapCommunities);

		mySerialization.serializeObject(overlapPartition,"D:\\paperdata\\soybean\\community detection\\最终结果\\overlapPartition.obj");

	}//main

}
