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
 * 下面是图类，图的基本元素：节点集、边集（邻接矩阵）、节点的数目.
 * 图类中应该有把图Graph初始化的方法，图的广度优先遍历算法，
 * @author Administrator
 *
 */
public class Graph{
	int vertexNum;
	int edgeNum;
	int bufferSize;
	
	//用map来存放（节点名称，和节点对象的引用）
	Map<String, VertexNode> map=new HashMap<String, VertexNode>();
	
	//存放已访问节点名
	Set<String> visited=new HashSet<String>();
	
	List<Edges> totalEdgesList=new ArrayList<Edges>();
	
	/**
	 * 
	 * 定义存放标签的全局概率的数据结构globalFrequencies，用hashmap来实现
	 */
	Map<String, Double>globalFrequencies=new HashMap<String,Double>();
	
	//最后识别出的社区集合C,其中包括C1,C2,...Ck等多个社区,key域代表一个社区的标记，value域中是在这个社区内的节点名集合
	Map<String, List<String>> Communities=new HashMap<String, List<String>>();
	
	//存储经过detection以后，节点及其对应的社区。key:节点名-value：社区标志名
	Map<String, String> nodeCommunity=new HashMap<String, String>();
	
	Graph()
	{
		
	}
	//带参构造器，初始化图的节点数，从而知道邻接矩阵的阶数
	Graph(int bSize,String filePath,int whetherRepeat)
	{
		this.bufferSize=bSize;
		switch (whetherRepeat){
			case 0:
				readData(filePath,totalEdgesList,map);//读文本初始化边集（就是图的邻接矩阵）
				break;
			case 1:
				readDataForRepeatEdges(filePath,totalEdgesList,map);//原数据集中的边有重复的，即一条边表示了两次
				break;
			default:
				break;
		}
	}
	
	
	/**
	 * 读取文本中的边集数据，在Graph含参数构造器中被调用一次。
	 * @param filepath
	 * @param edgelist，代表图的边集合， 调用的时候接受的是list的引用，对edgelist所指对象的改变等同于改变原引用所指的空间
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
				System.out.println("开始读取文件 ：");
				while((readrow=bf.readLine()) != null){
					
					String []str=readrow.split("\t");
					VertexNode from=new VertexNode(str[0]);
					VertexNode to=new VertexNode(str[1]);
						Edges newEdge=new Edges(from, to);
						//把读取到的这条边添加到图的边集合中
						edgelist.add(newEdge);
						
						//把from节点与to节点对应的节点都保存到map中，同时过滤重复的节点名
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
				System.out.println("读取完毕：有边相连的节点"+map.size()+"图的总边数"+edgelist.size());
				
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读取含有重复边的数据集，即一条边保存了两次，from-target,target-from形式
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
				System.out.println("开始读取文件 ：");
				while((readrow=bf.readLine()) != null){
					
					String []str=readrow.split("\t");
					VertexNode from=new VertexNode(str[0]);
					VertexNode to=new VertexNode(str[1]);
						Edges newEdge=new Edges(from, to);
						//把读取到的这条边添加到图的边集合中
						edgelist.add(newEdge);
						
						//每读取一条边只把from节点对应的节点保存到map中即可，同时过滤重复的节点名
						if(!map.containsKey(str[0])){
							from.neighborList.add(str[1]);
							map.put(str[0], from);
						}else{
							from=map.get(str[0]);
							from.neighborList.add(str[1]);
							map.put(str[0], from);
						}
						
				}
				System.out.println("读取完毕：有边相连的节点"+map.size()+"图的总边数"+edgelist.size());
				
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 广度优先遍历
	 * @param g 图
	 * @param functionIndex 如何访问一个节点的函数方法标号,0:初始化第一步  1：初始化第二步 填满每个buffer  2：计算全局概率  3：确定每个节点要更新的标签  4：提取社区划分结果
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
	 * 广度优先遍历，从一个顶点开始
	 * @param g 图g
	 * @param v 遍历开始的节点,是图的map类型节点集合中对应的节点引用
	 * @param q 用来保存已访问节点的队列
	 * @param functionIndex SpeakEasy类中的方法标号，标号值代表初始化到更新标签的数序，
	 * 决定在遍历图的时候对节点进行怎样的处理（初始化、计算全局概率分布、更新节点的buffer等操作）
	 */
	public void BFS(Graph g,VertexNode v,Queue<VertexNode>q,int functionIndex){
		//访问节点v，调用SpeakEasy方法

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
						//访问该节点
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
//						System.out.println("已访问节点："+neighbor.vertexName);
					}//if
				}//for
			}//while
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
