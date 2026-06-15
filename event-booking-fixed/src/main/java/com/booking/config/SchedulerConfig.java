package com.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * SchedulerConfig
 *
 * Purpose:
 * This configuration class enables scheduling and asynchronous
 * processing features in the Spring Boot application.
 *
 * Why we use @EnableScheduling:
 * --------------------------------
 * It allows methods annotated with @Scheduled
 * to run automatically at fixed intervals or specific times.
 *
 * Example:
 * - Cleaning expired seat locks
 * - Booking timeout handling
 * - Sending periodic notifications
 * - Automatic payment status checks
 *
 * Why we use @EnableAsync:
 * -------------------------
 * It allows methods annotated with @Async
 * to run in separate background threads asynchronously.
 *
 * Example:
 * - Sending emails
 * - Sending OTPs
 * - Background notification processing
 * - Non-blocking operations
 *
 * Benefits:
 * ----------
 * - Improves application performance
 * - Enables background task execution
 * - Supports automated recurring jobs
 * - Prevents blocking the main request thread
 */

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {

}