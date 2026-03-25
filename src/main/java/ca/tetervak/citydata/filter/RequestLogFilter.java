package ca.tetervak.citydata.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class RequestLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        String requestUri = ((HttpServletRequest)servletRequest).getRequestURI();
        String method = ((HttpServletRequest)servletRequest).getMethod();

        log.info("received {} request for {}", method, requestUri);
        filterChain.doFilter(servletRequest, servletResponse);
        log.info("response send for requested {}", requestUri);
    }
}
