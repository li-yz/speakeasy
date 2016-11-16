package serialprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * 节点类，属性有：节点name，是否访问、节点的labelBuffer的大小
 * 还包括节点的带参数的初始化构造器
 * @author Administrator
 *
 */
public class VertexNode{
	String vertexName;
	
	//存放相邻节点的名字
	List<String> neighborList=new ArrayList<String>();
	
	//节点的缓存区buffer
	List<String> labelBuffer=new ArrayList<String>();
	
	public VertexNode()
	{
		
	}
	public VertexNode(String name)
	{
		this.vertexName=name;
	}
}
