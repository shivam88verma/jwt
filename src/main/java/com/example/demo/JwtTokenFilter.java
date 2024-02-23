package com.example.demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;

@Component // Filter only applies to URLs under /api/
public class JwtTokenFilter implements Filter {
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private static Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		logger.info("Filter DOne");

		String url = httpRequest.getRequestURI();
		// Skip the filter for specific URLs
		if (shouldSkipFilter(url)) {
			chain.doFilter(request, response); // Skip the filter chain
			return;
		}

		// Extract JWT token from the Authorization header
		String header = httpRequest.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.replace("Bearer ", "");
			logger.info("token :- {}",token);

			try {
				
				if(!jwtTokenUtil.valdateToken(token)) {
					logger.info("Filter Working");
				}
				// Continue with the filter chain
				chain.doFilter(request, response);
			} catch (ExpiredJwtException e) {
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} catch (Exception e) {
				httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		} else {
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private boolean shouldSkipFilter(String url) {
		// Add URLs for which the filter should be skipped
		List<String> urls = Arrays.asList("/signup", "/login", "/validateotp");
		return urls.contains(url); 
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Initialization code, if needed
	}

	@Override
	public void destroy() {
		// Cleanup code, if needed
	}
}
