/*
 * Copyright 2013-2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.frontend.configuration.filter;

import com.hp.autonomy.frontend.configuration.ConfigService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet filter that redirects users to an error page if no configuration is available in the provided config service
 */
public class ConfigEnvironmentVariableFilter implements Filter {

    private String configPage;

    private ConfigService<?> configService;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final Object config = configService.getConfig();
        final String contextPath = request.getContextPath();
        final String configUri = contextPath + configPage;
        final String requestUri = request.getRequestURI();

        if (config == null && !requestUri.equals(configUri)) {
            response.sendRedirect(configUri);
        } else if (config != null && requestUri.equals(configUri)) {
            response.sendRedirect(contextPath);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * @param configPage The URI of the error page
     */
    public void setConfigPage(final String configPage) {
        this.configPage = configPage;
    }

    /**
     * @param configService The {@link ConfigService} to read the configuration from
     */
    public void setConfigService(final ConfigService<?> configService) {
        this.configService = configService;
    }
}
