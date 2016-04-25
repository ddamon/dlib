package com.dm.ioc.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入resource中的内容
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectResource {

	public int drawable() default 0;

	public int string() default 0;

	public int color() default 0;

	public int dimen() default 0;

}
