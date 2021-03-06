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
	public void communityDetectProcedure(Graph g ,Partition partition) {
		System.out.println("一次社区发现迭代，开始");

		/**
		 * 全局变量，初始化以后网络图中总的标签数
		 * @param v
		 */
	//repeat;
		
		//按照SpeakEasy算法的步骤对图进行遍历，
		//遍历的过程中做相应的处理（初始化节点的buffer（分两步0、1）、计算全局概率分布2、确定要更新的标签3）
		for(int funcIndex=0;funcIndex < 4;funcIndex++){
			g.BFSTraverse(g,funcIndex,partition);
			
			//注意每遍历一次之后，所有节点都被访问过了，为了再进行其他处理，要把存放已访问节点的集合清空
			g.visited.clear();
		}
		//迭代标签更新的过程，执行多次，直到节点的buffer不再有显著变化，即收敛
		for(int repeatTime=0;repeatTime < 200;repeatTime++){
			g.globalFrequencies.clear();
			g.BFSTraverse(g,2,partition);
			g.visited.clear();
			g.BFSTraverse(g,3,partition);
			g.visited.clear();
		}
	//repeat结束
		
		//识别社区
		g.BFSTraverse(g, 4,partition);
		g.visited.clear();
		
		//输出当前网络图的状态到文本
		ResultOutput ro=new ResultOutput();
		 try {
			ro.outputResult(g);//迭代更新50次后，保存当前网络图g的状态，即每个节点的buffer中的标签
			 ro.outputCommunities(g,partition);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("一次社区发现，完毕");
	}
}



