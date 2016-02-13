package com.pos.util;

import java.util.HashMap;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.pos.util.ConfigHelper;

/** 
 * 访问Web Service的工具类 
 * @author xusong 
 *  
 */  
public class WebServiceHelper {
	/** 
     * @param url          web service路径 
     * @param serviceName  web service服务名称 
     * @param nameSpace    web service名称空间 
     * @param methodName   web service方法名称 
     * @param params       web service方法参数 
     */  
    public static SoapObject getSoapObject(String serviceName,  
            String methodName, String soapAction, HashMap<String, Object> params) {  
        String URL = ConfigHelper.getUrl()+ serviceName + "?wsdl";  
        String NAMESPACE = ConfigHelper.getNameSpace();// 名称空间，服务器端生成的namespace属性值  
        String METHOD_NAME = methodName;  
        String SOAP_ACTION = soapAction;  
  
        SoapObject soap = null;  
        try {  
        	//实例化SoapObject对象
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);  
            if (params != null && params.size() > 0) {  
                for (Entry<String, Object> item : params.entrySet()) {  
                    rpc.addProperty(item.getKey(), item.getValue().toString());  
                }  
            }  
            //使用SOAP1.1协议创建Envelop对象
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  
            envelope.bodyOut = rpc;  
            // true--net; false--java;  
            envelope.dotNet = false;
            envelope.setOutputSoapObject(rpc);  
            //创建HttpTransportSE传输对象
            HttpTransportSE ht = new HttpTransportSE(URL);  
            ht.debug = true;  
            ht.call(SOAP_ACTION, envelope);  
            try {  
            	//如果服务器返回的是String类型
                soap = (SoapObject) envelope.getResponse();  
            } catch (Exception e) {  
            	//如果服务器返回的是byte[]类型
                soap = (SoapObject) envelope.bodyIn;  
            }  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        return soap;  
    }  
}
