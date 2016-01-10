package com.pos.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {
	static private String _dbNameSpace = null;
	static private String _dbUrl = null;
	static{
		loads();
	}
	synchronized static public void loads(){
		if(_dbNameSpace==null||_dbUrl==null)
		{
			InputStream fis = ConfigHelper.class.getResourceAsStream("/config.properties");
			Properties dbProps = new Properties();
			try {
				dbProps.load(fis);
				_dbNameSpace = dbProps.getProperty("NAMESPACE");
				_dbUrl = dbProps.getProperty("URL");
			}
			catch (Exception e) {
				System.err.println("不能读取属性文件. " +
				"请确保config.properties在CLASSPATH指定的路径中");
			}
			finally  
	        {  
	            try  
	            { fis.close();}  
	            catch(IOException e) {e.printStackTrace();}  
	            fis = null;//垃圾回收站上收拾  
	        }  
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
