package serialprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * ������ͼ�࣬ͼ�Ļ���Ԫ�أ��ڵ㼯���߼����ڽӾ��󣩡��ڵ����Ŀ.
 * ͼ����Ӧ���а�ͼGraph��ʼ���ķ�����ͼ�Ĺ�����ȱ����㷨��
 * @author Administrator
 *
 */
public class Graph{
	int vertexNum;
	int edgeNum;
	int bufferSize;
	
	//��map����ţ��ڵ����ƣ��ͽڵ��������ã�
	Map<String, VertexNode> map=new HashMap<String, VertexNode>();
	
	//����ѷ��ʽڵ���
	Set<String> visited=new HashSet<String>();
	
	List<Edges> totalEdgesList=new ArrayList<Edges>();
	
	/**
	 * 
	 * �����ű�ǩ��ȫ�ָ��ʵ����ݽṹglobalFrequencies����hashmap��ʵ��
	 */
	Map<String, Double>globalFrequencies=new HashMap<String,Double>();
	
	//���ʶ�������������C,���а���C1,C2,...Ck�ȶ������,key�����һ�������ı�ǣ�value����������������ڵĽڵ�������
	Map<String, List<String>> Communities=new HashMap<String, List<String>>();
	
	//�洢����detection�Ժ󣬽ڵ㼰���Ӧ��������key:�ڵ���-value��������־��
	Map<String, String> nodeCommunity=new HashMap<String, String>();
	
	Graph()
	{
		
	}
	//���ι���������ʼ��ͼ�Ľڵ������Ӷ�֪���ڽӾ���Ľ���
	Graph(int bSize,String filePath,int whetherRepeat)
	{
		this.bufferSize=bSize;
		switch (whetherRepeat){
			case 0:
				readData(filePath,totalEdgesList,map);//���ı���ʼ���߼�������ͼ���ڽӾ���
				break;
			case 1:
				readDataForRepeatEdges(filePath,totalEdgesList,map);//ԭ���ݼ��еı����ظ��ģ���һ���߱�ʾ������
				break;
			default:
				break;
		}
	}
	
	
	/**
	 * ��ȡ�ı��еı߼����ݣ���Graph�������������б�����һ�Ρ�
	 * @param filepath
	 * @param edgelist������ͼ�ı߼��ϣ� ���õ�ʱ����ܵ���list�����ã���edgelist��ָ����ĸı��ͬ�ڸı�ԭ������ָ�Ŀռ�
	 * @param map
	 */
	public void readData(String filepath,List<Edges>edgelist,Map<String, VertexNode> map)
	{
		
		String encoding="GBK";
		File file=new File(filepath);
		if(file.isFile() && file.exists()){
			try {
				InputStreamReader read=new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bf=new BufferedReader(read);
				String readrow="";
				System.out.println("��ʼ��ȡ�ļ� ��");
				while((readrow=bf.readLine()) != null){
					
					String []str=readrow.split("\t");
					VertexNode from=new VertexNode(str[0]);
					VertexNode to=new VertexNode(str[1]);
						Edges newEdge=new Edges(from, to);
						//�Ѷ�ȡ������������ӵ�ͼ�ı߼�����
						edgelist.add(newEdge);
						
						//��from�ڵ���to�ڵ��Ӧ�Ľڵ㶼���浽map�У�ͬʱ�����ظ��Ľڵ���
						if(!map.containsKey(str[0])){
							from.neighborList.add(str[1]);
							map.put(str[0], from);
						}else{
							from=map.get(str[0]);
							from.neighborList.add(str[1]);
							map.put(str[0], from);
						}
						if(!map.containsKey(str[1])){
							to.neighborList.add(str[0]); 
							map.put(str[1], to);
						}else{
							to=map.get(str[1]);
							to.neighborList.add(str[0]);
							map.put(str[1], to);
						}
						
				}
				bf.close();
				read.close();
				System.out.println("��ȡ��ϣ��б������Ľڵ�"+map.size()+"ͼ���ܱ���"+edgelist.size());
				
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��ȡ�����ظ��ߵ����ݼ�����һ���߱��������Σ�from-target,target-from��ʽ
	 * @param filepath
	 * @param edgelist
	 * @param map
	 */
	public void readDataForRepeatEdges(String filepath,List<Edges>edgelist,Map<String, VertexNode> map)
	{
		
		String encoding="GBK";
		File file=new File(filepath);
		if(file.isFile() && file.exists()){
			try {
				InputStreamReader read=new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bf=new BufferedReader(read);
				String readrow="";
				System.out.println("��ʼ��ȡ�ļ� ��");
				while((readrow=bf.readLine()) != null){
					
					String []str=readrow.split("\t");
					VertexNode from=new VertexNode(str[0]);
					VertexNode to=new VertexNode(str[1]);
						Edges newEdge=new Edges(from, to);
						//�Ѷ�ȡ������������ӵ�ͼ�ı߼�����
						edgelist.add(newEdge);
						
						//ÿ��ȡһ����ֻ��from�ڵ��Ӧ�Ľڵ㱣�浽map�м��ɣ�ͬʱ�����ظ��Ľڵ���
						if(!map.containsKey(str[0])){
							from.neighborList.add(str[1]);
							map.put(str[0], from);
						}else{
							from=map.get(str[0]);
							from.neighborList.add(str[1]);
							map.put(str[0], from);
						}
						
				}
				System.out.println("��ȡ��ϣ��б������Ľڵ�"+map.size()+"ͼ���ܱ���"+edgelist.size());
				
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ������ȱ���
	 * @param g ͼ
	 * @param functionIndex ��η���һ���ڵ�ĺ����������,0:��ʼ����һ��  1����ʼ���ڶ��� ����ÿ��buffer  2������ȫ�ָ���  3��ȷ��ÿ���ڵ�Ҫ���µı�ǩ  4����ȡ�������ֽ��
	 */
	public void BFSTraverse(Graph g,int functionIndex){
		Queue<VertexNode> q=new LinkedList<VertexNode>();
		Iterator bfsIter=g.map.entrySet().iterator();
		while(bfsIter.hasNext()){
			Map.Entry entry=(Map.Entry)bfsIter.next();
			String vertexName=(String)entry.getKey();
			VertexNode v=g.map.get(vertexName);
			if(!g.visited.contains(vertexName)){
				BFS(g, v, q,functionIndex);
			}
		}
	}
	
	/**
	 * ������ȱ�������һ�����㿪ʼ
	 * @param g ͼg
	 * @param v ������ʼ�Ľڵ�,��ͼ��map���ͽڵ㼯���ж�Ӧ�Ľڵ�����
	 * @param q ���������ѷ��ʽڵ�Ķ���
	 * @param functionIndex SpeakEasy���еķ�����ţ����ֵ�����ʼ�������±�ǩ������
	 * �����ڱ���ͼ��ʱ��Խڵ���������Ĵ�����ʼ��������ȫ�ָ��ʷֲ������½ڵ��buffer�Ȳ�����
	 */
	public void BFS(Graph g,VertexNode v,Queue<VertexNode>q,int functionIndex){
		//���ʽڵ�v������SpeakEasy����

		SpeakEasy speak=new SpeakEasy();
		ExtractCommunities ec=new ExtractCommunities();
		
		try {
			switch (functionIndex) {
			case 0:
				speak.initializeLabelBuffer(g,v);
				break;
			case 1:
				speak.fillLabelBuffer(g,v);
				break;
			case 2:
				speak.calcuGlobalFrequency(g, v);
				break;
			case 3:
				speak.determineAndAlterLabel(g, v);
				break;
			case 4:
				ec.extractCommunity(g, v);
				break;
			default:
				break;
			}
			g.visited.add(v.vertexName);
			q.offer(v);
			while(!q.isEmpty()){
				VertexNode queueFront=q.poll();
				for(int j=0;j<queueFront.neighborList.size();j++){
					String neighborName=queueFront.neighborList.get(j);
					VertexNode curNeighbor=g.map.get(neighborName);
					if(!g.visited.contains(neighborName)){
						//���ʸýڵ�
						switch (functionIndex) {
						case 0:
							speak.initializeLabelBuffer(g,curNeighbor);
							break;
						case 1:
							speak.fillLabelBuffer(g,curNeighbor);
							break;
						case 2:
							speak.calcuGlobalFrequency(g, curNeighbor);
							break;
						case 3:
							speak.determineAndAlterLabel(g, curNeighbor);
							break;
						case 4:
							ec.extractCommunity(g, curNeighbor);
							break;
						default:
							break;
						}
						q.offer(curNeighbor);
						
						g.visited.add(neighborName);
//						System.out.println("�ѷ��ʽڵ㣺"+neighbor.vertexName);
					}//if
				}//for
			}//while
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
