package com.pos.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigHelper {
	static private String _dbNameSpace = null;
	static private String _dbUrl = null;
	static{
		loads();
	}
	//����properties��Ϣ
	synchronized static public void loads(){
		if(_dbNameSpace==null||_dbUrl==null)
		{
			InputStream fis = ConfigHelper.class.getResourceAsStream("/config.properties");
			Properties dbProps = new Properties();
			try {
				dbProps.load(fis);
				//��ȡ��Ӧ����ֵ
				_dbNameSpace = dbProps.getProperty("NAMESPACE");
				_dbUrl = dbProps.getProperty("URL");
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
	}
	
    //д��properties��Ϣ
	synchronized static public void writes(String parameterName,String parameterValue) {
    	try {
			InputStream fis = ConfigHelper.class.getResourceAsStream("/config.properties");
			Properties dbProps = new Properties();
			//���������ж�ȡ�����б�
			dbProps.load(fis);
			OutputStream fos = new FileOutputStream("/config.properties");
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
}
