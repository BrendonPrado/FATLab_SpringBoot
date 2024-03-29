package com.fatlab.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JWTUtil {

    @Value( "${jwt.secret}" )
    private String secret;

 

    public String generateToken(String username){
        return Jwts.builder().setSubject( username )
                .signWith( SignatureAlgorithm.HS512,secret.getBytes() ).compact();
    }


    public boolean tokenValido(String token) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();
        if(username != null ) {
            return true;
        }
        return false;

    }



    private Claims getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
        }catch (Exception e) {
            return null;
        }
    }


    public String getUsername(String token) {
        Claims claims = getClaims(token);
        if(claims != null) {
            return claims.getSubject();
        }
        return null;
    }



}