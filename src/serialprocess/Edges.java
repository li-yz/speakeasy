package serialprocess;

import java.io.Serializable;

/**
 * 边类，用来存储边对象
 * @author Administrator
 *
 */
public class Edges implements Serializable{
	VertexNode fromNode;
	VertexNode toNode;
	double weight;
	public Edges() {
		// TODO Auto-generated constructor stub
	}
	public Edges(VertexNode fNode,VertexNode tNode,double weight){
		this.fromNode=fNode;
		this.toNode=tNode;
		this.weight=weight;
	}
}
