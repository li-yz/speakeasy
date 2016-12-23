package serialprocess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 计算共生矩阵，共生矩阵中元素值得含义是：在多次运行社区划分算法时，两个网络图节点被 划分到同一个社区中的次数
 */
public class CooccurMatrix {
	Map<String, Map<String, Integer>> matrix;

	public CooccurMatrix(){
		this.matrix=new HashMap<String, Map<String,Integer>>();
	}
	/**
	 * 对矩阵元素的put操作,相应元素值加1
	 * 
	 * 注意：Java内在机制：一个对象调用一个方法时，隐含的把这个对象的引用也传递到这个方法了，
	 * 可以使用this关键字来使用这个对象的引用。
	 */
	public void addMatrixValue(String unode,String vnode,int uIndex,int vIndex){
		if( this.matrix==null|| !this.matrix.containsKey(unode)){
//			System.out.println("共生矩阵新的一行,第"+uIndex+"行");
			Map<String, Integer>firstMap=new HashMap<String, Integer>();
			firstMap.put(vnode, 1);
			this.matrix.put(unode, firstMap);
		}else{
//			System.out.println("共生矩阵的第"+uIndex+"行已存在");
			Map<String, Integer>firstMap2=this.matrix.get(unode);
			if(firstMap2.containsKey(vnode)){
				int times=firstMap2.get(vnode)+1;
				firstMap2.put(vnode, times);
				this.matrix.put(unode, firstMap2);
			}else{
				firstMap2.put(vnode, 1);
				this.matrix.put(unode, firstMap2);
			}
		}
	}
	
	/**
	 * make the matrix symmetric if the original matrix is not
	 * use this to operate the object
	 */
	public void symmetricMatrix(){
		Map<String, Map<String, Integer>> matrixNew=new HashMap<String, Map<String,Integer>>();
		Iterator symIter=this.matrix.entrySet().iterator();
		while(symIter.hasNext()){
			Map.Entry entry=(Map.Entry)symIter.next();
			String unode=(String)entry.getKey();
			Map<String, Integer> secMap=this.matrix.get(unode);
			Iterator secIter=secMap.entrySet().iterator();
			while(secIter.hasNext()){
				Map.Entry secEntry=(Map.Entry)secIter.next();
				String vnode=(String)secEntry.getKey();
				int value=secMap.get(vnode);
				if(matrixNew.containsKey(vnode)){
					Map<String, Integer> vmap=matrixNew.get(vnode);
					if(vmap.containsKey(unode)){
						value+=vmap.get(unode);
						vmap.put(unode, value);
					}else {
						vmap.put(unode, value);
					}
					matrixNew.put(vnode, vmap);
				}else{
					Map<String, Integer> newMap=new HashMap<String, Integer>();
					newMap.put(unode, value);
					matrixNew.put(vnode, newMap);
				}
				
			}
		}
		this.matrix.putAll(matrixNew);
	}
}
