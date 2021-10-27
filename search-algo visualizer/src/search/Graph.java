package search;


import java.util.ArrayList;
import java.util.Collections;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Group;



public class Graph 
{
	private int width;
	private int height;
	private int actionCostLeft;
	private int actionCostRight;
	private int actionCostDown;
	private int actionCostUp;
	private int nodesExpanded;
	private Node[][] grid;
	private ArrayList<Node> walls, fringe, closed;
	Node start, goal;
	private Group root;
	
	public Graph(int width, int height, Group root)
	{
		goal = new Node(width/2+1, height/2);
		start = new Node(width/2-1, height/2);
		this.root = root;
		nodesExpanded = 0;
		actionCostLeft = 1;
		actionCostRight = 1;
		actionCostDown = 1;
		actionCostUp = 1;
		walls = new ArrayList<Node>();
		closed = new ArrayList<Node>();
		fringe = new ArrayList<Node>();
		this.width = width;
		this.height = height;
		grid = new Node[width][height];
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				grid[i][j] = new Node(i, j);
			}
		}
		

	}
	
	public void resetNodes() 
	{
		goal = new Node(width/2+1, height/2);
		start = new Node(width/2-1, height/2);
		nodesExpanded = 0;
		walls = new ArrayList<Node>();
		closed = new ArrayList<Node>();
		fringe = new ArrayList<Node>();
	}
	
	public void resetSearch()
	{
		nodesExpanded = 0;
		closed = new ArrayList<Node>();
		fringe = new ArrayList<Node>();
	}
	
	
	public void removeWall(int x, int y)
	{
		for(int i = 0; i < walls.size(); i++)
		{
			Node w = walls.get(i);
			if(w.getX() == x && w.getY() == y)
			{
				walls.remove(i);
				break;
			}
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	private ArrayList<Node> expand(Node n)
	{
		ArrayList<Node> children = new ArrayList<Node>();
		if(n.getX() > 0)
		{
			Node temp = new Node(n.getX()-1, n.getY());
			temp.setParent(n);
			temp.setCost(n.getCost() + actionCostLeft);
			temp.setDepth(n.getDepth()+1);
			if(!isWall(temp))
			{
				children.add(temp);
			}
		}
		
		if(n.getX() < width-1)
		{
			Node temp = new Node(n.getX()+1, n.getY());
			temp.setParent(n);
			temp.setCost(n.getCost() + actionCostRight);
			temp.setDepth(n.getDepth()+1);
			if(!isWall(temp))
			{
				children.add(temp);
			}
		}
		
		if(n.getY() > 0)
		{
			Node temp = new Node(n.getX(), n.getY()-1);
			temp.setParent(n);
			temp.setCost(n.getCost() + actionCostDown);
			temp.setDepth(n.getDepth()+1);
			if(!isWall(temp))
			{
				children.add(temp);
			}
		}
		
		if(n.getY() < height-1)
		{
			Node temp = new Node(n.getX(), n.getY()+1);
			temp.setParent(n);
			temp.setCost(n.getCost() + actionCostUp);
			temp.setDepth(n.getDepth()+1);
			if(!isWall(temp))
			{
				children.add(temp);
			}
		}
		
		return children;
	}
	
	
	private int getManhatten(Node n1, Node n2)
	{
		int disX = Math.abs(n1.getX() - n2.getX());
		int disY = Math.abs(n1.getY() - n2.getY());
		return disX + disY;
	}
	
	private int chooseFromFringe(String method, ArrayList<Node> fringe)
	{
		switch(method)
		{
			case "GBFS":
			{
				int minMan = getManhatten(fringe.get(0), goal);
				int minIndex = 0;
				for(int i = 0; i < fringe.size(); i++)
				{
					Node n = fringe.get(i);
					if(getManhatten(n, goal) < minMan)
					{
						minMan = getManhatten(n, goal);
						minIndex = i;
					}
				}
				return minIndex;
				
				
			}
			
			case "BFS":
			{
				int min = fringe.get(0).getDepth();
				int minIndex = 0;
				for(int i = 0; i < fringe.size(); i++)
				{
					Node n = fringe.get(i);
					if(n.getDepth() < min)
					{
						min = n.getDepth();
						minIndex = i;
					}
				}
				return minIndex;
			}
			
			case "ASTAR":
			{
				//h(n) + g(n)
				int min = getManhatten(fringe.get(0), goal) + fringe.get(0).getCost();
				int minIndex = 0;
				for(int i = 0; i < fringe.size(); i++)
				{
					Node n = fringe.get(i);
					//System.out.println("Manhatten: " + getManhatten(n, goal) + "\nCost : " + n.getCost());
					if(getManhatten(n, goal)+ n.getCost() <= min)
					{
						min = getManhatten(n, goal) + n.getCost();
						minIndex = i;
					}
					
				}
				//System.out.println("LOWEST -- " + getManhatten(fringe.get(minIndex), goal) + "\n" + fringe.get(minIndex).getCost() + "\n---------------");
				return minIndex;
			
			}
			default:
			{
				return -1;
			}
		}
	}
	
	
	public boolean isWall(Node n)
	{
		for(Node node : walls)
		{
			if(node.getX() == n.getX() && node.getY() == n.getY())
			{
				return true;
			}
		}
		return false;
	}
	
	
	public void addWall(int wallX, int wallY)
	{
		Node n = new Node(wallX, wallY);
		walls.add(n);
	}
	
	public Node search(Node start, Node end, String method) throws InterruptedException
	{
		goal = end;
		this.start = start;
		
		
		Node init = start;
		fringe.add(init);

		
		while(fringe.isEmpty() == false)
		{
			
			Node temp = fringe.remove(chooseFromFringe(method, fringe));
			if(temp.getX() == goal.getX() && temp.getY() == goal.getY())
			{
				return temp;
			}
			boolean found = false;
			for(Node n : closed)
			{
				if(n.getX() == temp.getX() && temp.getY() == n.getY())
				{
					found = true;
				}
			}
			if(!found)
			{
				closed.add(temp);
				for(Node n : expand(temp))
				{
					fringe.add(n);
					//System.out.println("adding " + n.getX() + ", " + n.getY());
				}
				;
				Event.fireEvent(root, new SearchEvent(SearchEvent.EXPANDED, this));
				Thread.sleep(100);
				nodesExpanded++;
			}
		}
		return null;

	}
	
	public ArrayList<Node> getFringe()
	{
		return fringe;
	}
	
	public ArrayList<Node> getClosed()
	{
		return closed;
	}
	
	
	public Node getStart()
	{
		return start;
	}
	
	public Node getGoal()
	{
		return goal;
	}
	
	public void setStart(Node newStart)
	{
		start = newStart;
	}
	
	public void setGoal(Node newGoal)
	{
		goal = newGoal;
	}
	
	public ArrayList<String> getPath(Node goal)
	{
		ArrayList<String> path = new ArrayList<String>();
		Node temp = goal;
		while(temp.getParent() != null)
		{
			if(temp.getParent().getX() == temp.getX()-1)
			{
				path.add("right");
			}
			if(temp.getParent().getX() == temp.getX()+1)
			{
				path.add("left");
			}
			if(temp.getParent().getY() == temp.getY()+1)
			{
				path.add("up");
			}
			if(temp.getParent().getY() == temp.getY()-1)
			{
				path.add("down");
			}
			temp = temp.getParent();
		}
		Collections.reverse(path);
		return path;
	}
	
	public Node[][] getGrid()
	{
		return grid;
	}
	
	
	/*
	 * public static void main(String[] args) { Graph graph = new Graph(10, 10);
	 * for(int i = 0; i < 7; i++) { graph.addWall(4, i); } graph.addWall(1, 0); Node
	 * finalNode = graph.search(0,0, 9, 0, "ASTAR"); if(finalNode != null) {
	 * ArrayList<String> path = graph.getPath(finalNode); for(int i = path.size()-1;
	 * i >=0; i--) { System.out.println(path.get(i)); }
	 * System.out.println("Nodes Expanded: " + graph.nodesExpanded); } else {
	 * System.out.println("NO PATH FOUND"); }
	 * 
	 * }
	 */
}
