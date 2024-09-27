package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Entity.Product;
import com.shopping_cart_project.shopping_cart_project.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProductService(ProductRepository productRepository, RedisTemplate<String, Object> redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public String deleteProduct(Long id){
        productRepository.deleteById(id);
        //刪除Redis快取
        String cacheKey = "product:" + id;
        redisTemplate.delete(cacheKey);
        return "Product deleted successfully";
    }

    public Product getProductById(Long id) throws Exception{
        //從Redis取得快取資料
        String cacheKey = "product:" + id;
        Product cachedProduct = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            return cachedProduct;
        }
        //沒有在Redis快取中，必須從資料庫取得
        Optional<Product> opt = productRepository.findById(id);
        if(opt.isPresent()){
            Product product = opt.get();
            //存入Redis快取，設定30秒後過期
            redisTemplate.opsForValue().set(cacheKey, product, 30, TimeUnit.SECONDS);
            return product;
        }
        throw new Exception("Product not found");
    }

    public Page<Product> getProductsByFilter(String category, Integer minPrice, Integer maxPrice,
                                             String sort, Integer pageNumber, Integer pageSize) {
        //取得第pageNumber（頁數是從0開始），每頁有pageSize個產品
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Product> products;

        //在快取中尋找
        String cacheKey = "products:filter:category:" + category +
                ":minPrice:" + minPrice +
                ":maxPrice:" + maxPrice +
                ":sort:" + sort +
                ":page:" + pageNumber +
                ":size:" + pageSize;
        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedProducts != null) {
            //直接將快取的資料放入products
            products = cachedProducts;
        }
        else{
            //沒在快取中
            //從資料庫取得符合條件的產品
            products = productRepository.findProductsByFilter(category, minPrice, maxPrice, sort);
            redisTemplate.opsForValue().set(cacheKey, products, 1, TimeUnit.MINUTES);
        }

        //設定從哪裡開始取資料，哪裡結束
        int startIndex = (int) pageable.getOffset();//取得指定頁數前有多少資料，等於pageNumber*pageSize
        //如果剩餘的資料>=pageSize，就只取pageSize筆。
        //如果剩餘的資料<pageSize，將剩下的資料全部取得。
        int endIndex = Math.min((startIndex + pageable.getPageSize()), products.size());

        //從過濾後的產品列表，截取對應頁數和數量的產品
        List<Product> pageContent = products.subList(startIndex, endIndex);

        //回傳內容、分頁資訊（頁碼、一頁有幾筆資料）、符合過濾條件的產品數量
        return new PageImpl<>(pageContent, pageable, products.size());
    }
}
