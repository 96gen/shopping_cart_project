package com.shopping_cart_project.shopping_cart_project.Config;

import io.github.cdimascio.dotenv.Dotenv;

public class JWTConstant {
    static Dotenv dotenv = Dotenv.load();
    public static final String SECRET = dotenv.get("JWT_CONSTANT");
}
