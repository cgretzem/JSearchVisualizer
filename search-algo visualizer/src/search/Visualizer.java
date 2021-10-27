package search;


import java.util.ArrayList;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.event.ActionEvent;  
import javafx.event.EventHandler;  
public class Visualizer extends Application
{
	private static final int width = 1000;
	private static final int height = 800;
	private static final int nodeSize = 21;

	boolean finished = false;
	private Graph graph;
	private Group root;
	private Group recGroup;
	boolean started = false;
	private Node finalNode;
	private boolean drag = false;
	private String dragNode;
	private boolean hasStart = true;
	private boolean hasGoal = true;
	private String searchType = "ASTAR";
	@Override
	public void start(Stage stage) throws Exception
	{
		root = new Group();
		HBox hbox = new HBox(nodeSize);
		recGroup = new Group();
		graph = new Graph(width/nodeSize - 10, height/nodeSize, root);
		
		root.addEventFilter(SearchEvent.EXPANDED, this::handleSearchEvent);
		stage.setTitle("SEARCH ALGORITHM VISUALIZER");
		Scene scene = new Scene(root, width, height);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{

			
			@Override
			public void handle(KeyEvent event) 
			{
				System.out.println(event.getCode());
				if(event.getCode() == KeyCode.B)
				{
					
				}
				
			}});
		for(Node[] outer : graph.getGrid())
		{
			for(Node n : outer)
			{
				Rectangle r = new Rectangle(n.getX()*nodeSize, n.getY()*nodeSize, nodeSize, nodeSize);
				r.setStroke(Color.BLACK);
				if(n.getX() == graph.goal.getX() && n.getY() == graph.goal.getY())
				{
					r.setFill(Color.RED);
				}
				else if(n.getX() == graph.start.getX() && n.getY() == graph.start.getY())
				{
					r.setFill(Color.GREEN);
				}
				else
				{
					r.setFill(Color.WHITE);
				}
				r.setOnMouseClicked(new EventHandler<MouseEvent>() 
				{
					@Override
					public void handle(MouseEvent me) 
					{
						
						if(!started)
						{
							if(!drag)
							{
								if(r.getFill() == Color.WHITE)
								{
									r.setFill(Color.BLACK);
									graph.addWall((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
								}
								
								else if(r.getFill() == Color.GREEN)
								{
									drag = true;
									dragNode = "GREEN";
									r.setFill(Color.WHITE);
									hasStart = false;
								}
								else if(r.getFill() == Color.RED)
								{
									drag = true;
									dragNode = "RED";
									r.setFill(Color.WHITE);
									hasGoal = false;
								}
								else if(r.getFill() == Color.BLACK)
								{
									r.setFill(Color.WHITE);
									graph.removeWall((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
								}
							}
							else
							{
								if(dragNode.equals("RED") && r.getFill() != Color.GREEN)
								{
									graph.setGoal(new Node((int)r.getX()/nodeSize, (int)r.getY()/nodeSize));
									r.setFill(Color.RED);
									drag = false;
									hasGoal = true;
								}
								if(dragNode.equals("GREEN") && r.getFill() != Color.RED)
								{
									graph.setStart(new Node((int)r.getX()/nodeSize, (int)r.getY()/nodeSize));
									r.setFill(Color.GREEN);
									drag = false;
									hasStart = true;
								}
							}
							
						}
						
						
					}});
				recGroup.getChildren().add(r);
			}
			
		}
		
		
		
		Button startSearch = new Button("Start Search!");
		startSearch.setOnAction(new EventHandler<ActionEvent>() 
		{

			@SuppressWarnings("unchecked")
			@Override
			public void handle(ActionEvent ae) 
			{
				if(finished && !started)
				{
					resetSearch();
				}
				if(hasStart && hasGoal && !started)
				{
					Task task = new Task<Void>()
					{
						@Override
						protected Void call() throws Exception {
							finalNode = graph.search(graph.start, graph.goal, searchType);
							return null;
						}
							
					};
					started = true;
					task.setOnSucceeded(e ->
					{
						started = false;
						drawPath(finalNode);
						finished = true;
					});
					
					new Thread(task).start();	
					
				}
			}
					
		});
		Button resetGraph = new Button("Reset Graph");
		resetGraph.setOnAction(new EventHandler<ActionEvent>() 
		{

			@SuppressWarnings("unchecked")
			@Override
			public void handle(ActionEvent ae) 
			{
				reset();
			}
					
		});
		ToggleGroup searchTypes = new ToggleGroup();
		RadioButton astar = new RadioButton("A*");
		astar.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) {searchType = "ASTAR";	}
		});
		astar.setToggleGroup(searchTypes);
		astar.setSelected(true);
		RadioButton bfs = new RadioButton("Breadth First Search");
		bfs.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) {searchType = "BFS";	}
		});
		bfs.setToggleGroup(searchTypes);
		RadioButton gbfs = new RadioButton("Greedy Best First Search");
		gbfs.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) {searchType = "GBFS";	}
		});
		gbfs.setToggleGroup(searchTypes);
		
		VBox menu = new VBox(10);
		menu.getChildren().add(astar);
		menu.getChildren().add(bfs);
		menu.getChildren().add(gbfs);
		menu.getChildren().add(startSearch);
		menu.getChildren().add(resetGraph);
		hbox.getChildren().add(recGroup);
		hbox.getChildren().add(menu);
		root.getChildren().add(hbox);
		
		stage.setScene(scene);
		stage.show();

		
	}
	
	private void reset()
	{
		HBox hbox1 = (HBox)root.getChildren().get(0);
		Group recGroup1 = (Group)hbox1.getChildren().get(0);
		graph.resetNodes();
		finished = false;
		for(int i = 0; i < recGroup1.getChildren().size(); i++)
		{
			Rectangle r = (Rectangle)recGroup1.getChildren().get(i);
			if(r.getWidth() == 1 || r.getHeight() ==1 )
			{
				recGroup1.getChildren().remove(i);
				i--;
			}
			else if(r.getX()/nodeSize == graph.goal.getX() && r.getY()/nodeSize == graph.goal.getY())
			{
				r.setFill(Color.RED);
			}
			else if(r.getX()/nodeSize == graph.start.getX() && r.getY()/nodeSize == graph.start.getY())
			{
				r.setFill(Color.GREEN);
			}
			else
			{
				r.setFill(Color.WHITE);
			}
			
		}
	}
	
	private void resetSearch()
	{
		HBox hbox1 = (HBox)root.getChildren().get(0);
		Group recGroup1 = (Group)hbox1.getChildren().get(0);
		graph.resetSearch();
		finished = false;
		for(int i = 0; i < recGroup1.getChildren().size(); i++)
		{
			Rectangle r = (Rectangle)recGroup1.getChildren().get(i);
			if(r.getWidth() == 1 || r.getHeight() ==1 )
			{
				recGroup1.getChildren().remove(i);
				i--;
			}
			else if(r.getX()/nodeSize == graph.goal.getX() && r.getY()/nodeSize == graph.goal.getY())
			{
				r.setFill(Color.RED);
			}
			else if(r.getX()/nodeSize == graph.start.getX() && r.getY()/nodeSize == graph.start.getY())
			{
				r.setFill(Color.GREEN);
			}
			else if(!graph.isWall(new Node((int)r.getX()/nodeSize,(int)r.getY()/nodeSize )))
			{
				r.setFill(Color.WHITE);
			}
			
		}			
	}
	private void drawPath(Node goal) 
	{
		
		ArrayList<String> path = graph.getPath(finalNode);
		
		
		
		
		
		Node currentNode = graph.start;
		for(String s : path)
		{
			switch(s)
			{
			case "down":
				Rectangle down = new Rectangle(currentNode.getX()*nodeSize + nodeSize/2, currentNode.getY()*nodeSize + nodeSize/2, 1, nodeSize);
				down.setFill(Color.YELLOW);
				down.setStroke(Color.YELLOW);
				currentNode = new Node(currentNode.getX(), currentNode.getY()+1);
				((Group)((HBox)root.getChildren().get(0)).getChildren().get(0)).getChildren().add(down);
				break;
			case "up":
				Rectangle up = new Rectangle(currentNode.getX()*nodeSize + nodeSize/2, currentNode.getY()*nodeSize - nodeSize + nodeSize/2, 1, nodeSize);
				up.setFill(Color.YELLOW);
				up.setStroke(Color.YELLOW);
				currentNode = new Node(currentNode.getX(), currentNode.getY()-1);
				((Group)((HBox)root.getChildren().get(0)).getChildren().get(0)).getChildren().add(up);
				break;
			case "left":
				Rectangle left = new Rectangle(currentNode.getX()*nodeSize - nodeSize + nodeSize/2, currentNode.getY()*nodeSize  +nodeSize/2, nodeSize, 1);
				left.setFill(Color.YELLOW);
				left.setStroke(Color.YELLOW);
				currentNode = new Node(currentNode.getX()-1, currentNode.getY());
				((Group)((HBox)root.getChildren().get(0)).getChildren().get(0)).getChildren().add(left);
				break;
			case "right":
				Rectangle right = new Rectangle(currentNode.getX()*nodeSize + nodeSize/2, currentNode.getY()*nodeSize + nodeSize/2, nodeSize, 1);
				right.setFill(Color.YELLOW);
				right.setStroke(Color.YELLOW);
				currentNode = new Node(currentNode.getX()+1, currentNode.getY());
				((Group)((HBox)root.getChildren().get(0)).getChildren().get(0)).getChildren().add(right);
				break;
			}
			
				
			
		}
		
		
	}


	public void handleSearchEvent(SearchEvent event)
	{
		
		ArrayList<Node> fringe = event.getGraph().getFringe();
		ArrayList<Node> closed = event.getGraph().getClosed();

			for(Node fNode : fringe)
			{
				//System.out.println(fNode.getX() + ", " + fNode.getY());
				//gets rectangle - disgusting
				Rectangle n = ((Rectangle) ((Group)((HBox)root.getChildren().get(0)).getChildren().get(0)).getChildren().get(fNode.getX()* (height/nodeSize)+fNode.getY()));
				
				if(n.getX()/nodeSize == (double)fNode.getX() && n.getY()/nodeSize == (double)fNode.getY())
				{
					if(n.getFill() != Color.GREEN && n.getFill() != Color.RED)
					{
						n.setFill(Color.BLUE);
					}
				}
			}
			for(Node fNode : closed)
			{
				Rectangle n = ((Rectangle) ((Group)((HBox)root.getChildren().get(0)).getChildren().get(0)).getChildren().get(fNode.getX()* (height/nodeSize)+fNode.getY()));
				if(n.getX()/nodeSize == (double)fNode.getX() && n.getY()/nodeSize == (double)fNode.getY())
				{
					if(n.getFill() != Color.GREEN && n.getFill() != Color.RED)
					{
						n.setFill(Color.GREY);
					}
						
				}
			}
		
	}
	public static void main(String[] args)
	{
		launch(args);
	}
}
