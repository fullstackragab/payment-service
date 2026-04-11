package com.payments.payment_service.audit;

import com.payments.payment_service.domain.AuditEntry;
import com.payments.payment_service.dto.PaymentResponse;
import com.payments.payment_service.repository.AuditRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditAspect {
    private final AuditRepository auditRepository;

    public AuditAspect(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Around("@annotation(com.payments.payment_service.audit.Audited)")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Audited audited = method.getAnnotation(Audited.class);

        String methodName = method.getName();
        String action = audited.action();
        String entityType = audited.entityType();
        String beforeState = null;
        String afterState = null;
        String entityId = null;

        for (Object arg : joinPoint.getArgs()) {
            if(arg instanceof String s && s.matches(
                    "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{8}-[0-9a-f]{8}-[0-9a-f]{12}"
            )) {
                entityId = s;
                break;
            }
        }

        Object result = joinPoint.proceed();

        if(result instanceof PaymentResponse response) {
            entityId = response.paymentId();
            afterState = response.status();
        }

        if(entityId != null) {
            AuditEntry entry = new AuditEntry(
                    entityType, entityId, action,
                    beforeState, afterState, methodName
            );
            auditRepository.save(entry);
            System.out.println("[AUDIT] " + action + " on " + entityType + " " + entityId + " -> " + afterState);
        }

        return  result;
    }
}
