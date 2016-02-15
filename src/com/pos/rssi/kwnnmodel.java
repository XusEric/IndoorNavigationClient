package com.pos.rssi;
/** 
 * KWNN结点类，用来存储最近邻的k个元组相关的信息 
 * @author xusong 
 * @date 2016-1-29 
 */  
public class kwnnmodel {
	private int index; // 元组标号  
    private double distance; // 与测试元组的距离  
    private String c; // 所属类别  
    private double X;  
    private double Y;  
    public kwnnmodel(int index, double distance, String c,double x,double y) {  
        super();  
        this.index = index;  
        this.distance = distance;  
        this.c = c;  
        this.X = x;  
        this.Y = y;  
    }  
      
      
    public int getIndex() {  
        return index;  
    }  
    public void setIndex(int index) {  
        this.index = index;  
    }  
    public double getDistance() {  
        return distance;  
    }  
    public void setDistance(double distance) {  
        this.distance = distance;  
    }  
    public String getC() {  
        return c;  
    }  
    public void setC(String c) {  
        this.c = c;  
    }


	public double getX() {
		return X;
	}


	public void setX(double x) {
		X = x;
	}


	public double getY() {
		return Y;
	}


	public void setY(double y) {
		Y = y;
	}  
}
