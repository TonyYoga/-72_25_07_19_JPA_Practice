package com.telran.shop.controller;

import com.telran.shop.controller.dto.*;
import com.telran.shop.data.UserRepository;
import com.telran.shop.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class UserController {
    @Autowired
    UserRepository repository;

    @PostMapping("user")
    public UserDto addUserInfo(@RequestBody UserDto user) {
        return repository.addUserInfo(user.getEmail(), user.getName(), user.getPhone())
        .map(Mapper::map)
        .orElseThrow();
    }

    @GetMapping("user/{userEmail}")
    public UserDto getUserInfo(@PathVariable("userEmail") String userEmail) {
        return repository.getUserInfo(userEmail)
                .map(Mapper::map)
                .orElseThrow();
    }

    @GetMapping("products")
    public List<ProductDto> getAllProducts() {
        return repository.getAllProducts()
                .stream()
                .map(Mapper::map)
                .collect(toList());
    }

    @GetMapping("categories")
    public List<CategoryDto> getAllCategories() {
        return repository.getAllCategories()
                .stream()
                .map(Mapper::map)
                .collect(toList());
    }

    @GetMapping("products/{categoryName}")
    public List<ProductDto> getProductByCategory(@PathVariable("categoryName") String categoryName) {
        return repository.getProductsByCategory(categoryName)
                .stream()
                .map(Mapper::map)
                .collect(toList());
    }

    @PostMapping("cart/{userEmail}")
    public ShoppingCartDto addProductToCart(@PathVariable("userEmail") String userEmail,
                                            @RequestBody AddProductDto dto) {
        return repository.addProductToCart(userEmail, dto.getProductId(), dto.getCount())
                .map(Mapper::map)
                .orElseThrow();
    }

    @GetMapping("cart/{userEmail}")
    public ShoppingCartDto getShoppingCart(@PathVariable("userEmail") String userEmail) {
        return repository.getShoppingCart(userEmail)
                .map(Mapper::map)
                .orElseThrow();
    }

    @DeleteMapping("cart/{userEmail}/{productId}/{count}")
    public ShoppingCartDto removeProductFromCart(@PathVariable("userEmail") String userEmail,
                                                 @PathVariable("productId") String productId,
                                                 @PathVariable("count") int count) {
        return repository.removeProductFromCart(userEmail,productId,count)
                .map(Mapper::map)
                .orElseThrow();
    }

    @DeleteMapping("cart/{userEmail}/all")
    public void clearShoppingCart(@PathVariable("userEmail") String userEmail) {
        repository.clearShoppingCart(userEmail);
    }

    @GetMapping("orders/{userEmail}")
    public List<OrderDto> getAllOrdersByEmail(@PathVariable("userEmail")String userEmail){
        return repository.getOrders(userEmail)
                .stream()
                .map(Mapper::map)
                .collect(toList());
    }


    @GetMapping("checkout/{userEmail}")
    public OrderDto checkout(@PathVariable("userEmail") String userEmail) {
        return repository.checkout(userEmail)
                .map(Mapper::map)
                .orElseThrow();
    }
}
