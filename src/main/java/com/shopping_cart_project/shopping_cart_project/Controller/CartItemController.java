package com.shopping_cart_project.shopping_cart_project.Controller;

import com.shopping_cart_project.shopping_cart_project.Entity.CartItem;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Service.CartItemService;
import com.shopping_cart_project.shopping_cart_project.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartItem")
public class CartItemController {
    private final CartItemService cartItemService;
    private final UserService userService;

    public CartItemController(CartItemService cartItemService, UserService userService) {
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    //修改商品數量
    @PutMapping("/{cartItemId}")
    public ResponseEntity<String> updateCartItem(@PathVariable("cartItemId") Long id,
                                                 @RequestBody CartItem cartItem,
                                                 @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJWT(jwt);
        cartItemService.updateCartItem(user.getId(), id, cartItem);

        return new ResponseEntity<>("Cart item updated successfully", HttpStatus.OK);
    }

    //刪除商品
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("cartItemId") Long id,
                                                 @RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserByJWT(jwt);
        cartItemService.removeCartItem(user.getId(), id);
        return new ResponseEntity<>("CartItem deleted successfully", HttpStatus.OK);
    }
}
