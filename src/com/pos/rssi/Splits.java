package com.pos.rssi;

/** 
 * ʱ̬�ָ��㷨������ 
 * @author xusong 
 * @date 2016-1-29 
 */  
public class Splits {
	//�ָ�ʱ̬�㷨
    public static String startSplit(double[] rssi){

        try { 
        	//double[] rssi={57,60,55,58,87,57,56,62,74,72,59,55,58,89,76,87,77,54,58,68};
        	int range=10;//����ȡ���ݶη�Χ����
        	double[][] data=new double[range+1][1];
        	for(int i=0;i<rssi.length;i++){
        		if((i+range)<=rssi.length)
        		{
	        		//��ƽ��ֵ
	            	double average=0.00;
	        		double total=0.00;
	        		for (int j = i; j <i+range; j++) {
	        			total+=rssi[j];
	        		}
	        		average=total/range;
	        		//��ƽ�����
	        		double sqrsum = 0.0;
	        	    for (int j = i; j <i+range; j++) {
	        	    	sqrsum = sqrsum + (rssi[j]-average) * (rssi[j]-average);
	        	    }
	        	    //����
	        		double result=0.00;
	        		result = sqrsum / range;
//	        		System.out.print(" a"+i+":"+average+" "); 
//	        		System.out.print(" s"+i+":"+sqrsum+" "); 
//	        		System.out.print(" r"+i+":"+result+" | "); 
	        		data[i][0]=result;
        		}
        	}

            System.out.println(); 
        	double temp = 0;  
        	int index=0;
            for (int i = 0; i < data.length; i++)  
            {  
                if(i == 0)  
                {   
                   temp = data[i][0];  
                }  
                else  
                {  
                	if(temp > data[i][0]){
                		temp=data[i][0];
                		index=i;
                	}
                }  
            } 
            System.out.println(); 
    		System.out.print(" ��С�ӵ�"+index+"��ʼ����Сֵ��"+temp); 
        	System.out.println(); 
        	return String.valueOf(index);
        } catch (Exception e) { 
            e.printStackTrace(); 
            return "";
        }  
	}
}
