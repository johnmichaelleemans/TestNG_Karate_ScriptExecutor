package app;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


import java.text.SimpleDateFormat;
import java.util.Date;

public class testStartTime {
	
	private static testStartTime instance = null;
	private static String time;
	
	private testStartTime(){
		
	}

	public static String getTime() {
		
		if(instance == null){
			instance = new testStartTime();
			time = new SimpleDateFormat("yyyy_MM_dd_'at'_HH_mm_ssa_z").format(new Date());
		}
		return time;
	}
	
	

}
