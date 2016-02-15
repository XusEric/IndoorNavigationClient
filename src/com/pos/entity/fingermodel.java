package com.pos.entity;

import java.util.Map;

/** 
 * 指纹点结点类，表示一个指纹点及其采集的所有AP节点
 * @author xusong 
 * @date 2016-1-29 
 */  
public class fingermodel {
	private int index;
    private double X;  //lat
    private double Y;  //lng
    private Map<String, Double> rssi;
    public fingermodel(double x, double y,Map<String, Double> rssi) {  
        super();   
        this.setX(x);  
        this.setY(y);  
        this.setRssi(rssi); 
    }
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	public Map<String, Double> getRssi() {
		return rssi;
	}
	public void setRssi(Map<String, Double> rssi) {
		this.rssi = rssi;
	}  
}
