/**
 * 
 */
package com.dm.ioc;

import android.app.Application;
import android.content.Context;

/**
 * 
 */
public class Ioc {

	public static void initApplication(Application application) {
		IocContainer.getIocContainer().initApplication(application);
	}

	public static <T extends Application> T getApplication() {
		return IocContainer.getIocContainer().getApplication();
	}

	public static Context getApplicationContext() {
		return IocContainer.getIocContainer().getApplicationContext();
	}

	public static <T> T get(String name) {
		return IocContainer.getIocContainer().get(name);
	}

	public static <T> T get(Class<T> clazz) {
		return IocContainer.getIocContainer().get(clazz);
	}

	public static <T> T get(Class<T> clazz, String tag) {
		return IocContainer.getIocContainer().get(clazz, tag);
	}

	public static Instance bind(Class clazz) {
		return IocContainer.getIocContainer().bind(clazz);
	}

}
