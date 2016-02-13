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
	//加载properties信息
	synchronized static public void loads(){
		if(_dbNameSpace==null||_dbUrl==null)
		{
			InputStream fis = ConfigHelper.class.getResourceAsStream("/config.properties");
			Properties dbProps = new Properties();
			try {
				dbProps.load(fis);
				//获取对应参数值
				_dbNameSpace = dbProps.getProperty("NAMESPACE");
				_dbUrl = dbProps.getProperty("URL");
			}
			catch (Exception e) {
				System.err.println("不能读取属性文件. " +"请确保config.properties在指定的路径中");
			}
			finally  
	        {  
	            try  
	            { fis.close();}  
	            catch(IOException e) {e.printStackTrace();}  
	            fis = null;//垃圾回收  
	        }  
		}
	}
	
    //写入properties信息
	synchronized static public void writes(String parameterName,String parameterValue) {
    	try {
			InputStream fis = ConfigHelper.class.getResourceAsStream("/config.properties");
			Properties dbProps = new Properties();
			//从输入流中读取属性列表
			dbProps.load(fis);
			OutputStream fos = new FileOutputStream("/config.properties");
			//设置属性信息
			dbProps.setProperty(parameterName, parameterValue);
			//将Properties类对象的属性列表保存到输出流中
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
