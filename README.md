# 小型電商購物車專案
一個對初學者友善的Spring Boot購物車專案，無需加入群組就能完成資料庫架構。

教學可[前往iT邦幫忙觀看](https://ithelp.ithome.com.tw/articles/10346761)

從Day15開始，到Day22結束。

---

## 功能簡介
- 註冊登入，註冊後會發送Email到電子信箱
- 新增、刪除、查詢商品
- 根據條件取得篩選後的商品列表
- 查看購物車內容
- 將商品放入、移除和修改數量
- 建立訂單並產生Stripe支付連結
- 查看用戶的訂單情形

---

## 專案路由與功能介紹
| 路徑 | HTTP request method | 說明 | Request body | Request header |
| --- | --- | --- | --- | --- |
| /auth/signup | POST | 註冊 | User | 無 |
| /auth/login | POST | 登入 | User | 無 |
| /api/user/ | GET | 取得目前登入的用戶資訊 | 無 | JWT |
| /api/product/ | POST | 建立商品 | Product | JWT |
| /api/product/{id} | DELETE | 刪除商品 | 無 | JWT |
| /api/product/{id} | GET | 取得商品 | 無 | JWT |
| /api/product/?minPrice=&maxPrice=&category=&sort=&pageNumber=&pageSize= | GET | 根據條件篩選並分頁商品 | 無 | JWT |
| /api/cart/ | GET | 取得購物車的內容 | 無 | JWT |
| /api/cart/add | PUT | 將商品加入購物車 | AddItemRequest | JWT |
| /api/cartItem/{cartItemId} | PUT | 修改購物車內的商品數量 | CartItem | JWT |
| /api/cartItem/{cartItemId} | DELETE | 刪除購物車內的商品 | 無 | JWT |
| /api/order/create_session | GET | 建立Stripe支付Session | 無 | JWT |
| /api/order/find_order | GET | 找尋該用戶的所有訂單 | 無 | JWT |

---

## 如何啟動
首先，在專案的根目錄編寫.env檔案，內容類似：

```
DATASOURCE_PASSWORD=********
JWT_CONSTANT=****************
STRIPE_PRIVATE_KEY=sk_test_****************
GMAIL_ADDRESS=*****@gmail.com
GMAIL_PASSWORD=****************
```

接著可以選擇：
1. 安裝MariaDB、Redis、RabbitMQ後，使用Maven編譯專案，執行jar。

```bash
mvn clean package -DskipTests
java -jar shopping_cart_project.jar
```

2. 或者直接使用Docker Compose，完成環境架構，並自動執行

```bash
docker-compose up
```

---

## Jmeter壓力測試

設定12000個Request，對於專案是個很大的挑戰。

![](https://images2.imgbox.com/cf/85/G7MpehmH_o.png)

測試的結果，看最大的延遲為2845ms，100%的通過率，沒有任何錯誤。

![](https://images2.imgbox.com/9b/72/5S371PAc_o.png)

最低的時候，可以達到5ms的低延遲表現。

![](https://images2.imgbox.com/58/77/TjtPYE9F_o.png)

我們的高併發專案通過了壓力測試，並且是在17秒內湧入12000個Request的情況下。