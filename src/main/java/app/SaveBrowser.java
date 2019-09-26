package app;

public class SaveBrowser implements java.io.Serializable  {
	/*******************************TestNG Testing Framework Tool ************************************
	 *******************************      Author:    ***********************************
	 *******************************John-Michael Leemans********************************
	 */

	private static final long serialVersionUID = 1L;
	//Serialize This object
	String getSess;

	String url;

	public SaveBrowser(String getSessNew, String urlNew) {
		
		this.getSess = getSessNew;
	
		this.url = urlNew;
	}

}
