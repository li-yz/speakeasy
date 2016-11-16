package serialprocess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SpeakEasy算法的第一步：发现社区
 * 对节点的visit方法应该放在此类中，调用图类中的遍历算法，然后visit每个节点
 * @author Administrator
 *
 */
public class GraphSearch {
	public void communityDetectProcedure(Graph g) {
		System.out.println("查看边数："+g.totalEdgesList.size());
		System.out.println("查看节点数："+g.map.size());//注意在图中有孤立的节点
		
		/**
		 * 全局变量，初始化以后网络图中总的标签数
		 * @param v
		 */
		int totalLabelNum=0;
	//repeat;
		
		//按照SpeakEasy算法的步骤对图进行遍历，
		//遍历的过程中做相应的处理（初始化节点的buffer（分两步0、1）、计算全局概率分布2、确定要更新的标签3）
		for(int funcIndex=0;funcIndex < 4;funcIndex++){
			g.BFSTraverse(g,funcIndex);
			
			//注意没遍历一次之后，所有节点都被访问过了，为了再进行其他处理，要把存放已访问节点的集合清空
			g.visited.clear();
		}
		//迭代标签更新的过程，执行多次，直到节点的buffer不再有显著变化，即收敛
		for(int repeatTime=0;repeatTime <2;repeatTime++){
			g.globalFrequencies.clear();
			g.BFSTraverse(g,2);
			g.visited.clear();
			g.BFSTraverse(g,3);
			g.visited.clear();
		}
	//repeat结束
		
		//识别社区
		g.BFSTraverse(g, 4);
		g.visited.clear();
		
		//输出结果到文本
		System.out.println("一次传播更新完毕");
		ResultOutput ro=new ResultOutput();
		 try {
			ro.outputResult(g);
			ro.outputCommunities(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}



