package com.telran.shop.data;

import com.telran.shop.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> addUserInfo(String email, String name, String phone);
    Optional<UserEntity> getUserInfo(String email);

    List<ProductEntity> getAllProducts();
    List<CategoryEntity> getAllCategories();
    List<ProductEntity> getProductsByCategory(String categoryId);

    Optional<ShoppingCartEntity> addProductToCart(String userEmail, String productId,int count);
    Optional<ShoppingCartEntity> removeProductFromCart(String userEmail,String productId,int count);
    Optional<ShoppingCartEntity> getShoppingCart(String userEmail);
    boolean clearShoppingCart(String userEmail);

    List<OrderEntity> getOrders(String userEmail);

    Optional<OrderEntity> checkout(String userEmail);
}
