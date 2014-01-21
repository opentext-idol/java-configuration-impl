package com.autonomy.frontend.configuration.filter;

import com.autonomy.frontend.configuration.ConfigService;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * $Id$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$
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

        if(configService.getConfig() == null && !request.getRequestURI().equals(request.getContextPath() + configPage)) {
            response.sendRedirect(request.getContextPath() + configPage);
        }
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    public void setConfigPage(final String configPage) {
        this.configPage = configPage;
    }

    public void setConfigService(final ConfigService<?> configService) {
        this.configService = configService;
    }
}
