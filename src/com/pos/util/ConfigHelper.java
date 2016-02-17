package com.pos.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigHelper {
	static private String _dbNameSpace = null;
	static private String _dbUrl = null;
	static private String _collectTime = null;
	static private String _clusterNum = null;
	static private String _kNum = null;
	static{
		loads();
	}
	//����properties��Ϣ
	synchronized static public void loads(){
		//InputStream fis = ConfigHelper.class.getResourceAsStream("/config.properties");
		FileInputStream fis=null;
		try {
			fis = new FileInputStream("/sdcard/config.properties");//�����ļ���
			Properties dbProps = new Properties();
			dbProps.load(fis);
			//��ȡ��Ӧ����ֵ
			_dbNameSpace = dbProps.getProperty("NAMESPACE");
			_dbUrl = dbProps.getProperty("URL");
			_collectTime = dbProps.getProperty("COLLECTTIME");//�ɼ�ʱ��
			_clusterNum= dbProps.getProperty("CLUSTERNUM");//������
			_kNum= dbProps.getProperty("KNUM");//kֵ
		}
		catch (Exception e) {
			System.err.println("���ܶ�ȡ�����ļ�. " +"��ȷ��config.properties��ָ����·����");
		}
		finally  
        {  
            try  
            { fis.close();}  
            catch(IOException e) {e.printStackTrace();}  
            fis = null;//��������  
        }  
	}
	
    //д��properties��Ϣ
	synchronized static public void writes(String parameterName,String parameterValue) {
    	try {
			//InputStream fis = ConfigHelper.class.getResourceAsStream("/config.properties");
			FileInputStream fis = new FileInputStream("/sdcard/config.properties");//�����ļ���
			Properties dbProps = new Properties();
			//���������ж�ȡ�����б�
			dbProps.load(fis);
			//OutputStream fos = new FileOutputStream("/config.properties");
			FileOutputStream fos = new FileOutputStream("/sdcard/config.properties");
			//����������Ϣ
			dbProps.setProperty(parameterName, parameterValue);
			//��Properties�����������б��浽�������
			dbProps.store(fos, "");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
	public static String getNameSpace() {
		if(_dbNameSpace==null)
			loads();
			return _dbNameSpace;
	}

	public static String getUrl() {
		if(_dbUrl==null)
			loads();
			return _dbUrl;
	}
	public static String getCollectTime() {
		if(_collectTime==null)
			loads();
			return _collectTime;
	}
	public static String getClusterNum() {
		if(_clusterNum==null)
			loads();
			return _clusterNum;
	}
	public static String getKNum() {
		if(_kNum==null)
			loads();
			return _kNum;
	}
}
