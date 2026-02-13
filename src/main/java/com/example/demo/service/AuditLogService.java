package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Async audit logging service — fire-and-forget event logging.
 *
 * KEY CONCEPTS:
 * - @Async("taskExecutor"): runs on the configured thread pool, not the request thread
 * - Caller doesn't block waiting for this to complete
 * - In production this would write to an audit table or event stream
 */
@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    @Async("taskExecutor")
    public void logTaskEvent(String action, Long taskId, String details) {
        log.info("AUDIT | action={} taskId={} details={}", action, taskId, details);
    }
}
