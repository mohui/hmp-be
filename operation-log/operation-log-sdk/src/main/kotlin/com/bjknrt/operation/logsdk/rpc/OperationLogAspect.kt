package com.bjknrt.operation.logsdk.rpc

import com.bjknrt.operation.logsdk.annotation.OperationLog
import com.bjknrt.operation.logsdk.util.OperationLogUtil
import com.bjknrt.security.client.AppSecurityUtil
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.LocalVariableTableParameterNameDiscoverer
import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.lang.reflect.Method
import java.math.BigInteger

@Aspect
class OperationLogAspect {

    private val parser = SpelExpressionParser()

    @Pointcut("@annotation(com.bjknrt.operation.logsdk.annotation.OperationLog)")
    fun pointcut() {
    }

    @Around("pointcut()")
    fun around(point: ProceedingJoinPoint): Any? {
        val annoInfo = annoInfo(point)
        val evaluationContext = evaluationContext(point)

        if (!getCondition(annoInfo.condition, evaluationContext)) return point.proceed()

        return OperationLogUtil.buzLog(
            annoInfo.module,
            annoInfo.action,
            getOrgId(annoInfo.currentOrgId, evaluationContext),
            getActionContext(annoInfo.context, evaluationContext)
        ) {
            point.proceed()
        }
    }

    private fun getCondition(condition: String, evaluationContext: EvaluationContext): Boolean {
        if (condition.isBlank()) return false
        return parser.parseExpression(condition).getValue(evaluationContext, Boolean::class.java) ?: false
    }

    private fun getActionContext(context: String, evaluationContext: EvaluationContext): String? {
        if (context.isBlank()) return null
        return parser.parseExpression(context).getValue(evaluationContext, String::class.java)
    }

    private fun getOrgId(currentOrgId: String, evaluationContext: EvaluationContext): BigInteger? {
        if (currentOrgId.isBlank()) return null
        return parser.parseExpression(currentOrgId).getValue(evaluationContext, BigInteger::class.java)
    }

    private fun evaluationContext(point: ProceedingJoinPoint): EvaluationContext {
        val methodInfo = methodInfo(point)
        // 创建 StandardEvaluationContext
        val standardEvaluationContext = StandardEvaluationContext()
        val parameterNameDiscoverer = LocalVariableTableParameterNameDiscoverer()
        val parametersName = parameterNameDiscoverer.getParameterNames(methodInfo)
        val args: Array<Any> = point.args

        standardEvaluationContext.setVariable("currentOrgId", AppSecurityUtil.jwtUser()?.currentOrgId)

        parametersName?.let {
            for (i in args.indices) {
                standardEvaluationContext.setVariable(parametersName[i], args[i])
            }
        }
        return standardEvaluationContext
    }

    private fun annoInfo(joinPoint: ProceedingJoinPoint): OperationLog {
        return methodInfo(joinPoint).getAnnotation(OperationLog::class.java)
    }

    private fun methodInfo(joinPoint: ProceedingJoinPoint): Method {
        val signature = joinPoint.signature as MethodSignature
        return signature.method
    }
}
