/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.report;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.report.generator.map.MarkerGraph;
import org.sigmah.shared.report.content.LatLng;
import org.sigmah.shared.report.content.Point;
import org.sigmah.shared.report.model.PointValue;
import org.sigmah.shared.report.model.clustering.Cluster;

/**
 * Represents a collection of nodes that
 * are clustered together in a given chromosone
 */
public class ClusterImpl implements Cluster {
    private List<PointValue> pointValues;
    private double radius;
    private Point point;
    private Rectangle rectangle;

    public ClusterImpl(Point point) {
        pointValues = new ArrayList<PointValue>();
        this.point = point;
    }
    
    public ClusterImpl(List<PointValue> points) {
    	pointValues=points;
    }

    public ClusterImpl(MarkerGraph.Node node) {
        pointValues = new ArrayList<PointValue>(1);
        pointValues.add(node.getPointValue());
        this.point = node.getPoint();
    }

    public ClusterImpl(PointValue pointValue) {
        pointValues = new ArrayList<PointValue>(1);
        pointValues.add(pointValue);
        point = pointValue.px;
    }

	public void addNode(MarkerGraph.Node node) {
        pointValues.add(node.getPointValue());
    }


    /**
     *
     * @return The weighted centroid of the cluster
     */
    @Override
	public Point bboxCenter() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for(PointValue pointValue : pointValues) {
            Point p = pointValue.px;
            if(p.getX() < minX) {
                minX = p.getX();
            }
            if(p.getY() < minY) {
                minY = p.getY();
            }
            if(p.getX() > maxX) {
                maxX = p.getX();
            }
            if(p.getY() > maxY) {
                maxY = p.getY();
            }
        }

        return new Point((minX + maxX)/2, (minY + maxY)/2);
    }

    @Override
	public LatLng latLngCentroid() {
        double latSum=0;
        double lngSum=0;
        double count=0;

        for(PointValue pointValue : pointValues) {
            latSum += pointValue.site.getLatitude();
            lngSum += pointValue.site.getLongitude();
            count++;
        }

        return new LatLng(latSum / count, lngSum / count);

    }

    @Override
	public Point weightedCentroid() {
            double weightSum = 0;
            double x = 0;
            double y = 0;
            for(PointValue pointValue : pointValues) {
                weightSum += pointValue.value;
                x += pointValue.px.getX() * pointValue.value;
                y += pointValue.px.getY() * pointValue.value;
            }

            return new Point((int)Math.round(x / weightSum), (int)Math.round(y / weightSum));
        }

    @Override
	public double getRadius() {
        return radius;
    }

    @Override
	public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
	public Point getPoint() {
        return point;
    }

    @Override
	public void setPoint(Point point) {
        this.point = point;
    }

    @Override
	public double sumValues() {
        double sum = 0;
        for(PointValue pointValue : pointValues) {
            sum += pointValue.value;
        }
        return sum;
    }

    @Override
	public List<PointValue> getPointValues() {
        return pointValues;
    }

    @Override
	public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
	public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }
//    private class Rectangle {
//    	private int x;
//    	private int y;
//    	private int width;
//    	private int height;
//
//    	public Rectangle(int x, int y, int width, int height) {
//    		this.x = x;
//    		this.y = y;
//    		this.width = width;
//    		this.height = height;
//    	}
//
//    	public int getX() {
//    		return x;
//    	}
//    	
//    	public void setX(int x) {
//    		this.x = x;
//    	}
//    	
//    	public int getY() {
//    		return y;
//    	}
//    	
//    	public void setY(int y) {
//    		this.y = y;
//    	}
//    	
//    	public int getWidth() {
//    		return width;
//    	}
//    	
//    	public void setWidth(int width) {
//    		this.width = width;
//    	}
//    	
//    	public int getHeight() {
//    		return height;
//    	}
//    	
//    	public void setHeight(int height) {
//    		this.height = height;
//    	}
//    	
//    	public boolean intersects(Rectangle rectangle) {
//    		int tw = this.width;
//    		     int th = this.height;
//    		     int rw = rectangle.getWidth();
//    		     int rh = rectangle.getHeight();
//    		     if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
//    		         return false;
//    		     }
//    		     int tx = this.x;
//    		     int ty = this.y;
//    		     int rx = rectangle.getX();
//    		     int ry = rectangle.getY();
//    		     rw += rx;
//    		     rh += ry;
//    		     tw += tx;
//    		     th += ty;
//    		     // overflow || intersect
//    		 return ((rw < rx || rw > tx) &&
//    		         (rh < ry || rh > ty) &&
//    		         (tw < tx || tw > rx) &&
//    		         (th < ty || th > ry));
//    	}
//    	
//    	public Rectangle intersection(Rectangle other) {
//    		int tx1 = this.x;
//    		     int ty1 = this.y;
//    		     int rx1 = other.x;
//    		     int ry1 = other.y;
//    		     long tx2 = tx1; tx2 += this.width;
//    		     long ty2 = ty1; ty2 += this.height;
//    		     long rx2 = rx1; rx2 += other.width;
//    		     long ry2 = ry1; ry2 += other.height;
//    		     if (tx1 < rx1) tx1 = rx1;
//    		     if (ty1 < ry1) ty1 = ry1;
//    		     if (tx2 > rx2) tx2 = rx2;
//    		     if (ty2 > ry2) ty2 = ry2;
//    		     tx2 -= tx1;
//    		     ty2 -= ty1;
//    		     // tx2,ty2 will never overflow (they will never be
//    		 // larger than the smallest of the two source w,h)
//    		 // they might underflow, though...
//    		 if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
//    		     if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
//    		     return new Rectangle  (tx1, ty1, (int) tx2, (int) ty2);
//    	}
//    }
}
