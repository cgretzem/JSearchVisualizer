package search;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javafx.event.Event;

import javafx.scene.Group;



public class Graph 
{
	private int width;
	private int height;
	
	private int nodesExpanded;
	private Node[][] grid;
	private ArrayList<Node> fringe;
	private HashSet<Node> closed, walls;
	private Node start, goal;
	private Group root;
	
	public Graph(int width, int height, Group root)
	{
		this.root = root;
		nodesExpanded = 0;
		walls = new HashSet<Node>();
		closed = new HashSet<Node>();
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
		goal = grid[width/2+2][height/2];
		start = grid[width/2-2][height/2];
		

	}
	
	public int getNodesExpanded()
	{
		return nodesExpanded;
	}
	
	public void resetNodes() 
	{
		
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				grid[i][j].resetNode(i, j);
			}
		}
		goal = grid[width/2+2][height/2];
		start = grid[width/2-2][height/2];
		nodesExpanded = 0;
		walls.clear();
		closed.clear();
		fringe.clear();
	}
	
	public void resetSearch()
	{
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				grid[i][j].resetNode(i, j);
			}
		}
		nodesExpanded = 0;
		closed.clear();
		fringe.clear();
	}
	
	
	public void removeWall(int x, int y)
	{
		walls.remove(grid[x][y]);
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
		Node temp = null;
		ArrayList<Node> children = new ArrayList<Node>();
		if(n.getX() > 0)
		{
			temp = grid[n.getX()-1][n.getY()];
			if(!closed.contains(temp) && !walls.contains(temp) && !fringe.contains(temp))
			{
				temp.setParent(n);
				temp.setCost(n.getCost() + 1);
				temp.setDepth(n.getDepth()+1);
				children.add(temp);
			}
		}
		
		if(n.getX() < width-1)
		{
			temp = grid[n.getX()+1][n.getY()];
			if(!closed.contains(temp) && !walls.contains(temp)&& !fringe.contains(temp))
			{
				temp.setParent(n);
				temp.setCost(n.getCost() + 1);
				temp.setDepth(n.getDepth()+1);
				children.add(temp);
			}
		}
		
		if(n.getY() > 0)
		{
			temp = grid[n.getX()] [n.getY()-1];
			if(!closed.contains(temp) && !walls.contains(temp)&& !fringe.contains(temp))
			{
				temp.setParent(n);
				temp.setCost(n.getCost() + 1);
				temp.setDepth(n.getDepth()+1);
				children.add(temp);
			}
		}
		
		if(n.getY() < height-1)
		{
			temp = grid[n.getX()] [n.getY()+1];
			if(!closed.contains(temp) && !walls.contains(temp)&& !fringe.contains(temp))
			{
				temp.setParent(n);
				temp.setCost(n.getCost() + 1);
				temp.setDepth(n.getDepth()+1);
				children.add(temp);
			}
			
		}
		n.setChildren(children);
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
					if(getManhatten(n, goal)+ n.getCost() <= min)
					{
						min = getManhatten(n, goal) + n.getCost();
						minIndex = i;
					}
					
				}
				return minIndex;
			}
			default:
				return -1;
		}
	}
	
	
	public boolean isWall(Node n)
	{
		return walls.contains(n);
	}
	
	
	public void addWall(int wallX, int wallY)
	{
		
		walls.add(grid[wallX][wallY]);
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
			boolean found = closed.contains(temp);
			if(!found)
			{
				closed.add(temp);
				fringe.addAll(expand(temp));
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
	
	public HashSet<Node> getClosed()
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
	
	public void setStart(int x, int y)
	{
		start = grid[x][y];
	}
	
	public void setGoal(int x, int y)
	{
		goal = grid[x][y];
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
}
