package com.gobslog.processing.connections;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import processing.core.PVector;

import com.gobslog.processing.common.draw.Drawable;

public class Node extends Drawable<ConnectionApplet>
{

	

	private Map<Node, Integer> connections = new HashMap<>();
	private int maxDistance = -1;
	private Node farthestNode = null;
	private int size;

	public Node(PVector position, ConnectionApplet parent, int size)
	{
		super(position, parent);
		this.size = size;
	}

	@Override
	public void drawMe(float sizeFactor)
	{
		parent.ellipse(position.x, position.y, size * sizeFactor, size * sizeFactor);

		// Let's draw the connections
		for (Node node : connections.keySet())
		{
			// Let's stroke with the fillColor
			parent.strokeWeight(sizeFactor);
			fillColor.stroke(parent);
			//Colors.RED.getRGBColor().stroke(parent);
			//parent.noFill();
			parent.line(position.x, position.y, node.position.x, node.position.y);
			parent.noStroke();
		}

	}

	public void connectWith(Node otherNode)
	{
		
		int distance = Math.round(position.dist(otherNode.position));
		
		// else
		if (parent.isConnectedByDistance)
			connectByDistance(otherNode, distance);
		else
			connectByMax(otherNode, distance);

	}
	
	private void connectByDistance(Node otherNode, int distance)
	{
		
		if (distance <= parent.factoredMaxConnectionDistance)
			connections.put(otherNode, distance);
	}
	
	
	private void connectByMax(Node otherNode, int distance)
	{
		// If the list is not filled yet, let's add nodes
		if (connections.size() < parent.maxConnections)
		{
			connections.put(otherNode, distance);

			if (distance > maxDistance)
			{
				maxDistance = distance;
				farthestNode = otherNode;
			}

		}
		else
		{
			// No need to continue, this node is too far
			if (distance > maxDistance)
				return;

			// If equal distance, let's arbitrary use the hashCode to chose one
			// viable connection
			if (distance == maxDistance && this.hashCode() < otherNode.hashCode())
				return;

			addConnection(otherNode, distance);
			// otherNode.addConnection(this, distance);
		}
	}

	public void reinitialise()
	{
		connections = new HashMap<>();
		maxDistance = -1;
		farthestNode = null;
	}

	private void addConnection(Node node, int distance)
	{
		// Let's remove the farthest node
		connections.remove(farthestNode);

		// Let's recalculate the farthest node
		int tempMaxDistance = distance;
		Node temFarthestNode = node;
		for (Entry<Node, Integer> nodeEntry : connections.entrySet())
		{
			if (nodeEntry.getValue() > tempMaxDistance)
			{
				tempMaxDistance = nodeEntry.getValue();
				temFarthestNode = nodeEntry.getKey();
			}
		}
		maxDistance = tempMaxDistance;
		farthestNode = temFarthestNode;

		// Let's add the new node
		connections.put(node, distance);
	}

}
