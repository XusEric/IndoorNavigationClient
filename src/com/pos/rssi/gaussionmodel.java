package com.pos.rssi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** 
 * 高斯结点类，表示一个指纹点及其采集的所有AP节点
 * @author xusong 
 * @date 2016-1-29 
 */  
public class gaussionmodel {
	private double data[];

	public double[] getData() {
		return data;
	}

	public void setData(double data[]) {
		this.data = data;
	}
	
	//求平均值
	public double Average(){
		double result=0.00;
		double total=0.00;
		for(int i=0;i<data.length;i++){
			total+=data[i];
		}
		result=total/data.length;
		return result;
	}
	 /**
	  * 求给定双精度数组中值的平方差和
	  *
	  * @param inputData
	  *            输入数据数组
	  * @return 运算结果
	  */
	 public double SquareDifSum(double[] inputData) {
	 	if(inputData==null||inputData.length==0)
	    	return -1;
	    int len=inputData.length;
		double average = Average();
	    double sqrsum = 0.0;
	    for (int i = 0; i <len; i++) {
	    	sqrsum = sqrsum + (inputData[i]-average) * (inputData[i]-average);
	    }
	    return sqrsum;
	 }
	//求方差 如是总体（即估算总体方差）,如是抽样（即估算样本方差）,为我们大量接触的是样本，所以普遍使用根号内除以（n-1）
	public double Variance(){
		double result=0.00;
		double sqrsum = SquareDifSum(data);
		result = sqrsum / (data.length-1);
		return result;
		
	}
	//求高斯滤波后的值
//	3σ(西格玛)原则为
//	数值分布在（μ-σ,μ+σ)中的概率为0.6826
//	数值分布在（μ-2σ,μ+2σ)中的概率为0.9544
//	数值分布在（μ-3σ,μ+3σ)中的概率为0.9974
	public double GaussionFilter(){
		double result=0.00;
		double standardDeviation=Math.sqrt(Variance());//标准差
		double average=Average();//平均值

		List<Double> oneSigema = new ArrayList<Double>();
		List<Double> twoSigema = new ArrayList<Double>();
		List<Double> threeSigema = new ArrayList<Double>();
		double min = average-standardDeviation;//u-o
		double max = average+standardDeviation;//u+o
		for(int i=0;i<data.length;i++){
			if ((data[i]>=min)&&(data[i]<=max))
			{
				//68.26%
				oneSigema.add(data[i]);
				System.out.print(" oneSigema: "+data[i]);
			}
			else if(((data[i]>=(min-standardDeviation))&&(data[i]<min))
		||((data[i]<=(max+standardDeviation))&&data[i]>max))
			{
				//0.9544-68.26%=27.18%
				twoSigema.add(data[i]);
				System.out.print(" twoSigema: "+data[i]);
			}
			else if(((data[i]>=(min-2*standardDeviation))&&(data[i]<(min-standardDeviation)))
					||((data[i]<=(max+2*standardDeviation))&&data[i]>(max+standardDeviation)))
			{
				//0.9974-0.9544=4.3%
				threeSigema.add(data[i]);
			}
			System.out.println();
		}
		double totalOne=0.00;
		double totalTwo=0.00;
		double totalThree=0.00;
		for(Double tmp:oneSigema)  
        {  
			totalOne+= tmp;
        } 
		for(Double tmp:twoSigema)  
        {  
			totalTwo+= tmp;
        } 
		for(Double tmp:threeSigema)  
        {  
			totalThree+= tmp;
        } 
		double basic=0.00;
		int one=1,two=1,three=1;
		if(oneSigema.size()>0){
			basic+=0.6826;
			one=oneSigema.size();
		}
		if(twoSigema.size()>0){
			basic+=0.2718;
			two=twoSigema.size();
		}
		if(threeSigema.size()>0){
			basic+=0.043;
			three=threeSigema.size();
		}
		
//		System.out.println(" oneSigema AVG: "+totalOne/oneSigema.size()+
//				" twoSigema AVG: "+totalTwo/twoSigema.size()+
//				" threeSigema AVG: "+totalThree/threeSigema.size());
		result=(0.6826*(totalOne/one)+
				0.2718*(totalTwo/two)+
				0.043*(totalThree/three))/basic;
//		System.out.println();
		return result;
		
	}
}
