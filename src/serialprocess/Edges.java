package serialprocess;

/**
 * 边类，用来存储边对象
 * @author Administrator
 *
 */
public class Edges{
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
