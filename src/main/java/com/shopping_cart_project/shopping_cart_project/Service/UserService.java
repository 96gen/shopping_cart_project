package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Config.JWTProvider;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTProvider jwtProvider, RedisTemplate<String, Object> redisTemplate, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }

    public void createUser(User user) throws Exception {
        //找尋資料庫中是否有使用同樣的email的用戶
        User isEmailExists = userRepository.findByEmail(user.getEmail());

        //如果有，代表這個email被註冊了
        if(isEmailExists != null) {
            throw new Exception("Error: Email is already registered.");
        }

        User createdUser = new User();
        createdUser.setEmail(user.getEmail());
        //將密碼加密，提升安全性
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        //寄出恭喜註冊的郵件到註冊Email
        sendEmail(user.getEmail());
        userRepository.save(createdUser);
    }

    //使用emailService完成發出Email的功能
    public void sendEmail(String to) {
        emailService.sendSimpleEmail(to);
    }

    public User findUserByEmail(String email){
        String cacheKey = "user:email:" + email;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        User user = userRepository.findByEmail(email);
        if (user != null) {
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        }

        return user;
    }

    public User findUserByJWT(String jwt) throws Exception{
        String email = jwtProvider.getEmailFromJWT(jwt);
        User user = findUserByEmail(email);
        if(user == null){
            throw new Exception("Error: Invalid JWT");
        }
        return user;
    }

    public User findUserById(Long id) throws Exception{
        String cacheKey = "user:id:" + id;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        Optional<User> opt = userRepository.findById(id);
        if(opt.isPresent()){
            User user = opt.get();
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
            return user;
        }
        throw new Exception("Error: User not found with id: " + id);
    }
}
