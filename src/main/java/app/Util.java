package app;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


import java.io.File;

public class Util {
		private Util() {
			// To prevent external instantiation of this class
		}
		
		public static String getFileSeparator() {
			return System.getProperty("file.separator");
		}
		
		public static String getParentdir(String path){
			return new File(path).getParent();
		}

}
