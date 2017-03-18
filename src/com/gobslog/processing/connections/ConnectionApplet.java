package com.gobslog.processing.connections;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.gobslog.processing.common.GobsPApplet;
import com.gobslog.processing.common.motion.BasicMotor;
import com.gobslog.processing.common.motion.Motor;

@SuppressWarnings("serial")
public class ConnectionApplet extends GobsPApplet
{
	
	public static final String _PROP_NODE_MIN = "connections.node.min";
	public static final String _PROP_NODE_MAX = "connections.node.max";
	public static final String _PROP_NODE_SIZE = "connections.node.size";
	public static final String _PROP_NODE_COLOR_FILL = "connections.node.color.fill";
	public static final String _PROP_NODE_COLOR_STROKE = "connections.node.color.stroke";
	
	public static final String _PROP_NODE_MAX_CONNETION = "connections.node.max.connection";
	public static final String _PROP_NODE_CONNETION_BY_DISTANCE = "connections.node.connectionbydistance";
	public static final String _PROP_NODE_CONNETION_MAX_DISTANCE = "connections.node.distancemax";
	
	public boolean isConnectedByDistance;
	public int maxConnections;
	public int maxConnectionDistance;
	public int factoredMaxConnectionDistance;
	public int numberOfNodes = 0;
	public int speedFactor = 50;
	public int backgroundOpacity = 50;
	
	private String propertiesFile = "connections.01.properties";	
	private List<Node> nodes;
	private Motor<ConnectionApplet> motor ;

	
	@Override
	public void initialise()
	{
		numberOfNodes = getIntRandom(_PROP_NODE_MIN, _PROP_NODE_MAX);
		nodes = new ArrayList<>();
		
		for (int i = 0; i< numberOfNodes;  i++ )
		{
			addNode();
		}
		
		motor = new BasicMotor<ConnectionApplet>(this);
		
		maxConnections = getIntProperty(_PROP_NODE_MAX_CONNETION);
		maxConnectionDistance = getIntProperty(_PROP_NODE_CONNETION_MAX_DISTANCE);
		isConnectedByDistance = getBooleanProperty(_PROP_NODE_CONNETION_BY_DISTANCE);
		
		controlApplet.addSlider("numberOfNodes","Nodes",0,170,numberOfNodes, this);
		controlApplet.addLine();
		controlApplet.addSlider("backgroundOpacity","Opacity",0,100,backgroundOpacity, this);
		controlApplet.addLine();
		controlApplet.addSlider("speedFactor","Speed",0,100,speedFactor, this);
		controlApplet.addLine();
		controlApplet.addSlider("maxConnectionDistance","Distance",50,120,maxConnectionDistance, this);
		controlApplet.addLine();

	}
	
	private void addNode()
	{
		Node node = new Node(getRandomPointOnScreen(), this, getIntProperty(_PROP_NODE_SIZE));
		node.setFillColor(getColorProperty(_PROP_NODE_COLOR_FILL));
		node.setStrokeColor(getColorProperty(_PROP_NODE_COLOR_STROKE));
		nodes.add(node);
	}
	
	@Override
	public void drawObjects( float sizeFactor, float computeToScreenRatio)
	{
		factoredMaxConnectionDistance = (int) (maxConnectionDistance * computeToScreenRatio);

		//int factoredNumberOfNodes = (int) (numberOfNodes * computeToScreenRatio);
		int factoredNumberOfNodes =  numberOfNodes;
		
		for (Node node : nodes)
		{
			node.reinitialise();
			motor.moveObject(node, (float)speedFactor/30);
		}
		
		
		if (nodes.size() > factoredNumberOfNodes)
		{
			for (int j = factoredNumberOfNodes ; j < nodes.size(); j++  )
			{
				nodes.remove(j);
			}
		}
		else if (nodes.size() < factoredNumberOfNodes)
		{
			for (int j = nodes.size() - 1; j < factoredNumberOfNodes; j++  )
			{
				addNode();
			}
		}
		
		for (int i = 0; i < nodes.size();  i++ )
		{
			Node currentNode = nodes.get(i);
			
			// Lets compare all the nodes with one another
			for (int j = i +1; j < nodes.size();  j++ )
			{
				currentNode.connectWith(nodes.get(j));
				// If connections are made using a maximum number
				// We need to compare nodes both way as node a could have reached its max connection
				// Whereas node b not
				if (!isConnectedByDistance)
					nodes.get(j).connectWith(currentNode);
			}
			
			currentNode.draw(sizeFactor * computeToScreenRatio);
		}
		
		for (Node node : nodes)
		{
			node.draw(sizeFactor * computeToScreenRatio);
		}
	}

	@Override
	public int getBackgroundOpacity() {
		return backgroundOpacity;
	}
	
	public static void main(String args[])
	{
		ConnectionApplet connectionApplet = new ConnectionApplet();
		String[] stringArgs = {"Compute Applet"};
		if (args.length > 0)
			connectionApplet.propertiesFile = args[0];
		runSketch(stringArgs, connectionApplet);
	}

	@Override
	public InputStream getPropertiesFile()
	{
		if (propertiesFile == null)
			return null;
		
		return this.getClass().getClassLoader().getResourceAsStream(propertiesFile);
	}

	@Override
	public void cleanScreen()
	{
		nodes = new ArrayList<>();
	}
		

}
