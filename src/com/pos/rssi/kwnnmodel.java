package com.pos.rssi;
/** 
 * KWNN����࣬�����洢����ڵ�k��Ԫ����ص���Ϣ 
 * @author xusong 
 * @date 2016-1-29 
 */  
public class kwnnmodel {
	private int index; // Ԫ����  
    private double distance; // �����Ԫ��ľ���  
    private String c; // �������  
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
