package search;


import java.util.ArrayList;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.*;
import javafx.event.ActionEvent;  
import javafx.event.EventHandler;  
public class Visualizer extends Application
{
	private static final int width = 1000;
	private static final int height = 800;
	private static final int nodeSize = 21;
	private boolean hasGoal = false;
	private boolean hasStart = false;
	private Graph graph;
	private Group root;
	private Group recGroup;
	boolean started = false;
	private Node finalNode;
	@Override
	public void start(Stage stage) throws Exception
	{
		root = new Group();
		HBox hbox = new HBox(nodeSize);
		recGroup = new Group();
		graph = new Graph(width/nodeSize, height/nodeSize, root);
		
		root.addEventFilter(SearchEvent.EXPANDED, this::handleSearchEvent);
		stage.setTitle("SEARCH ALGORITHM VISUALIZER");
		Scene scene = new Scene(root, width, height);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{

			@SuppressWarnings("unchecked")
			@Override
			public void handle(KeyEvent event) 
			{
				System.out.println(event.getCode());
				if(event.getCode() == KeyCode.B)
				{
					System.out.println("Test");
					Task task = new Task<Void>()
					{
						@Override
						protected Void call() throws Exception {
							finalNode = graph.search(graph.start, graph.goal, "GBFS");
							return null;
						}
							
					};
					started = true;
					task.setOnSucceeded(e ->
					{
						drawPath(finalNode);
					});
					
					new Thread(task).start();	
				}
				
			}});
		for(Node[] outer : graph.getGrid())
		{
			for(Node n : outer)
			{
				Rectangle r = new Rectangle(n.getX()*nodeSize, n.getY()*nodeSize, nodeSize, nodeSize);
				r.setStroke(Color.BLACK);
				r.setFill(Color.WHITE);
				r.setOnMouseClicked(new EventHandler<MouseEvent>() 
				{
					@Override
					public void handle(MouseEvent me) 
					{
						
						if(!started)
						{
							if(!hasStart)
							{
								r.setFill(Color.GREEN);
								graph.setStart(new Node((int)r.getX()/nodeSize, (int)r.getY()/nodeSize));
								hasStart = true;
								System.out.println(graph.getStart().getY());
							}
							else
							{
								if(!hasGoal)
								{
									if(r.getFill() == Color.WHITE)
									{
										r.setFill(Color.RED);
										graph.setGoal(new Node((int)r.getX()/nodeSize, (int)r.getY()/nodeSize));
										hasGoal = true;
									}
									if(r.getFill() == Color.GREEN)
									{
										r.setFill(Color.WHITE);
										graph.setGoal(new Node((int)r.getX(), (int)r.getY()));
										hasStart = false;
									}
									
								}
								else
								{
									if(r.getFill() == Color.RED)
									{
										r.setFill(Color.WHITE);
										graph.setGoal(new Node((int)r.getX(), (int)r.getY()));
										hasGoal = false;
									}
								}
							}
						
						}
						else
						{
							for(Node n : graph.getClosed())
							{
								if(n.getX() == r.getX()/nodeSize && n.getY() == r.getY()/nodeSize)
								{
									System.out.println("COST : " + n.getCost() +"\nDISTANCE : " + graph.getManhatten(n, graph.getGoal()));
								}
							}
							
						}
						
					}});
				recGroup.getChildren().add(r);
			}
			
		}
		
		hbox.getChildren().add(recGroup);
		root.getChildren().add(hbox);
		
		stage.setScene(scene);
		stage.show();

		
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
