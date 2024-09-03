package com.shopping_cart_project.shopping_cart_project.Controller;

import com.shopping_cart_project.shopping_cart_project.Entity.Cart;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Request.AddItemRequest;
import com.shopping_cart_project.shopping_cart_project.Service.CartService;
import com.shopping_cart_project.shopping_cart_project.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    //取得購物車的內容
    @GetMapping("/")
    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJWT(jwt);
        Cart cart = cartService.calcCartTotal(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    //將商品加入購物車
    @PutMapping("/add")
    public ResponseEntity<String> addItemToCart(@RequestBody AddItemRequest req, @RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserByJWT(jwt);
        cartService.addToCart(user.getId(), req);
        return new ResponseEntity<>("Item added to cart",HttpStatus.OK);
    }
}
