package serialprocess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DetermineOverlapNodes {

	/**
	 * ʶ���ص������ڵ� ���㹫ʽ
	 * @param bestPartitionCommunities ����numRuns�ظ����࣬ɸѡ�������Ż���
	 * @param allNodeList
	 * @param a ��������
	 * @param r �ж�һ���ڵ��Ƿ����ص������ڵ����ֵ
	 * @param nodeMapCommunities ���������ص������ڵ�Ľ�����ڵ��� <---> ������������
     */
	public static void determine(Map<String, List<String>> bestPartitionCommunities,Map<String, String> bestPartitionNodeMapCommu,List<String> allNodeList,CooccurMatrix a,double r,Map<String, List<String>> nodeMapCommunities){
		//determine the overlapping nodes
		
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
					
					int temp1=0;//�京���ǣ���numRuns�ظ���������У��ڵ�v ��finalPartition�� ����c�еĽڵ�۵�һ����ܴ��������нڵ�v ������ ����c
					for(int j=0;j < nodeInCList.size();j++){
						String uName=nodeInCList.get(j);
						
						if(uName!=null && vMap.containsKey(uName)){
							temp1+=vMap.get(uName);
						}else{
							continue;
						}
					}
					double temp2=0.0d;
					temp2=(double)temp1/(nodeInCList.size()*10);
					if (temp2 > r){//������ֵ�����ڵ�v���ص��ڵ㣬���ڵ�ǰ����c
//						System.out.println("temp2 > r,�ڵ�v: "+vnodeName+" Ҳ���������� "+commuName);
						if(nodeMapCommunities.containsKey(vnodeName)){
							List<String> strInCom=nodeMapCommunities.get(vnodeName);
							strInCom.add(commuName);
							nodeMapCommunities.put(vnodeName, strInCom);

							//�ѽڵ�v��������c��
							nodeInCList.add(vnodeName);
						}else{
							List<String> strInCom=new ArrayList<String>();
							strInCom.add(commuName);
							nodeMapCommunities.put(vnodeName, strInCom);

							//�ѽڵ�v��������c��
							nodeInCList.add(vnodeName);
						}//
					}//if
				}
		}//
	}//for

		//�Ѷ������ڵ�ԭ����������������Ҳ��ӵ�nodeMapCommunities�У���Ϊ����õ���nodeMapCommunities�У��ڵ�����key����value��ֻ�����˽ڵ�ԭ�������ڵ�������
		Iterator iter=nodeMapCommunities.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String nodeName=(String)entry.getKey();

			String originCommu=bestPartitionNodeMapCommu.get(nodeName);
			List<String> list=nodeMapCommunities.get(nodeName);
			list.add(originCommu);
			nodeMapCommunities.put(nodeName, list);
		}

	}
}
