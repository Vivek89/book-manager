package com.aqr.etf.book.filter;


import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class AuthFilter  implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        // Implement OAuth Check here
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
