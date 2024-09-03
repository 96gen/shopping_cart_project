package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Entity.Order;
import com.shopping_cart_project.shopping_cart_project.Repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
        Dotenv dotenv = Dotenv.load();
        //設定Stripe的私鑰，沒有私鑰就無法產生支付連結。
        Stripe.apiKey = dotenv.get("STRIPE_PRIVATE_KEY");
    }

    //建立Stripe支付的Session
    //import Session時選擇com.stripe.model.checkout
    public Session createCheckoutSession(int amount) throws StripeException {
        //設定創建Session的參數
        SessionCreateParams params =
                SessionCreateParams.builder()
                        //設定為單次付款，不會每月重複收費
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        //付款成功後，會前往這個網址
                        .setSuccessUrl("http://localhost:5173/checkout/success")
                        //建立Stripe的購物車內容
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        //設定數量為1個
                                        .setQuantity(1L)
                                        //設定價格的資料
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        //設定貨幣為新台幣
                                                        .setCurrency("twd")
                                                        //設定商品價格，為什麼要乘100呢？
                                                        //原來是為了國際化，例如美國常常出現$9.99，用浮點數儲存可能有誤差，原因請自行搜尋【浮點數誤差】
                                                        //為了避免誤差，不如直接乘100，變成整數的999。
                                                        //想得知真正的價格時，再除100，就能取得真實的價格
                                                        .setUnitAmount(amount * 100L)
                                                        //設定商品的資料
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        //設定商品名稱
                                                                        .setName("96gen shopping cart product")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        //用前面設定的params，產生Session
        return Session.create(params);
    }

    //建立訂單
    public Order createOrder(String sessionId, Integer totalPrice, String status, String url, Long userId) throws Exception {
        Order order = new Order();
        order.setSessionId(sessionId);
        order.setAmount(totalPrice);
        order.setStatus(status);
        order.setUrl(url);
        order.setUserId(userId);
        return orderRepository.save(order);
    }

    //使用用戶ID查詢用戶的訂單資訊，查詢時同時更新資料
    public List<Order> findOrderByUserId(Long userId) throws Exception {
        List<Order> orders = orderRepository.findOrderByUserId(userId);
        List<Order> updated_orders = new ArrayList<>();
        for(Order order: orders){
            updateOrder(order.getId());
            updated_orders.add(order);
        }
        return updated_orders;
    }

    //更新訂單資訊的付款狀態
    public void updateOrder(Long id) throws Exception {
        Optional<Order> opt = orderRepository.findById(id);
        if(opt.isPresent()){
            Order updated = opt.get();
            Session session =
                    //根據SessionId從Stripe API取得Session資料
                    Session.retrieve(
                        opt.get().getSessionId()
                    );
            //更新付款狀態
            updated.setStatus(session.getPaymentStatus());
            orderRepository.save(updated);
            return;
        }
        throw new Exception("Error: Order not found with id: " + id);
    }

}
