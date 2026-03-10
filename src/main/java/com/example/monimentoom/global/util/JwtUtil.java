package com.example.monimentoom.global.util;

import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
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

    /** 토큰 검증과 userId 추출 통합. 실패 시 CustomException */
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

        } catch (SignatureException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명 또는 구조입니다.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN); // 예외 던지기!
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.", e);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.", e);
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.", e);
            throw new CustomException(ErrorCode.EMPTY_TOKEN);
        }
    }
}
