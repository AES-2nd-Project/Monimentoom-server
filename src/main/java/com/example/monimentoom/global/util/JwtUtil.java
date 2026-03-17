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
    @Value("${jwt.refresh_token_expiration_time}")
    private Long refreshExpirationTime;

    private Key key;

    private static final String TOKEN_TYPE   = "type";
    private static final String TYPE_ACCESS  = "access";
    private static final String TYPE_REFRESH = "refresh";
    private static final String TYPE_SIGNUP  = "signup";

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 서비스 엑세스 토큰 발급
    public String createToken(Long userId){
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .claim(TOKEN_TYPE, TYPE_ACCESS)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 발급
    public String createRefreshToken(Long userId){
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshExpirationTime);

        return Jwts.builder()
                .claim(TOKEN_TYPE, TYPE_REFRESH)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 카카오 신규 유저 2단계 회원가입용 임시 토큰 발급 (5분 만료) */
    public String createSignupToken(Long kakaoId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 5 * 60 * 1000L); // 5분

        return Jwts.builder()
                .setSubject(String.valueOf(kakaoId))
                .claim(TOKEN_TYPE, TYPE_SIGNUP)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 임시 토큰에서 kakaoId 추출 + type=signup 검증 */
    public Long getKakaoIdFromSignupToken(String token) {
        Claims claims = parseClaims(token);
        if (!TYPE_SIGNUP.equals(claims.get(TOKEN_TYPE, String.class))) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return Long.parseLong(claims.getSubject());
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        if (!TYPE_ACCESS.equals(claims.get(TOKEN_TYPE, String.class))) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return Long.parseLong(claims.getSubject());
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = parseClaims(token);
        if (!TYPE_REFRESH.equals(claims.get(TOKEN_TYPE, String.class))) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return Long.parseLong(claims.getSubject());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
        } catch (SignatureException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명 또는 구조", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰", e);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰", e);
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있거나 잘못됨", e);
            throw new CustomException(ErrorCode.EMPTY_TOKEN);
        }
    }

    public long getRefreshTokenExpiration() { return refreshExpirationTime; }

    // DB 저장용 — ms → 초 변환
    public long getRefreshTokenExpirySeconds() {
        return refreshExpirationTime / 1000;  // 604800000 / 1000 = 604800초
    }
}
