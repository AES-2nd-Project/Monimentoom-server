package com.example.monimentoom.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration_time}")
    private Long expirationTime;

    private Key key;

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Long userId){
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰 검증 함수 */
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e){
            // TODO : 추후 로그 추가, 예외 세분화
            return false;
        }
    }

    /** 토큰 검증과 userId 추출 통합, 실패 시 null 반환 */
    public Long getUserIdFromToken(String token) {
        try {
            // 파싱시도 -> 에러 없이 넘어간 경우 검증 성공 상태.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 파싱된 데이터에서 유저 ID를 꺼내서 반환합니다.
            return Long.valueOf(claims.getSubject());

        } catch (Exception e) {
            // 만료된 토큰, 위조된 토큰 등 예외처리
            // TODO: 추후 ExpiredJwtException, SignatureException 등 예외 세분화 및 로그 추가
            return null; // 검증 실패 시 null 반환
        }
    }
}
