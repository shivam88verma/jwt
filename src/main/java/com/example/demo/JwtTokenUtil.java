package com.example.demo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.example.demo.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	private String secret = "secKey";

	public String getUserNameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		try {
			final Date expiration = getClaimFromToken(token, Claims::getExpiration);
			return expiration.before(new Date());
		}catch(ExpiredJwtException expiredJwtException) {
			return true;
		}
	}

	private Boolean ignoreTokenExpiration(String token) {
		return false;
	}

	public String generateToken(User userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("sub", userDetails.getUsername());
		
		return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}


	public Boolean canTokenBeRefreshed(String token) {
		return (isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public Boolean valdateToken(String token) {
		try {
			return isTokenExpired(token);
		}catch(SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			if(ex instanceof SignatureException) {
				throw new SignatureException("Invalid JWT-Token signature!");
			}else if(ex instanceof MalformedJwtException) {
				throw new MalformedJwtException("Jwt-token Structure is invalid");
			}else if(ex instanceof ExpiredJwtException) {
				throw new ExpiredJwtException(null, null, "jwt-token has expired, Please try to login with new token");
			}else if(ex instanceof UnsupportedJwtException) {
				throw new UnsupportedJwtException("Unsuppored Jwt-token format");
			}else if(ex instanceof IllegalArgumentException) {
				throw new IllegalArgumentException("Invalid argument found when processing the Jwt-token");
			}
			
			throw ex;
		}
	}

}
