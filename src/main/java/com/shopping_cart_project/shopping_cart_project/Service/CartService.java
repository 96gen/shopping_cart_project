package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Entity.Cart;
import com.shopping_cart_project.shopping_cart_project.Entity.CartItem;
import com.shopping_cart_project.shopping_cart_project.Entity.Product;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Repository.CartRepository;
import com.shopping_cart_project.shopping_cart_project.Request.AddItemRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartItemService cartItemService;

    public CartService(CartRepository cartRepository, ProductService productService, CartItemService cartItemService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cartItemService = cartItemService;
    }

    //建立購物車
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    //將商品加入購物車
    public void addToCart(Long userId, AddItemRequest req) throws Exception{
        Cart cart = cartRepository.findCartByUserId(userId);
        Product product = productService.getProductById(req.getProductId());
        //檢查要新增的物品是否已經在購物車中
        CartItem isPresent = cartItemService.isCartItemInCart(cart, product);
        //不在購物車中，創建並添加一個新的購物車項目到購物車中
        if(isPresent == null) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(req.getQuantity());
            cartItem.setPrice(req.getQuantity() * product.getPrice());

            CartItem createdCartItem = cartItemService.createCartItem(cartItem);
            cart.getCartItems().add(createdCartItem);
            calcCartTotal(userId);
        }
    }

    //計算並更新購物車的價格和數量
    public Cart calcCartTotal(Long userId) {
        Cart cart = cartRepository.findCartByUserId(userId);
        Integer totalPrice = 0, totalQuantity = 0;

        for(CartItem cartItem : cart.getCartItems()) {
            totalPrice += cartItem.getPrice();
            totalQuantity += cartItem.getQuantity();
        }

        cart.setTotalPrice(totalPrice);
        cart.setTotalQuantity(totalQuantity);
        return cartRepository.save(cart);
    }

    public Integer clearCart(Long userId) throws Exception {
        Cart cart = cartRepository.findCartByUserId(userId);
        Integer totalPrice = cart.getTotalPrice();

        Iterator<CartItem> iterator = cart.getCartItems().iterator();
        while (iterator.hasNext()) {
            CartItem cartItem = iterator.next();
            cartItemService.removeCartItem(userId, cartItem.getId());
            iterator.remove();
        }

        cart.setTotalPrice(0);
        cart.setTotalQuantity(0);
        cartRepository.save(cart);

        return totalPrice;
    }
}
