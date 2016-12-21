package serialprocess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DetermineOverlapNodes {
	public static void determine(Map<String, List<String>> finalPartition,List<String> allNodeList,CooccurMatrix a,double r,Map<String, List<String>> nodeMapCommunities){
		//determine the overlapping nodes
		
		for(int i=0;i < allNodeList.size();i++){
			Iterator finIter=finalPartition.entrySet().iterator();
			while(finIter.hasNext()){
				Map.Entry entry=(Map.Entry)finIter.next();
				String commuName=(String)entry.getKey();
				List<String> nodeInCList=finalPartition.get(commuName);
				String vnodeName=allNodeList.get(i);
				if(nodeInCList.contains(vnodeName) || !a.matrix.containsKey(vnodeName)){
					continue;
				}else{
					Map<String, Integer> vMap=a.matrix.get(vnodeName);
					
					int temp1=0;
					for(int j=0;j < nodeInCList.size();j++){
						String uName=nodeInCList.get(j);
						
						if(uName!=null && vMap.containsKey(uName)){
							temp1+=vMap.get(uName);
						}else{
							continue;
						}
					}
					double temp2=0.0d;
					if(temp1 >0){
//						System.out.println("temp1的值"+temp1);
					}
					temp2=(double)temp1/(nodeInCList.size()*10);
					if (temp2 > r){
						System.out.println("temp2 > r,识别重叠社区执行了");
						if(nodeMapCommunities.containsKey(vnodeName)){
							List<String> strInCom=nodeMapCommunities.get(vnodeName);
							strInCom.add(commuName);
							nodeMapCommunities.put(vnodeName, strInCom);
						}else{
							List<String> strInCom=new ArrayList<String>();
							strInCom.add(commuName);
							nodeMapCommunities.put(vnodeName, strInCom);
						}//
					}//if
				}
		}//while锟斤拷锟斤拷锟叫节碉拷谋锟斤拷锟?
	}//for 
	}
}
