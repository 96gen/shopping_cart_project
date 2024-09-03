package com.shopping_cart_project.shopping_cart_project.Service;

import com.shopping_cart_project.shopping_cart_project.Config.JWTProvider;
import com.shopping_cart_project.shopping_cart_project.Entity.User;
import com.shopping_cart_project.shopping_cart_project.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
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

        userRepository.save(createdUser);
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User findUserByJWT(String jwt) throws Exception{
        String email = jwtProvider.getEmailFromJWT(jwt);
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Error: Invalid JWT");
        }
        return user;
    }

    public User findUserById(Long id) throws Exception{
        Optional<User> opt = userRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        throw new Exception("Error: User not found with id: " + id);
    }
}
