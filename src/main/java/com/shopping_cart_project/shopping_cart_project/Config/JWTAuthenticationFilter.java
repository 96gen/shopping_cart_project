package com.shopping_cart_project.shopping_cart_project.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

//一個request只會執行一次，避免重複執行
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //取得Authorization中的內容
        String jwt = request.getHeader("Authorization");

        if(jwt != null){
            //去除開頭的Bearer
            jwt = jwt.substring("Bearer ".length() - 1);
            try{
                SecretKey key = Keys.hmacShaKeyFor(JWTConstant.SECRET != null ? JWTConstant.SECRET.getBytes() : null);
                //取得JWT中的payload區塊內容
                Claims claims = Jwts.parserBuilder()//建立JWT解讀器
                                    .setSigningKey(key)//設定檢驗的密鑰
                                    .build()//產生解讀器
                                    .parseClaimsJws(jwt)//解讀JWT
                                    .getBody();//取得payload內容
                //從JWT payload取得email
                String email = String.valueOf(claims.get("email"));
                //有效的JWT就等於本人輸入正確的email和密碼，可以直接通過驗證
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, null);
                //設定通過Spring Security的認證
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e){
                //JWT無效或過期，就顯示錯誤
                throw new BadCredentialsException("Invalid JWT or expired");
            }
        }
        //把request傳給下個filter
        filterChain.doFilter(request, response);
    }
}
