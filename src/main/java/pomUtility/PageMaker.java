package pomUtility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


import pomUtility.deviceView.deviceViewEnum;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */



public class PageMaker {
	
	
	
	public static <T> T getPageClassResponsive(Object object, String abstractClassName, String application){
		
	
		deviceViewEnum device = ((functions.AppTest)object).getDeviceView();
	
		//class instantiation via a string
		Class<?> cls = null;
		
		try{
		switch (device){
		case Desktop:
			try {
				cls = Class.forName(application + ".Desktop." + abstractClassName +"Desktop"  );
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
			break;
		case Mobile:
			try {
				cls = Class.forName(application + ".Mobile." + abstractClassName +"Mobile"  );
			} catch (ClassNotFoundException e) {
				
				try {
					cls = Class.forName(application + ".Desktop." + abstractClassName +"Desktop"  );
				} catch (ClassNotFoundException e1) {
					
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			break;
		case Tablet:
			try {
				cls = Class.forName(application + ".Tablet." + abstractClassName +"Tablet"  );
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
				try {
					cls = Class.forName(application + ".Desktop." + abstractClassName +"Desktop"  );
				} catch (ClassNotFoundException e1) {
					
					e1.printStackTrace();
				}
			}
			break;
		default:
			try {
				cls = Class.forName(application + ".Desktop." + abstractClassName +"Desktop"  );
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
			break;
			}
		}
		catch(Exception ex){
			try {
				cls = Class.forName(application + ".Desktop." + abstractClassName +"Desktop"  );
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
		}
		
		return (T) pageInstantiator(cls, object);
		
	} 
	
	public static <T> T getPageClass(Object object, String pageClass){
		
	
		//class instantiation via a string
		Class<?> cls = null;
			try {
				cls = Class.forName( pageClass );
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
		
		
		
		return (T) pageInstantiator(cls, object);
		
	} 
	
	private static <T> T pageInstantiator( Class<T> pageClassToProxy , Object object){
		try {
			try {
				//make singleton possibly
				Constructor<T> constructor = pageClassToProxy.getConstructor(java.lang.Object.class );
				
				return constructor.newInstance(object);
			} catch (NoSuchMethodException e) {
				return pageClassToProxy.newInstance();
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	private static <T> T pageInstantiator( Class<T> pageClassToProxy ){
		try {
			try {
				//make singleton possibly
				Constructor<T> constructor = pageClassToProxy.getConstructor();
				
				return constructor.newInstance();
			} catch (NoSuchMethodException e) {
				return pageClassToProxy.newInstance();
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
