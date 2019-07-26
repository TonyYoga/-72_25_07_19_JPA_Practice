package com.telran.shop.data;

import com.telran.shop.data.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @PersistenceContext
    EntityManager em;


    @Override
    @Transactional
    public Optional<UserEntity> addUserInfo(String email, String name, String phone) {
        UserEntity user = em.find(UserEntity.class, email);
        if (user == null) {
            user = new UserEntity(email, name, phone, BigDecimal.valueOf(0), null, null);
            em.persist(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UserEntity> getUserInfo(String email) {
        UserEntity user = em.find(UserEntity.class, email);
        if (user != null) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public List<ProductEntity> getAllProducts() {
        return em.createQuery("SELECT pe FROM ProductEntity pe", ProductEntity.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<CategoryEntity> getAllCategories() {
        return em.createQuery("SELECT ce FROM CategoryEntity ce", CategoryEntity.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<ProductEntity> getProductsByCategory(String categoryId) {
        CategoryEntity ce = em.find(CategoryEntity.class, categoryId);
        if (ce != null) {
            TypedQuery<ProductEntity> query = em.createQuery("SELECT pe FROM ProductEntity pe WHERE pe.category = :category", ProductEntity.class);
            query.setParameter("category", ce);
            return query.getResultList();
        }
        return List.of();
    }

    @Override
    @Transactional
    public Optional<ShoppingCartEntity> addProductToCart(String userEmail, String productId, int count) {
        //Find product and user info
        ProductEntity productEntity = em.find(ProductEntity.class, productId);
        UserEntity user = getUserInfo(userEmail).orElseThrow();

        if (productEntity == null) {
            throw new RuntimeException("Product not found");
        }

        //Getting user shopping cart if exist
        ShoppingCartEntity shoppingCart = getShoppingCart(userEmail).orElse(null);

        //If user does not have shopping cart create new shopping cart
        //and add new Shopping cart to PersistenceContext
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCartEntity();
            shoppingCart.setDate(Timestamp.valueOf(LocalDateTime.now()));
            shoppingCart.setProducts(new ArrayList<>());
            em.persist(shoppingCart);
        }

        // Add Shopping cart to current user
        user.setShoppingCart(shoppingCart);
        // Getting current products list
        List<ProductOrderEntity> products = shoppingCart.getProducts();

        // Getting ProductOrderEntity
        // (if user added product before we need add count to current product)
        ProductOrderEntity poe = products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findAny()
                .orElse(null);

        // If Product was not before
        // create new ProductOrderEntity and add to Shopping cart
        // else add count to current product
        if (poe == null) {
            poe = new ProductOrderEntity();
            poe.setProductId(productEntity.getId());
            poe.setCategory(productEntity.getCategory());
            poe.setName(productEntity.getName());
            poe.setPrice(productEntity.getPrice());
            poe.setCount(count);
            em.persist(poe);
            poe.setShoppingCart(shoppingCart);
            products.add(poe);
        } else {
            poe.setCount(poe.getCount() + count);
        }

        return Optional.of(shoppingCart);
    }

    @Override
    public Optional<ShoppingCartEntity> removeProductFromCart(String userEmail, String productId, int count) {
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<ShoppingCartEntity> getShoppingCart(String userEmail) {
        TypedQuery<ShoppingCartEntity> query = em.createQuery("SELECT sce FROM ShoppingCartEntity sce WHERE sce.owner.email = :email", ShoppingCartEntity.class);
        query.setParameter("email", userEmail);
        ShoppingCartEntity shoppingCart = getSingleResult(query);
        if (shoppingCart != null) {
            return Optional.of(shoppingCart);
        }
        return Optional.empty();
    }

    @Override
    public boolean clearShoppingCart(String userEmail) {
        return false;
    }

    @Override
    public List<OrderEntity> getOrders(String userEmail) {
        return null;
    }

    @Override
    public Optional<OrderEntity> checkout(String userEmail) {
        return Optional.empty();
    }


    private static <T> T getSingleResult(TypedQuery<T> query) {
        query.setMaxResults(1);
        List<T> list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }
}
