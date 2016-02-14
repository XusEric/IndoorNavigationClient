package com.pos.rssi;

public class kmeansmodel {
	public int k=2;//�ص���Ŀ�������ĵ�����
	public double[][] data;
	public int dim;//ά����һ��������Ӧdim��AP
	public int[] labels;//����������(0~k-1)
	public double[][] centers;//k���������ĵ������,����i(0~k-1)�����ĵ�Ϊcenters[i]
	public int[] centerCounts;//������İ���AP�����
	public static final int MAX_ATTEMPTS = 4000;
	public static final double MIN_CRITERIA = 1.0;
	
	public double criteria = MIN_CRITERIA; //��ֵ
	public int attempts = MAX_ATTEMPTS; //���Դ���
	
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
