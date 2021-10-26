package search;

import java.util.ArrayList;
public class Node
{
	private int x, y;
	private Node parent;
	private ArrayList<Node> children;
	private int depth;
	private int pathCost;
	
	public Node(int x, int y)
	{

		this.parent = null;
		this.children = new ArrayList<Node>();
		this.depth = 0;
		this.pathCost = 0;
		this.x = x;
		this.y = y;
		
		
	}
	
	
	
	public void setChildren(ArrayList<Node> chil)
	{
		children = chil;
	}
	
	public void setParent(Node n)
	{
		parent = n;
	}
	
	public int getCost()
	{
		return pathCost;
	}
	
	public void setCost(int newCost)
	{
		pathCost = newCost;
	}
	
	public void setDepth(int newDepth)
	{
		depth = newDepth;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public Node getParent()
	{
		return parent;
	}

}
