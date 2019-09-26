package app;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class FrameworkException extends Exception {
	
	private Exception ex;
	private String value;
	
	public FrameworkException(Exception exp, String message) {
		ex = exp;
		value = message;
		
	}
	
	public String getMessage(){
		return ex.getMessage();
	}
	
	public Throwable getCause(){
		if(value != null)
			return new Throwable(value);
		else if (ex.getCause()!=null){
			return ex.getCause();
		}
		else{
			return new Throwable("Framework Exception");
		}
	}
	
	public String getValue(){
		
		return value;
	}
	public StackTraceElement[] getStackTrace(){
		return ex.getStackTrace();
	}
	
	
	
}
