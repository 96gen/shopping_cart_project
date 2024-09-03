package com.shopping_cart_project.shopping_cart_project.Repository;

import com.shopping_cart_project.shopping_cart_project.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //當Order entity的userId和傳入的userId相同時，才符合條件
    @Query("SELECT o FROM Order o WHERE o.userId = :userId")
    public List<Order> findOrderByUserId(@Param("userId") Long userId);
}
