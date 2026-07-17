package com.example.imagic.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String clientId = req.getHeader("clientId");
        String path = req.getServletPath();

        if(path.startsWith("/api") && StringUtils.isBlank(clientId)) {
            logger.error("Forbidden, no clientId");
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "No clientId");
        } else  {
            req.setAttribute("cliendId", clientId);
            chain.doFilter(request, response);
        }
    }
}
