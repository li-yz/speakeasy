package serialprocess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultOutput {

	public void outputResult(Graph g) throws IOException{
		 FileWriter writer = new FileWriter("d://result2.txt");
         BufferedWriter bw = new BufferedWriter(writer);
         
		Iterator outIterator=g.map.keySet().iterator();
		while(outIterator.hasNext()){
			StringBuffer sb=new StringBuffer();
			String vertName=(String)outIterator.next();
			VertexNode vert=g.map.get(vertName);
			
			sb.append("节点："+vert.vertexName+"的buffer：");
			sb.append(vert.labelBuffer);
			
			bw.newLine();
			bw.write(sb.toString());
		}
		 bw.close();
         writer.close();
	}
	
	public void outputCommunities(Graph g) throws IOException{
		 FileWriter writer = new FileWriter("d://communities.txt");
        BufferedWriter bw = new BufferedWriter(writer);
        
		Iterator it=g.Communities.keySet().iterator();
		while(it.hasNext()){
			StringBuffer sb=new StringBuffer();
			String communityTag=(String)it.next();
			List<String> nodesList=g.Communities.get(communityTag);
			
			sb.append("社区标志："+communityTag+"内：");
			sb.append(nodesList);
			
			bw.newLine();
			bw.write(sb.toString());
		}
		 bw.close();
        writer.close();
	}
	
	//输出社区划分结果
	public void outputCommunities(Map<String, List<String>> partition) throws IOException{
		FileWriter writer = new FileWriter("d:\\最终结果\\finalPartition.txt");
		BufferedWriter bw = new BufferedWriter(writer);
       
		Iterator it=partition.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry)it.next();
			String communityTag=(String)entry.getKey();
			List<String> nodesList=partition.get(communityTag);
			StringBuffer sb=new StringBuffer();
			
			sb.append("社区标志："+communityTag+"的社区内：");
			sb.append(nodesList);
			
			bw.newLine();
			bw.write(sb.toString());
		}
		bw.close();
		writer.close();
	}
	
	//输出重叠节点
	public void outputOverLapNodes(Map<String, List<String>> nodeAndCommunities) throws IOException{
		FileWriter writer = new FileWriter("d:\\最终结果\\overlapNodes.txt");
		BufferedWriter bw = new BufferedWriter(writer);
      
		Iterator it=nodeAndCommunities.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry)it.next();
			String nodeName=(String)entry.getKey();
			List<String> communitiesList=nodeAndCommunities.get(nodeName);
			StringBuffer sb=new StringBuffer();
			
			sb.append("节点："+nodeName+"属于："+communitiesList+"个社区：");
			for(String community :communitiesList){
				sb.append(community+"\t");
			}
			
			bw.newLine();
			bw.write(sb.toString());
		}
		bw.close();
		writer.close();
	}
	
	//验证读取到的数据的正确性
	public void validateGraph(Graph g) throws IOException{
		 FileWriter writer = new FileWriter("d:\\读取到的图数据\\graphGeted.txt");
        BufferedWriter bw = new BufferedWriter(writer);
        
			StringBuffer sb=new StringBuffer();
			VertexNode vert=g.map.get("1");
			
			sb.append("节点："+vert.vertexName);
			sb.append(vert.neighborList);
			
			bw.write(sb.toString());
			VertexNode vert2=g.map.get("10980");
			sb=new StringBuffer();
			sb.append("节点："+vert2.vertexName);
			sb.append(vert2.neighborList);
			bw.newLine();
			bw.write(sb.toString());
			
		 bw.close();
        writer.close();
	}
	
	//输出10个社区划分的中间结果
	public void outputTempCommunities(List<ConsensusClusteringSerial> list) throws IOException{
		for(int t=0;t < list.size();t++){
			Map<String, List<String>> partition=new HashMap<String, List<String>>();
			partition=list.get(t).Partition;
			StringBuffer sb1=new StringBuffer();
			sb1.append("d:\\划分的中间结果十个\\tempPartition"+t+".txt");
			FileWriter writer = new FileWriter(sb1.toString());
			BufferedWriter bw = new BufferedWriter(writer);

       
			Iterator it=partition.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry=(Map.Entry)it.next();
				String communityTag=(String)entry.getKey();
				List<String> nodesList=partition.get(communityTag);
				StringBuffer sb=new StringBuffer();
				sb.append(communityTag+":");
				for(String node :nodesList){
					sb.append("\t"+node);
				}
				bw.write(sb.toString());
				bw.newLine();
				
			}//while 遍历一个社区
			bw.close();
			writer.close();
		}//for 10个社区
	}
	
	public void outPutNodeMapCommunityTemp(List<ConsensusClusteringSerial> list) throws IOException{
		for(int k=0;k < list.size();k++){
			Map<String, String> nodeMapCommu=list.get(k).nodeCommunityMap;
			StringBuffer sb1=new StringBuffer();
			sb1.append("d:\\节点及其所属社区中间结果\\nodeMapCommu"+k+".txt");
			FileWriter writer = new FileWriter(sb1.toString());
			BufferedWriter bw = new BufferedWriter(writer);
			
			Iterator it=nodeMapCommu.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry=(Map.Entry)it.next();
				String node=(String)entry.getKey();
				String commu=nodeMapCommu.get(node);
				StringBuffer sb=new StringBuffer();
				sb.append(node+":\t"+commu);
				bw.write(sb.toString());
				bw.newLine();
			}
			bw.close();
			writer.close();
		}
	}
}
