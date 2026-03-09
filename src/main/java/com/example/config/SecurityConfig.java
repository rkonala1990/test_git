package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Spring Security configuration.
 *
 * PCI DSS Req 7/8: @EnableMethodSecurity activates @PreAuthorize and
 * @PostAuthorize enforcement on all Spring-managed beans. Without this,
 * method-level security annotations are silently ignored at runtime,
 * providing zero access control enforcement.
 *
 * securedEnabled = true  — enables @Secured annotations
 * prePostEnabled is true by default in @EnableMethodSecurity
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    // HTTP security rules (e.g. HttpSecurity) to be configured here
    // as the application grows. Method-level security is active via
    // @EnableMethodSecurity above.
}
