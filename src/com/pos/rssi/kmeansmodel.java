package com.pos.rssi;

public class kmeansmodel {
	public int k=2;//簇的数目，即中心点数量
	public double[][] data;
	public int dim;//维数，一个坐标点对应dim个AP
	public int[] labels;//所属聚类标号(0~k-1)
	public double[][] centers;//k个聚类中心点的坐标,，第i(0~k-1)个中心点为centers[i]
	public int[] centerCounts;//各聚类的包含AP点个数
	public static final int MAX_ATTEMPTS = 4000;
	public static final double MIN_CRITERIA = 1.0;
	
	public double criteria = MIN_CRITERIA; //阈值
	public int attempts = MAX_ATTEMPTS; //尝试次数
	
	public kmeansmodel(int k,double[][] data, int dim) {
		super();
		this.k=k;
		this.data = data;
		this.dim = dim;
	}
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}
}
