package serialprocess;

import java.io.Serializable;

/**
 * ���࣬�����洢�߶���
 * @author Administrator
 *
 */
public class Edges implements Serializable{
	VertexNode fromNode;
	VertexNode toNode;
	public Edges() {
		// TODO Auto-generated constructor stub
	}
	public Edges(VertexNode fNode,VertexNode tNode){
		this.fromNode=fNode;
		this.toNode=tNode;
	}
}
