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
 * KWNN�㷨������ 
 * @author xusong 
 * @date 2016-1-29 
 */  
public class kwnn {
	/** 
     * �������ȼ����еıȽϺ���������Խ�����ȼ�Խ�� o1�������ݣ�o2��pq������
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
     * ��ȡK����ͬ������� 
     * @param k ������ĸ��� 
     * @param max ��������ķ�Χ 
     * @return ���ɵ���������� 
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
     * �������Ԫ����ѵ��Ԫ��֮ǰ�ľ��� 
     * @param d1 ����Ԫ�� 
     * @param d2 ѵ��Ԫ�� 
     * @return ����ֵ 
     * �߼����ȴ�d2ѵ��Ԫ���в��Ҳ���Ԫ���е�APǿ��ֵ���㣬���û����Ĭ��Ϊ0
     * ����d2����Ҫ��ȡǰk��AP��ǿ��ֵ��ֻ���d1�����е�AP��ŷ�Ͼ�����㼴�ɣ�d1�е�ǿ��ֵ�����ڻ�ȡʱ�������ƣ���������źŲ����ǣ�
     */  
    public double calDistance(fingermodel d1, fingermodel d2) {  
        double distance = 0.00; 
        double f1 = 0.00;  
        double f2 = 0.00;  
        for (Entry<String, Double> entry: d1.getRssi().entrySet()) { 
            f1 = entry.getValue();  //�����ɼ�����Rssi 
            //f2 = d2.getRssi().get(entry.getKey());  
            if(d2.getRssi().get(entry.getKey())!=null)
            	f2=d2.getRssi().get(entry.getKey());//�Ӹ�ָ�Ƶ����Macȡ����Ӧ��AP��Rssi
            else
            	f2=0.00;
            distance += (f1 - f2) * (f1 - f2); 
        }
        return Math.sqrt(distance);  
    }  
    /** 
     * ִ��KNN�㷨����ȡ����Ԫ������ 
     * @param datas ѵ�����ݼ� 
     * @param testData ����Ԫ�� 
     * @param k �趨��Kֵ 
     * @return ����Ԫ������ 
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
            	//�˴�����һ�����������pq������ڵ�i��Ԫ�飬��remove��������һ������֤pq����Ԫ����Ψһ��
            	int judge=0;
            	//���Ϸ�ʽ������Ԫ�ز��ᱻ�Ƴ�������Ƿ������ͬ��kwnnmodel
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
     * ��ȡ���õ���k�������Ԫ��ļ�Ȩ�ͣ������λ�õ�
     * @param pq �洢k���������Ԫ������ȼ����� 
     * @return �����
     */  
    private String getWeight(PriorityQueue<kwnnmodel> pq) {
    	double total=0.00;
//    	System.out.println();
//        System.out.println("-----after-----");
        //�������ֵ���ӷ���ʹ�ñ���value�ķ�ʽ������
//        for (String value : map.values()) { 
//        }
        //������Խ����Ȩ��Խ�������ɷ���
    	for (kwnnmodel x : pq) {
    		total+=1/x.getDistance();
//    		System.out.print(x.getIndex()+" "+x.getDistance()+"--X:"+x.getX()+" Y:"+x.getY()); 
//    		System.out.println(); 
        }
//        System.out.println("-----�����-----");
        double dx=0.00;
        double dy=0.00;
    	for (kwnnmodel x : pq) {
    		double w=1/(x.getDistance()*total);
    		x.setX(x.getX()*w);
    		x.setY(x.getY()*w);
    		dx+=x.getX();
    		dy+=x.getY();
//    		System.out.print(x.getIndex()+" "+x.getDistance()+"--X:"+x.getX()+" Y:"+x.getY()+" Ȩ�أ�"+w); 
//    		System.out.println(); 
        }
//    	System.out.println(" X:"+dx+" Y:"+dy);
    	return dx+","+dy;
    }
}
