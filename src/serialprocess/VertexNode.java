package serialprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * �ڵ��࣬�����У��ڵ�name���Ƿ���ʡ��ڵ��labelBuffer�Ĵ�С
 * �������ڵ�Ĵ������ĳ�ʼ��������
 * @author Administrator
 *
 */
public class VertexNode{
	String vertexName;
	
	//������ڽڵ������
	List<String> neighborList=new ArrayList<String>();
	
	//�ڵ�Ļ�����buffer
	List<String> labelBuffer=new ArrayList<String>();
	
	public VertexNode()
	{
		
	}
	public VertexNode(String name)
	{
		this.vertexName=name;
	}
}
