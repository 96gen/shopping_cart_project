package com.shopping_cart_project.shopping_cart_project.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private Integer amount;
    private String status;

    //設定這個欄位的上限是1024字元
    @Column(length = 1024)
    private String url;
    private Long userId;

    public Order(){

    }

    public Order(Long id, String sessionId, Integer amount, String status, String url, Long userId) {
        this.id = id;
        this.sessionId = sessionId;
        this.amount = amount;
        this.status = status;
        this.url = url;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}