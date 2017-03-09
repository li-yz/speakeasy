package serialprocess;

import java.io.Serializable;
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
public class VertexNode implements Serializable{
	public String vertexName;
	
	//������ڽڵ������
//	public List<String> neighborList=new ArrayList<String>();
	 public Map<String,Double>neighborList = new HashMap<String, Double>();

	//�ڵ�Ļ�����buffer
	public List<String> labelBuffer=new ArrayList<String>();

	public VertexNode()
	{
		
	}
	public VertexNode(String name)
	{
		this.vertexName=name;
	}
}
