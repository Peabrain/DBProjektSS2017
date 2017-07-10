
//Original von:
//https://coderanch.com/t/338737/java/draw-points-Java

import java.awt.*;
import java.awt.Point;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
 
  
class PointPanel extends JPanel
{
    List<PointAndColor> pointList;
    Color selectedColor;
    Color colorNow;
    Ellipse2D selectedPoint;
    
  
    public PointPanel()
    {
        pointList = new ArrayList();
        setBackground(Color.white);
    }
  
    //Jetzt auch anders
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D e;
        Color color;
        PointAndColor pointandcolor;
        for(int j = 0; j < pointList.size(); j++)
        {
            pointandcolor = pointList.get(j);
            
            g2.setPaint(pointandcolor.color);
            g2.fill(pointandcolor.point);
        }
    }
  
    public List getPointList()
    {
        return pointList;
    }
  
    public void setSelectedPoint(Ellipse2D e)
    {
        selectedPoint = e;
        repaint();
    }
  
    /* Veralted
    public void addPoint(Point p)
    {
        Ellipse2D e = new Ellipse2D.Double(p.getX() *10, p.getY() *10, 6, 6);
        
        pointList.add(e);
        selectedPoint = null;
        repaint();
    }*/
    
    //Zusazt
    public void addPoint(Point p, Color c)
    {
        Ellipse2D e = new Ellipse2D.Double(p.getX(), p.getY(), 6, 6);
        
        pointList.add(new PointAndColor(e, c));
        selectedPoint = null;
        repaint();
    }
    private class PointAndColor{
    	Ellipse2D point;
    	Color color;
    	PointAndColor(Ellipse2D point, Color color){
    		this.color=color;
    		this.point=point;
    	}
    }
}
