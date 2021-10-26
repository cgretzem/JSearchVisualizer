package search;

import javafx.event.Event;
import javafx.event.EventType;
public class SearchEvent extends Event
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5019677483918129275L;
	
	public static final EventType<SearchEvent> EXPANDED = new EventType<>(SearchEvent.ANY, "EXPANDED");

	private Graph graph;
	
	public SearchEvent(EventType<? extends Event> eventType, Graph graph)
	{
		super(eventType);
		this.graph = graph;
	}
	
	public Graph getGraph()
	{
		return graph;
	}
}
