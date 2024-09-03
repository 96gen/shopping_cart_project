package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Entity.Cart;
import com.shopping_cart_project.shopping_cart_project.Entity.CartItem;
import com.shopping_cart_project.shopping_cart_project.Entity.Product;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserService userService;

    public CartItemService(CartItemRepository cartItemRepository, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
    }

    //檢查商品是否在購物車中
    public CartItem isCartItemInCart(Cart cart, Product product) {
        return cartItemRepository.isCartItemInCart(cart, product);
    }

    //創建並儲存cartItem到資料庫中
    public CartItem createCartItem(CartItem cartItem) {
        cartItem.setQuantity(Math.max(cartItem.getQuantity(), 1));
        cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());

        return cartItemRepository.save(cartItem);
    }

    //更新CartItem，重新計算數量和價格，並儲存到資料庫。
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws Exception {
        CartItem item = findCartItemById(id);
        User user = userService.findUserById(item.getCart().getUser().getId());
        //確認發送請求的用戶和購物車的擁有者是同一人
        if(user.getId().equals(userId)) {
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(item.getQuantity() * item.getProduct().getPrice());
        }

        return cartItemRepository.save(item);
    }

    //用ID查詢CartItem
    public CartItem findCartItemById(Long id) throws Exception {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(id);
        if(optionalCartItem.isPresent()) {
            return optionalCartItem.get();
        }
        throw new Exception("CartItem not found with id : " + id);
    }

    //移除購物車的商品
    public void removeCartItem(Long userId, Long id) throws Exception {
        CartItem item = findCartItemById(id);
        User user = userService.findUserById(item.getCart().getUser().getId());
        User reqUser = userService.findUserById(userId);
        if(user.getId().equals(reqUser.getId())) {
            cartItemRepository.deleteById(id);
            return;
        }
        throw new Exception("Can't remove another users item");
    }
}
