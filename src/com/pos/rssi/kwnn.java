package com.pos.rssi;
import java.util.ArrayList;  
import java.util.Comparator;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import com.pos.entity.fingermodel;  
  
/** 
 * KWNN算法主体类 
 * @author xusong 
 * @date 2016-1-29 
 */  
public class kwnn {
	/** 
     * 设置优先级队列的比较函数，距离越大，优先级越高 o1是新数据，o2是pq中数据
     */  
    private Comparator<kwnnmodel> comparator = new Comparator<kwnnmodel>() {  
        public int compare(kwnnmodel o1, kwnnmodel o2) {  
            if (o1.getDistance() > o2.getDistance()) {  
                return -1;  
            } else if(o1.getDistance() < o2.getDistance()) {  
                return 1;  
            }  else {  
                return 0;  
            }  
        }  
    };  
    /** 
     * 获取K个不同的随机数 
     * @param k 随机数的个数 
     * @param max 随机数最大的范围 
     * @return 生成的随机数数组 
     */  
    public List<Integer> getRandKNum(int k, int max) {  
        List<Integer> rand = new ArrayList<Integer>(k);  
        for (int i = 0; i < k; i++) {  
            int temp = (int) (Math.random() * max);  
            if (!rand.contains(temp)) {  
                rand.add(temp);  
            } else {  
                i--;  
            }  
        }  
        return rand;  
    }  
    /** 
     * 计算测试元组与训练元组之前的距离 
     * @param d1 测试元组 
     * @param d2 训练元组 
     * @return 距离值 
     * 逻辑是先从d2训练元组中查找测试元组中的AP强度值计算，如果没有则默认为0
     * 这里d2不需要获取前k个AP的强度值，只需跟d1中所有的AP做欧氏距离计算即可，d1中的强度值可以在获取时做个限制（如较弱的信号不考虑）
     */  
    public double calDistance(fingermodel d1, fingermodel d2) {  
        double distance = 0.00; 
        double f1 = 0.00;  
        double f2 = 0.00;  
        for (Entry<String, Double> entry: d1.getRssi().entrySet()) { 
            f1 = entry.getValue();  //待测点采集到的Rssi 
            //f2 = d2.getRssi().get(entry.getKey());  
            if(d2.getRssi().get(entry.getKey())!=null)
            	f2=d2.getRssi().get(entry.getKey());//从该指纹点根据Mac取出对应的AP的Rssi
            else
            	f2=0.00;
            distance += (f1 - f2) * (f1 - f2); 
        }
        return Math.sqrt(distance);  
    }  
    /** 
     * 执行KNN算法，获取测试元组的类别 
     * @param datas 训练数据集 
     * @param testData 测试元组 
     * @param k 设定的K值 
     * @return 测试元组的类别 
     */  
    public String startkwnn(List<fingermodel> datas, fingermodel testData, int k) {  
        PriorityQueue<kwnnmodel> pq = new PriorityQueue<kwnnmodel>(k, comparator);  
        List<Integer> randNum = getRandKNum(k, datas.size());  
        for (int i = 0; i < k; i++) {  
            int index = randNum.get(i);  
            fingermodel currData = datas.get(index);  
            kwnnmodel node = new kwnnmodel(index, calDistance(testData, currData),
            		"",currData.getX(),currData.getY());  
            pq.add(node);  
        }  
//        System.out.println();
//        System.out.println("-----before-----");
//    	for (kwnnmodel x : pq) {
//    		 System.out.println(x.getIndex()+" "+x.getDistance()); 
//        } 
        for (int i = 0; i < datas.size(); i++) {  
            fingermodel t = datas.get(i);  
            double distance = calDistance(testData, t);  
            kwnnmodel top = pq.peek();  
            if (top.getDistance() > distance) {  
            	//此处做了一个修正，如果pq里面存在第i个元组，则不remove，继续下一个，保证pq里面元组是唯一的
            	int judge=0;
            	//集合方式遍历，元素不会被移除，检查是否存在相同的kwnnmodel
                for (kwnnmodel x : pq) {
                	if(x.getIndex()==i){
                		judge=1;
                		break;
                	}
                } 
            	if(judge==0){
	                pq.remove();  
	                pq.add(new kwnnmodel(i, distance, "",t.getX(),t.getY()));  
            	}
            }  
        }  
          
        return getWeight(pq);  
    }  
    /** 
     * 获取所得到的k个最近邻元组的加权和，并算出位置点
     * @param pq 存储k个最近近邻元组的优先级队列 
     * @return 坐标点
     */  
    private String getWeight(PriorityQueue<kwnnmodel> pq) {
    	double total=0.00;
//    	System.out.println();
//        System.out.println("-----after-----");
        //如果仅对值做加法，使用遍历value的方式，如下
//        for (String value : map.values()) { 
//        }
        //离待测点越近，权重越大，与距离成反比
    	for (kwnnmodel x : pq) {
    		total+=1/x.getDistance();
//    		System.out.print(x.getIndex()+" "+x.getDistance()+"--X:"+x.getX()+" Y:"+x.getY()); 
//    		System.out.println(); 
        }
//        System.out.println("-----分配后-----");
        double dx=0.00;
        double dy=0.00;
    	for (kwnnmodel x : pq) {
    		double w=1/(x.getDistance()*total);
    		x.setX(x.getX()*w);
    		x.setY(x.getY()*w);
    		dx+=x.getX();
    		dy+=x.getY();
//    		System.out.print(x.getIndex()+" "+x.getDistance()+"--X:"+x.getX()+" Y:"+x.getY()+" 权重："+w); 
//    		System.out.println(); 
        }
//    	System.out.println(" X:"+dx+" Y:"+dy);
    	return dx+","+dy;
    }
}
