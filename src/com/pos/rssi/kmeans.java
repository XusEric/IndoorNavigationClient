package com.pos.rssi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** 
 * �����㷨������ 
 * @author xusong 
 * @date 2016-1-29 
 */  
public class kmeans {
	/**
     * ��Kmeans����
     * 
     * @param k
     *            int �������
     * @param data
     *            kmeans_data kmeans������
     * @param param
     *            kmeans_param kmeans������
     * @return kmeans_result kmeans������Ϣ��
     */
    public static Map<double[][],int[]> doKmeans(kmeansmodel km) {
        // Ԥ����
    	int k=km.k;
    	int dim =km.dim;//ά����һ��������Ӧdim��AP
    	double[][] data=km.data;
        double[][] centers = new double[k][dim]; // �������ĵ㼯
        int[] centerCounts = new int[k]; // ������İ��������
        int[] labels = new int[data.length]; // ����������������
        double[][] oldCenters = new double[k][dim]; // ��ʱ����ɵľ�����������

        // ��ʼ���������ģ����ѡ��data�ڵ�k�����ظ��㣩
        Random rn = new Random();
        List<Integer> seeds = new LinkedList<Integer>();
        while (seeds.size() < k) {
            int randomInt = rn.nextInt(data.length);
            if (!seeds.contains(randomInt)) {
                seeds.add(randomInt);
            }
        }
        Collections.sort(seeds);
        for (int i = 0; i < k; i++) {
            int m = seeds.remove(0);
            for (int j = 0; j < dim; j++) {
                centers[i][j] = data[m][j];
            }
        }

        // ��һ�ֵ���
        for (int i = 0; i < data.length; i++) {
            double minDist = dist(data[i], centers[0], dim);
            int label = 0;
            for (int j = 1; j < k; j++) {
                double tempDist = dist(data[i], centers[j], dim);
                if (tempDist < minDist) {
                    minDist = tempDist;
                    label = j;
                }
            }
            labels[i] = label;
            centerCounts[label]++;
        }
        for(int i=0;i<k;i++){
        	System.out.print("i=" + i+",centers:");
        	for(int j=0;j<dim;j++){
            	System.out.print(centers[i][j]+" ");
        	}
        	System.out.println();
        }
        for(int i=0;i<data.length;i++){
        	System.out.println("i=" + i+",labels="+labels[i]);
        }
        km.centers=centers;
        km.centerCounts=centerCounts;
        km.labels=labels;
        updateCenters(km);
        copyCenters(oldCenters, centers, k, km.dim);

        // ����Ԥ����
        int maxAttempts = km.attempts > 0 ? km.attempts : km.MAX_ATTEMPTS;
        int attempts = 1;
        double criteria = km.criteria > 0 ? km.criteria : km.MIN_CRITERIA;
        boolean[] flags = new boolean[k]; // �����Щ���ı��޸Ĺ�

        // ����
        iterate: while (attempts < maxAttempts) { // �����������������ֵ��������ĸı�����������ֵ
        	// ��ʼ�����ĵ㡰�Ƿ��޸Ĺ������
            for (int i = 0; i < k; i++) { 
                flags[i] = false;
            }
            // ����data������ָ�Ƶ�
            for (int i = 0; i < data.length; i++) { 
                double minDist = dist(km.data[i], centers[0], km.dim);
                int label = 0;
                for (int j = 1; j < k; j++) {
                    double tempDist = dist(km.data[i], centers[j], km.dim);
                    if (tempDist < minDist) {
                        minDist = tempDist;
                        label = j;//������Сֵ�����ĸ���(k)
                    }
                }
                if (label != labels[i]) { // �����ǰ�㱻���ൽ�µ������������
                    int oldLabel = labels[i];
                    labels[i] = label;//����i��dimά��dataָ���µĴ�label
                    centerCounts[oldLabel]--;//����ԭ��ָ��Ĵ�oldLabel��ӵ����
                    centerCounts[label]++;//������ָ��Ĵ�labelӵ����
                    flags[oldLabel] = true;
                    flags[label] = true;
                }
            }
            updateCenters(km);//�������ĵ�
            attempts++;//������������

            // ����ÿһ�����޸Ĺ������ĵ�����޸����Ƿ񳬹���ֵ
            double maxDist = 0;
            for (int i = 0; i < k; i++) {
                if (flags[i]) {
                    double tempDist = dist(centers[i], oldCenters[i], km.dim);
                    if (maxDist < tempDist) {
                        maxDist = tempDist;
                    }
                    for (int j = 0; j < km.dim; j++) { // ����oldCenter
                        oldCenters[i][j] = centers[i][j];
                    }
                }
            }
            if (maxDist < criteria) {
                break iterate;
            }
        }
        
//        for(int i=0;i<k;i++){
//        	System.out.print("i=" + i+",centers:");
//        	for(int j=0;j<dim;j++){
//            	System.out.print(centers[i][j]+" ");
//        	}
//        	System.out.println();
//        }
        Map<double[][],int[]> result=new HashMap<double[][],int[]>();
        result.put(centers, labels);
        return result;
    }
    
    /**
     * ��������ŷ�Ͼ���
     * 
     * @param pa
     *            double[]
     * @param pb
     *            double[]
     * @param dim
     *            int ά��
     * @return double ����
     */
    public static double dist(double[] pa, double[] pb, int dim) {
        double rv = 0;
        for (int i = 0; i < dim; i++) {
            double temp = pa[i] - pb[i];
            temp = temp * temp;
            rv += temp;
        }
        return Math.sqrt(rv);
    }
    
    /**
     * ���¾�����������
     * 
     * @param k
     *            int �������
     * @param data
     *            kmeans_data
     */
    private static void updateCenters(kmeansmodel km) {
        double[][] centers = km.centers;
        setDouble2Zero(centers, km.k, km.dim);
        int[] labels = km.labels;
        int[] centerCounts = km.centerCounts;
        for (int i = 0; i < km.dim; i++) {
            for (int j = 0; j < km.data.length; j++) {
                centers[labels[j]][i] += km.data[j][i];
            }
        }
        for (int i = 0; i < km.k; i++) {
            for (int j = 0; j < km.dim; j++) {
                centers[i][j] = centers[i][j] / centerCounts[i];
            }
        }
    }
    
    /**
     * double[][] Ԫ��ȫ��0
     * 
     * @param matrix
     *            double[][]
     * @param highDim
     *            int
     * @param lowDim
     *            int <br/>
     *            double[highDim][lowDim]
     */
    private static void setDouble2Zero(double[][] matrix, int highDim, int lowDim) {
        for (int i = 0; i < highDim; i++) {
            for (int j = 0; j < lowDim; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    /**
     * ����Դ��ά����Ԫ�ص�Ŀ���ά���� foreach (dests[highDim][lowDim] = sources[highDim][lowDim]);
     * 
     * @param dests
     *            double[][]
     * @param sources
     *            double[][]
     * @param highDim
     *            int
     * @param lowDim
     *            int
     */
    private static void copyCenters(double[][] dests, double[][] sources, int highDim, int lowDim) {
        for (int i = 0; i < highDim; i++) {
            for (int j = 0; j < lowDim; j++) {
                dests[i][j] = sources[i][j];
            }
        }
    }
}
