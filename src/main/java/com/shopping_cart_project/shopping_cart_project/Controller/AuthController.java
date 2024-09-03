package com.shopping_cart_project.shopping_cart_project.Controller;

import com.shopping_cart_project.shopping_cart_project.Config.JWTProvider;
import com.shopping_cart_project.shopping_cart_project.Entity.Cart;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Response.AuthResponse;
import com.shopping_cart_project.shopping_cart_project.Service.CartService;
import com.shopping_cart_project.shopping_cart_project.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JWTProvider jwtProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    public AuthController(JWTProvider jwtProvider, UserService userService, PasswordEncoder passwordEncoder, CartService cartService) {
        this.jwtProvider = jwtProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        //處理用戶註冊
        userService.createUser(user);

        //使用JWTProvider產生token
        String token = jwtProvider.generateToken(user.getEmail());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signup Success");

        //建立用戶時，也建立購物車
        Cart cart = cartService.createCart(userService.findUserByEmail(user.getEmail()));

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody User user) throws Exception {
        //根據提供的email在資料庫中找尋用戶
        User foundUser = userService.findUserByEmail(user.getEmail());

        //如果找不到或密碼不對，就顯示錯誤
        if(foundUser == null || !passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            throw new Exception("Invalid email or password");
        }

        //產生token
        String token = jwtProvider.generateToken(foundUser.getEmail());
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login Success");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
