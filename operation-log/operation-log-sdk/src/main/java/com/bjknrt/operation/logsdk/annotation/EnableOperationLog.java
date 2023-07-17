package com.bjknrt.operation.logsdk.annotation;

import com.bjknrt.operation.logsdk.config.ServerNameConfig;
import com.bjknrt.operation.logsdk.rpc.OperationLogAspect;
import com.bjknrt.operation.logsdk.util.OperationLogUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value = {OperationLogAspect.class, OperationLogUtil.class, ServerNameConfig.class})
public @interface EnableOperationLog {
}
