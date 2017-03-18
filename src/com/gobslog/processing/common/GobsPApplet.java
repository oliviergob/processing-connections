package com.gobslog.processing.common;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import com.gobslog.processing.common.draw.Colors;
import com.gobslog.processing.common.draw.RGBColor;


@SuppressWarnings("serial")
public abstract class GobsPApplet extends PApplet
{
	public static final String _APPLET_PARAM_PROPS = "propertiesFile";
	
	public static final String _PROP_BG_COLOR = "common.background.color";
	public static final String _PROP_BG_REFRESH = "common.background.refresh";
	public static final String _PROP_FOR_WEB_EXPORT = "common.for.web";
	public static final String _PROP_COMPUTE_SIZE_LIST = "common.size.list";
	public static final String _PROP_COMPUTE_SIZE = "common.compute.size";
	public static final String _PROP_COMPUTE_SIZE_DPI = "common.compute.size.dpi";
	public static final String _PROP_COMPUTE_DPI_LIST = "common.size.dpi.list";
	public static final String _PROP_DISPLAY_SIZE_X = "common.display.width";
	public static final String _PROP_DISPLAY_SIZE_Y = "common.display.height";
	public static final String _PROP_SCREENSHOT_DIR = "common.screenshot.dir";
	public static final String _PROP_SCREENSHOT_CONTACT = "common.screenshot.takedetails";
	public static final String _PROP_SCREENSHOT_FILENAME = "common.screenshot.filename";
	public static final String _PROP_SCREENSHOT_FILETYPE = "common.screenshot.filetype";
	public static final String _PROP_APP_NAME = "common.applicationName";
	public static final String _PROP_DISPLAY_DEFAULT_SIZE_X = "common.display.default.width";
	
	
	

	
	public String computeSize;
	public String currentComputeSize;
	public String computeDPI;
	public String currentComputeDPI;
	public String fullName;
	public String email;
	public String phoneNumber;
	public int sizeFactor = 10;
	public float computeToScreenRatio = 1;
	public float screenToDefaultRation = 1;

	private String defaultPropertiesFile = "default.properties";
	private Properties props = null;
	private RGBColor backgroundColor = null;
	private int displayWidth = 0;
	private int displayHeight = 0;
	private int bestDisplayWidth = 0;
	private int bestDisplayHeight = 0;
	private boolean isComputeScreenSize = true;
	private boolean takeScreenShot = false;
	
	
	protected ControlApplet controlApplet = new ControlApplet();
	protected DisplayApplet displayApplet;
	
	SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");

	public GobsPApplet()
	{
		super();
	}

	public final void setup()
	{
		try
		{
			this.setVisible(false);
			this.setBounds(0, 0, 0, 0);
			loadProperties();
			
			backgroundColor = getColorProperty(_PROP_BG_COLOR);
			
			int deviceIndex = 0;
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice devices[] = environment.getScreenDevices();
			// If there is a second screen plugged in always use that one
			if (devices.length == 2)
				deviceIndex = 1;
			GraphicsDevice displayDevice = devices[deviceIndex];
			
			Rectangle screenRect =  displayDevice.getDefaultConfiguration().getBounds();

			displayWidth = screenRect.width;
			displayHeight = screenRect.height;
			
			screenToDefaultRation = displayWidth / getIntProperty(_PROP_DISPLAY_DEFAULT_SIZE_X) ;
			
			computeSize = getProperty(_PROP_COMPUTE_SIZE);
			computeDPI = getProperty(_PROP_COMPUTE_SIZE_DPI);
			
			
			updateSize();

			
			
			//String[] stringArgs = {"--full-screen","--display=0", "Settings"};
			String[] stringArgs = {"Settings"};
	    	runSketch(stringArgs, controlApplet);
	    	
	    	displayApplet = DisplayApplet.startup(displayWidth, displayHeight, getProperty(_PROP_APP_NAME), deviceIndex);
	
			initialise();
			
	    	controlApplet.addSlider("sizeFactor","size",0,100,sizeFactor, this);
			controlApplet.addLine();
			controlApplet.addLine();
			controlApplet.addLine();
	    	controlApplet.addRadioButton("computeSize", "SIZE", this, getStringArrayProperty(_PROP_COMPUTE_SIZE_LIST));
	    	controlApplet.addLine();
/*	    	controlApplet.addRadioButton("computeDPI", "DPI", this, getStringArrayProperty(_PROP_COMPUTE_DPI_LIST));
	    	controlApplet.addLine();*/
	    	if (getBooleanProperty(_PROP_SCREENSHOT_CONTACT))
	    	{
	    		controlApplet.addTextField("fullName", "Full Name", this);
		    	controlApplet.addColumn();
		    	controlApplet.addColumn();
		    	controlApplet.addColumn();
		    	controlApplet.addColumn();
		    	controlApplet.addTextField("email", "email", this);
		    	controlApplet.addLine();
		    	controlApplet.addHalfLine();
		    	controlApplet.addTextField("phoneNumber", "Phone", this);
		    	controlApplet.addLine();
	    	}
	    	controlApplet.addHalfLine();
	    	controlApplet.addButton("screenShot", "ScreenShot", this);
	    	controlApplet.addLine();
			
			this.setVisible(false);
			//frame.setSize(0, 0);
			
			
		}
		catch (GobsProcessingException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void screenShot()
	{
		if ( getBooleanProperty(_PROP_SCREENSHOT_CONTACT) && ( email == null || email.length() == 0 || fullName == null || fullName.length() == 0))
		{
			System.err.println("Need to populate Full Name and Email to take a screenshot");
			return;
		}
		
		takeScreenShot = true;
		
		while (takeScreenShot)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	


	
	public final void draw()
	{
		controlApplet.setInitialised(true);
		// If the user changed DPI or compute size
		// Let's update the size
		if ((currentComputeSize != computeSize) || (currentComputeDPI != computeDPI) )
		{
			updateSize();
		}
		
		//frame.setSize(0, 0);
				
		if (getBooleanProperty(_PROP_BG_REFRESH) && backgroundColor!= null)
			{
				backgroundColor.fill(this);
				backgroundColor.setOpacity(getBackgroundOpacity());
				rect(0, 0, width, height);
			}
			
		smooth(8);
		
		/*if (sizeFactor <= 10)
			drawObjects(screenToDefaultRation, computeToScreenRatio * screenToDefaultRation);
		else
			drawObjects(((float)sizeFactor/10) * screenToDefaultRation , computeToScreenRatio * screenToDefaultRation);*/
		
		drawObjects(((float)sizeFactor/10) * screenToDefaultRation , computeToScreenRatio * screenToDefaultRation);
		
		loadPixels();
		PImage image = new PImage(width, height);
		image.loadPixels();
		image.pixels = pixels.clone();
		image.updatePixels();
		// Only resize the image if it is a different size than the display
		if (!isComputeScreenSize)
			image.resize(bestDisplayWidth, bestDisplayHeight);
		
		displayApplet.displayImage = image;
		
		if (takeScreenShot)
		{
			String screenshotName = getScreenShotFileName();
			System.out.println("Taking Screenshot:"+ screenshotName);
			saveFrame(screenshotName);
			takeScreenShot = false;
		}
		
	}
	
	private void updateSize()
	{
		int x = displayWidth;
		int y = displayHeight;
		currentComputeSize = computeSize;
		currentComputeDPI = computeDPI;
		
		if (!"screen".equals(computeSize))
		{
			isComputeScreenSize = false;
					
			x = getSizeX(computeSize, computeDPI);
			y = getSizeY(computeSize, computeDPI);
			size(x,y);
			
			// calculating the display ratio
			float computeToScreenRatioA = (float)width / displayWidth;
			float computeToScreenRatioB = (float)height / displayHeight;
			if (computeToScreenRatioB > computeToScreenRatioA)
			{
				bestDisplayHeight = displayHeight;
				bestDisplayWidth = Math.round(width / computeToScreenRatioB);
				computeToScreenRatio = computeToScreenRatioB;
			}
			else
			{
				bestDisplayHeight =	Math.round(height / computeToScreenRatioA);
				bestDisplayWidth = displayWidth;
				computeToScreenRatio = computeToScreenRatioA;
			}
		}
		else
		{
			size(x,y);
			computeToScreenRatio = 1;
			bestDisplayWidth = displayWidth;
			bestDisplayHeight = displayHeight;
		}
		
		
		if (backgroundColor!= null)
			background(backgroundColor.getRed(),backgroundColor.getGreen(), backgroundColor.getBlue());
		
		cleanScreen();
	}
	
	public abstract void drawObjects(float sizeFactor, float resolutionFactor);
	
	public abstract void initialise();
	
	public abstract void cleanScreen();
	
	public abstract int getBackgroundOpacity();
	
	public abstract InputStream getPropertiesFile();
	
	private void loadProperties() throws GobsProcessingException
	{
		props = new Properties();
		try
		{
			// Let's load the default properties
			InputStream input = this.getClass().getClassLoader().getResourceAsStream(defaultPropertiesFile);
			// load a properties file
			props.load(input);
			
			
			// Let's load the default properties
			if (getPropertiesFile() != null)
			{
				input = getPropertiesFile();
				// load a properties file
				props.load(input);
			}
			
		}
		catch (IOException e)
		{
			throw new GobsProcessingException(e);
		}
	}

	public String getProperty(String propertyName)
	{
		return props.getProperty(propertyName);
	}
	
	public boolean getBooleanProperty(String propertyName)
	{
		return "true".equals(getProperty(propertyName).trim());
	}
	
	public RGBColor getColorProperty(String propertyName)
	{
		// A color can be set to null
		if (null== getProperty(propertyName) || getProperty(propertyName).length() ==0  )
			return null;
		
		return getColor(getProperty(propertyName));
	}
	
	public int getIntProperty(String propertyName)
	{
		return Integer.valueOf(getProperty(propertyName));
	}
	
	public Float getFloatProperty(String propertyName)
	{
		return Float.valueOf(getProperty(propertyName));
	}
	
	public RGBColor getColor(String colorName)
	{
		return Colors.valueOf(colorName).getRGBColor();
	}
	
    public int getIntRandom(String minProp, String maxProp)
    {

    	return getIntRandom(getIntProperty(minProp), getIntProperty(maxProp));
    }
	
    public PVector getRandomPointOnScreen()
    {
    	return new PVector(getIntRandom(0, super.width), getIntRandom(0, super.height));
    }
	
	public boolean getBooleanRandom()
    {
    	return (random(100) > 50);
    }
	
	public boolean oneInNChance(int n)
    {
    	int random = (int) random(n);
    	int test = (int) (n/2);
    	return (random == test);
    }
	
    public int getIntRandom(int min, int max)
    {

    	int random = Math.round(getRandom(min,max));
    	return random;
    }
    
    public float getRandom(String minProp, String maxProp)
    {
    	return getRandom(getFloatProperty(minProp), getFloatProperty(maxProp));
    }
    
    public float getRandom(float min, float max)
    {
    	return min+random(max-min);
    }
    
    public int getSizeX(String sizeName, String sizeDPI)
    {
    	return getIntProperty("common.size."+sizeName+"."+sizeDPI+".x");
    }
    
    public int getSizeY(String sizeName, String sizeDPI)
    {
    	return getIntProperty("common.size."+sizeName+"."+sizeDPI+".y");
    }
    
    public String[] getStringArrayProperty(String propertyName)
    {
    	return getProperty(propertyName).split(",");
    }
    
    public void writeContacts(File contactSheet)
    {
    	
    	PrintWriter writer;
		try
		{
			contactSheet.createNewFile();
			writer = new PrintWriter(contactSheet);
			writer.println("Full Name: "+fullName);
	    	writer.println("Email: "+email);
	    	writer.println("Phone: "+phoneNumber);
	    	writer.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public String getScreenShotFileName()
    {
    	String cleanName = "connections";
    	if (getBooleanProperty(_PROP_SCREENSHOT_CONTACT))
    		cleanName = fullName.trim().toLowerCase().replace(' ', '.');
    	String directory = getProperty(_PROP_SCREENSHOT_DIR)+"//"+sdfMonth.format(new Date())+"//"+cleanName;
    	File currentPath = new File(directory);
    	int i = 0;
    	
    	if (getBooleanProperty(_PROP_SCREENSHOT_CONTACT))
    	{
    		File contactSheet = new File(directory+"//"+cleanName+".contact.txt");
        	if (!contactSheet.exists())
        		writeContacts(contactSheet);
    	}
    	
    	if (currentPath.isDirectory())
    	{
    		String[] files = currentPath.list();
    		for (String file : files)
    		{
    			if (file.startsWith(getProperty(_PROP_SCREENSHOT_FILENAME)+"."+cleanName))
    			{	
    				int j = Integer.valueOf((file.split("\\."))[2 + (cleanName.split("\\.")).length - 1]);
        			if (j>i)
        				i = j;
    			}
    		}
    		i++;
    		
    	}
    	
    	return directory+"//"+getProperty(_PROP_SCREENSHOT_FILENAME)+"."+cleanName+"."+i+"."
    			+computeSize+"."+computeDPI+getProperty(_PROP_SCREENSHOT_FILETYPE);
    	   	
    }
}
