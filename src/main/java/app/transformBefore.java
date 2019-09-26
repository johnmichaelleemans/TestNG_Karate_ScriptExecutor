package app;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer2;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.ITestAnnotation;

public class transformBefore implements IAnnotationTransformer2{

	@Override
	public void transform(ITestAnnotation arg0, Class arg1, Constructor arg2, Method arg3) {
	
		
	}

	@Override
	public void transform(IDataProviderAnnotation arg0, Method arg1) {
		
		
	}

	@Override
	public void transform(IFactoryAnnotation arg0, Method arg1) {
		
		
	}

	@Override
	public void transform(IConfigurationAnnotation arg0, Class arg1, Constructor arg2, Method arg3) {
		
		
	}

}
