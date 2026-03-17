package ca.tetervak.citydata.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RequestLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        String requestUri = ((HttpServletRequest)servletRequest).getRequestURI();

        log.info("received request for {}", requestUri);
        filterChain.doFilter(servletRequest, servletResponse);
        log.info("response send for requested {}", requestUri);
    }
}
