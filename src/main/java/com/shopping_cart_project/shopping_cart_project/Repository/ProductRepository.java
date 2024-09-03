package com.shopping_cart_project.shopping_cart_project.Repository;

import com.shopping_cart_project.shopping_cart_project.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    //選擇存放Product的資料表，把所有的商品資訊都選取出來，並且用p來代表每一筆商品資料
    @Query("SELECT p FROM Product p " +
            //OR前的部分：如果指定了類別，就只找這個類別的商品
            //OR後：沒有指定類別，那就把所有商品都找出來。
            "WHERE (p.category = :category OR :category LIKE '') " +
            //如果指定了最低價格，就只找價格比這個最低價格高的商品
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            //如果指定了最高價格，就只找價格比這個最高價格低的商品
            //如果使用者兩個都沒指定，那就把所有商品都找出來。
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            //依據指定的排序方式，對商品價格進行排序。
            "ORDER BY " +
            //如果sort是price_low，按照價格由低到高排序
            "CASE :sort WHEN 'price_low' THEN p.price END ASC, " +
            //sort是price_high，按照價格由高到低排序
            //price_low和price_high是由前端設定的
            "CASE :sort WHEN 'price_high' THEN p.price END DESC")
    public List<Product> findProductsByFilter(@Param("category") String category,
                                              @Param("minPrice") Integer minPrice,
                                              @Param("maxPrice") Integer maxPrice,
                                              @Param("sort") String sort);
}
