package com.pos.rssi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** 
 * 聚类算法主体类 
 * @author xusong 
 * @date 2016-1-29 
 */  
public class kmeans {
	/**
     * 做Kmeans运算
     * 
     * @param k
     *            int 聚类个数
     * @param data
     *            kmeans_data kmeans数据类
     * @param param
     *            kmeans_param kmeans参数类
     * @return kmeans_result kmeans运行信息类
     */
    public static Map<double[][],int[]> doKmeans(kmeansmodel km) {
        // 预处理
    	int k=km.k;
    	int dim =km.dim;//维数，一个坐标点对应dim个AP
    	double[][] data=km.data;
        double[][] centers = new double[k][dim]; // 聚类中心点集
        int[] centerCounts = new int[k]; // 各聚类的包含点个数
        int[] labels = new int[data.length]; // 各个点所属聚类标号
        double[][] oldCenters = new double[k][dim]; // 临时缓存旧的聚类中心坐标

        // 初始化聚类中心（随机选择data内的k个不重复点）
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

        // 第一轮迭代
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

        // 迭代预处理
        int maxAttempts = km.attempts > 0 ? km.attempts : km.MAX_ATTEMPTS;
        int attempts = 1;
        double criteria = km.criteria > 0 ? km.criteria : km.MIN_CRITERIA;
        boolean[] flags = new boolean[k]; // 标记哪些中心被修改过

        // 迭代
        iterate: while (attempts < maxAttempts) { // 迭代次数不超过最大值，最大中心改变量不超过阈值
        	// 初始化中心点“是否被修改过”标记
            for (int i = 0; i < k; i++) { 
                flags[i] = false;
            }
            // 遍历data内所有指纹点
            for (int i = 0; i < data.length; i++) { 
                double minDist = dist(km.data[i], centers[0], km.dim);
                int label = 0;
                for (int j = 1; j < k; j++) {
                    double tempDist = dist(km.data[i], centers[j], km.dim);
                    if (tempDist < minDist) {
                        minDist = tempDist;
                        label = j;//距离最小值属于哪个簇(k)
                    }
                }
                if (label != labels[i]) { // 如果当前点被聚类到新的类别则做更新
                    int oldLabel = labels[i];
                    labels[i] = label;//将第i个dim维的data指向新的簇label
                    centerCounts[oldLabel]--;//减少原来指向的簇oldLabel的拥有数
                    centerCounts[label]++;//增加新指向的簇label拥有数
                    flags[oldLabel] = true;
                    flags[label] = true;
                }
            }
            updateCenters(km);//更新中心点
            attempts++;//迭代次数增加

            // 计算每一个被修改过的中心点最大修改量是否超过阈值
            double maxDist = 0;
            for (int i = 0; i < k; i++) {
                if (flags[i]) {
                    double tempDist = dist(centers[i], oldCenters[i], km.dim);
                    if (maxDist < tempDist) {
                        maxDist = tempDist;
                    }
                    for (int j = 0; j < km.dim; j++) { // 更新oldCenter
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
     * 计算两点欧氏距离
     * 
     * @param pa
     *            double[]
     * @param pb
     *            double[]
     * @param dim
     *            int 维数
     * @return double 距离
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
     * 更新聚类中心坐标
     * 
     * @param k
     *            int 分类个数
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
     * double[][] 元素全置0
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
     * 拷贝源二维矩阵元素到目标二维矩阵。 foreach (dests[highDim][lowDim] = sources[highDim][lowDim]);
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
