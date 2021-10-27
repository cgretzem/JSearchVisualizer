package search;


import java.util.ArrayList;
import java.util.HashSet;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.event.ActionEvent;  
import javafx.event.EventHandler;
import javafx.geometry.Insets;  
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
	private boolean hasStart = true;
	private boolean hasGoal = true;
	private String searchType = "ASTAR";
	private Paint dragColor;

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
		
		for(Node[] outer : graph.getGrid())
		{
			for(Node n : outer)
			{
				Rectangle r = new Rectangle(n.getX()*nodeSize, n.getY()*nodeSize, nodeSize, nodeSize);
				r.setStroke(Color.BLACK);
				
				if(n.equals(graph.getGoal()))
					r.setFill(Color.RED);
				
				else if(n.equals(graph.getStart()))
					r.setFill(Color.GREEN);
				
				else
					r.setFill(Color.WHITE);
				
				
				r.setOnMouseDragReleased(new EventHandler<MouseDragEvent>()
				{

					@Override
					public void handle(MouseDragEvent arg0)
					{
						if(dragColor == Color.GREEN)
						{
							r.setFill(Color.GREEN);
							graph.setStart((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
						}
							
						
						else if(dragColor == Color.RED)
						{
							r.setFill(Color.RED);
							graph.setGoal((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
						}
						dragColor = null;
					}
					
				});
				
				
				
				r.setOnMouseDragExited(new EventHandler<MouseDragEvent>() 
				{

					@Override
					public void handle(MouseDragEvent arg0)
					{
						if(!started)
						{
							
							if(dragColor == Color.GREEN && r.getFill() == Color.GREEN)
								r.setFill(Color.WHITE);
							
							else if(dragColor == Color.RED && r.getFill() == Color.RED)
								r.setFill(Color.WHITE);
							
						}
					}
					
				});
				
				
				r.setOnMouseDragEntered(new EventHandler<MouseDragEvent>() 
				{
					
					@Override
					public void handle(MouseDragEvent arg0)
					{

						if(!started)
						{
							if(dragColor == Color.BLACK)
							{
								if(r.getFill() == Color.WHITE)
								{
									r.setFill(Color.BLACK);
									graph.addWall((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
								}
							}
							
							else if(dragColor == Color.WHITE)
							{
								if(r.getFill() == Color.BLACK)
								{
									r.setFill(Color.WHITE);
									graph.removeWall((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
								}
							}
							
							else if(dragColor == Color.GREEN && r.getFill() == Color.WHITE)
							{
								r.setFill(Color.GREEN);
								graph.setStart((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
							}
							
							else if(dragColor == Color.RED && r.getFill() == Color.WHITE)
							{
								r.setFill(Color.RED);
								graph.setGoal((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
							}
							
						}
					}
					
				});
				
				r.setOnDragDetected(new EventHandler<MouseEvent>()
				{
					
					@Override
					public void handle(MouseEvent me)
					{
						
						if(!started)
						{
							r.startFullDrag();
							if(r.getFill() == Color.WHITE)
							{
								dragColor = Color.BLACK;
							}
							else if(r.getFill() == Color.BLACK)
							{
								dragColor = Color.WHITE;
							}
							else if(r.getFill() == Color.GREEN)
							{
								dragColor = Color.GREEN;
								r.setFill(Color.WHITE);
							}
							else if(r.getFill() == Color.RED)
							{
								dragColor = Color.RED;
								r.setFill(Color.WHITE);
							}
							r.startFullDrag();
						}
					}
							
				});
				
				r.setOnMouseClicked(new EventHandler<MouseEvent>() 
				{
					@Override
					public void handle(MouseEvent me) 
					{
						
						if(!started)
						{
							if(r.getFill() == Color.WHITE)
							{
								r.setFill(Color.BLACK);
								graph.addWall((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
							}
							
							else if(r.getFill() == Color.BLACK)
							{
								r.setFill(Color.WHITE);
								graph.removeWall((int)r.getX()/nodeSize, (int)r.getY()/nodeSize);
							}
						}
						
						
					}});
				recGroup.getChildren().add(r);
			}
			
		}
		
		
		
		Button startSearch = new Button("Start Search!");
		startSearch.setOnAction(new EventHandler<ActionEvent>() 
		{

			@Override
			public void handle(ActionEvent ae) 
			{
				if(finished && !started)
				{
					resetSearch();
				}
				if(hasStart && hasGoal && !started)
				{
					Task<Node> task = new Task<Node>()
					{
						@Override
						protected Node call() throws Exception {
							finalNode = graph.search(graph.getStart(), graph.getGoal(), searchType);
							return finalNode;
						}
							
					};
					started = true;
					task.setOnSucceeded(e ->
					{
						started = false;
						if(finalNode != null)
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
		
		
		VBox algoControls = new VBox(10);
		Label controlLabel = new Label("Select an Algorithm: ");
		algoControls.getChildren().add(controlLabel);
		algoControls.getChildren().add(astar);
		algoControls.getChildren().add(bfs);
		algoControls.getChildren().add(gbfs);
		
		VBox graphControls = new VBox(10);
		graphControls.getChildren().add(startSearch);
		graphControls.getChildren().add(resetGraph);
		
		VBox menu = new VBox(50);
		menu.getChildren().add(algoControls);
		menu.getChildren().add(graphControls);
		menu.setPadding(new Insets(20, 10, 10, 10));
		hbox.getChildren().add(recGroup);
		hbox.getChildren().add(menu);
		root.getChildren().add(hbox);
		
		stage.setScene(scene);
		stage.show();

		
	}
	
	private void reset()
	{
		HBox hbox1 = (HBox)root.getChildren().get(0);
		VBox vbox1 = (VBox)hbox1.getChildren().get(1);
		if(vbox1.getChildren().size() == 3)
			vbox1.getChildren().remove(2);
		
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
			else if(r.getX()/nodeSize == graph.getGoal().getX() && r.getY()/nodeSize == graph.getGoal().getY())
			{
				r.setFill(Color.RED);
			}
			else if(r.getX()/nodeSize == graph.getStart().getX() && r.getY()/nodeSize == graph.getStart().getY())
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
		VBox vbox1 = (VBox)hbox1.getChildren().get(1);
		if(vbox1.getChildren().size() == 3)
			vbox1.getChildren().remove(2);
		
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
			else if(r.getX()/nodeSize == graph.getGoal().getX() && r.getY()/nodeSize == graph.getGoal().getY())
			{
				r.setFill(Color.RED);
			}
			else if(r.getX()/nodeSize == graph.getStart().getX() && r.getY()/nodeSize == graph.getStart().getY())
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
		int count = 1;
		String displayPath = "Nodes expanded : " + graph.getNodesExpanded() +"\nPath Found :"; 
		ArrayList<String> path = graph.getPath(finalNode);

		Node currentNode = graph.getStart();
		for(String s : path)
		{
			displayPath +="\n"+count +". " + s;
			count++;
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
		HBox hbox1 = (HBox)root.getChildren().get(0);
		VBox vbox1 = (VBox)hbox1.getChildren().get(1);
		ScrollPane pane = new ScrollPane();
		TextArea p = new TextArea();
		p.setText(displayPath);
		pane.setContent(p);
		pane.setFitToWidth(true);
		pane.setPrefWidth(50);
		vbox1.getChildren().add(pane);
		
	}


	public void handleSearchEvent(SearchEvent event)
	{
		
		ArrayList<Node> fringe = event.getGraph().getFringe();
		HashSet<Node> closed = event.getGraph().getClosed();

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
