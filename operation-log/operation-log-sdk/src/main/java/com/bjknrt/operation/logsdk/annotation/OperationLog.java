package com.bjknrt.operation.logsdk.annotation;

import com.bjknrt.operation.log.vo.LogAction;
import com.bjknrt.operation.log.vo.LogModule;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OperationLog {

    LogModule module();

    LogAction action();

    String context() default "";

    String currentOrgId() default "#currentOrgId";

    /**
     * 是否记录
     */
    String condition() default "true";
}
