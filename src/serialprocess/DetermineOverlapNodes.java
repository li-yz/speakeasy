package serialprocess;

import utils.MyOutPut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DetermineOverlapNodes {

	/**
	 * 识别重叠社区节点 计算公式
	 * @param bestPartitionCommunities 经过numRuns重复聚类，筛选出的最优划分
	 * @param allNodeList
	 * @param a 共生矩阵
	 * @param r 判断一个节点是否是重叠社区节点的阈值
	 * @param nodeMapCommunities 用来保存重叠社区节点的结果，节点名 <---> 所属社区集合
     */
	public static void determine(Map<String, List<String>> bestPartitionCommunities,Map<String, String> bestPartitionNodeMapCommu,List<String> allNodeList,CooccurMatrix a,double r,Map<String, List<String>> nodeMapCommunities){
		//determine the overlapping nodes

		StringBuffer sb = new StringBuffer();

		for(int i=0;i < allNodeList.size();i++){
			Iterator finIter=bestPartitionCommunities.entrySet().iterator();
			while(finIter.hasNext()){
				Map.Entry entry=(Map.Entry)finIter.next();
				String commuName=(String)entry.getKey();
				List<String> nodeInCList=bestPartitionCommunities.get(commuName);
				String vnodeName=allNodeList.get(i);
				if(nodeInCList.contains(vnodeName) || !a.matrix.containsKey(vnodeName)){
					continue;
				}else{
					Map<String, Integer> vMap=a.matrix.get(vnodeName);
					
					int temp1=0;//其含义是：在numRuns重复聚类过程中，节点v 与finalPartition的 社区c中的节点聚到一起的总次数，其中节点v 不属于 社区c
					for(int j=0;j < nodeInCList.size();j++){
						String uName=nodeInCList.get(j);
						
						if(uName!=null && vMap.containsKey(uName)){
							temp1+=vMap.get(uName);
						}else{
							continue;
						}
					}
					double temp2=0.0d;

					//4.10毕业论文中算法改进1部分
//					List<String> nodeInVOriginalCom = bestPartitionCommunities.get(bestPartitionNodeMapCommu.get(vnodeName));
//					int max = nodeInVOriginalCom.size();
//					if(max < nodeInCList.size()){
//						max = nodeInCList.size();
//					}
					//4.10毕业论文中算法改进1部分
					int max = nodeInCList.size();//改进前

					temp2=(double)temp1/(max*10);

					//保存所有大于0的temp2的值，即Wv,c的值,了解其分布，以便选择合适的阈值
					if(temp2 > 0) {
						sb.append(temp2 + " ");
					}

					if (temp2 > r){//大于阈值，即节点v是重叠节点，属于当前社区c
//						System.out.println("temp2 > r,节点v: "+vnodeName+" 也属于社区： "+commuName);
						if(nodeMapCommunities.containsKey(vnodeName)){
							List<String> strInCom=nodeMapCommunities.get(vnodeName);
							strInCom.add(commuName);
							nodeMapCommunities.put(vnodeName, strInCom);

							//把节点v并入社区c中
							nodeInCList.add(vnodeName);
						}else{
							List<String> strInCom=new ArrayList<String>();
							strInCom.add(commuName);
							nodeMapCommunities.put(vnodeName, strInCom);

							//把节点v并入社区c中
							nodeInCList.add(vnodeName);
						}//
					}//if
				}
		}//
	}//for

		//把多社区节点原来所属的社区名字也添加到nodeMapCommunities中，因为上面得到的nodeMapCommunities中，节点名是key，其value中只存入了节点原来不属于的社区名
		Iterator iter=nodeMapCommunities.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String nodeName=(String)entry.getKey();

			String originCommu=bestPartitionNodeMapCommu.get(nodeName);
			List<String> list=nodeMapCommunities.get(nodeName);
			list.add(originCommu);
			nodeMapCommunities.put(nodeName, list);
		}


		//保存sb,即每个节点Wv,c的分布
//		MyOutPut.saveStringResultToTxt(sb.toString(),"D:\\paperdata\\soybean\\community detection\\筛选重叠节点Wv,c分布\\Wvc.txt");
	}

}
