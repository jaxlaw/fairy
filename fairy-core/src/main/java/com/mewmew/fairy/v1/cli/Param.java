package com.mewmew.fairy.v1.cli;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Param
{
    String desc() default "";
    String name() default "";
    String option() default "";
    String defaultValue() default "";
    String propertyName() default "";
}
