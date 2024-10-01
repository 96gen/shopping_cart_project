package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Entity.Cart;
import com.shopping_cart_project.shopping_cart_project.Entity.CartItem;
import com.shopping_cart_project.shopping_cart_project.Entity.Product;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Repository.CartItemRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final int CARTITEM_REDIS_CACHE_MINUTES = 1;
    Random random = new Random();

    public CartItemService(CartItemRepository cartItemRepository, UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
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

            //刪除快取
            String cacheKey = "cartItem:" + id;
            redisTemplate.delete(cacheKey);
        }

        return cartItemRepository.save(item);
    }

    //用ID查詢CartItem
    public CartItem findCartItemById(Long id) throws Exception {
        //尋找快取
        String cacheKey = "cartItem:" + id;
        CartItem cachedCartItem = (CartItem) redisTemplate.opsForValue().get(cacheKey);
        if (cachedCartItem != null) {
            return cachedCartItem;
        }
        //cache miss
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(id);
        if(optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            int random_delay = random.nextInt(3);
            redisTemplate.opsForValue().set(cacheKey, cartItem, CARTITEM_REDIS_CACHE_MINUTES + random_delay, TimeUnit.SECONDS);
            return cartItem;
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
            //刪除快取
            String cacheKey = "cartItem:" + id;
            redisTemplate.delete(cacheKey);
            return;
        }
        throw new Exception("Can't remove another users item");
    }
}
