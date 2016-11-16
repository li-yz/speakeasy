package serialprocess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CooccurMatrix {
	//锟斤拷一锟斤拷锟斤拷锟斤拷映锟斤拷锟斤拷锟斤拷示锟斤拷锟斤拷锟斤拷锟�
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
		//锟斤拷锟节碉拷u锟斤拷诘锟絭锟斤拷锟斤拷锟斤拷锟街撅拷锟酵拷锟斤拷也锟轿拷眨锟斤拷锟紸[u][v]+1
		if( this.matrix==null|| !this.matrix.containsKey(unode)){
			System.out.println("共生矩阵新的一行,第"+uIndex+"行");
			Map<String, Integer>firstMap=new HashMap<String, Integer>();
			firstMap.put(vnode, 1);
			this.matrix.put(unode, firstMap);
		}else{
			System.out.println("共生矩阵的第"+uIndex+"行已存在");
			Map<String, Integer>firstMap2=this.matrix.get(unode);
			if(firstMap2.containsKey(vnode)){//锟节碉拷u锟斤拷v锟斤拷值锟揭伙拷锟斤拷
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
					vmap.put(unode, value);
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
