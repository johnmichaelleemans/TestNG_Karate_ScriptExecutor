package pomUtility;

//import app.deviceEnum;
/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class deviceView {
	//private device device;

	public enum deviceViewEnum{
		Desktop,
		Mobile,
		Tablet
	}
	
	public static  deviceViewEnum getDeviceViewEnum(Integer width){
		if(width <= 599 )
			return deviceViewEnum.Mobile;
		else if (width <= 991)
			return deviceViewEnum.Tablet;
		else
			return deviceViewEnum.Desktop;			
	}

}
