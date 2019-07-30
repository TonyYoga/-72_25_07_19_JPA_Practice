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
import java.util.Collections;
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
    @Transactional
    public Optional<ShoppingCartEntity> removeProductFromCart(String userEmail, String productId, int count) {
        Optional<ShoppingCartEntity> currChoppingCart = getShoppingCart(userEmail);
        if (currChoppingCart.isEmpty()) {
            return Optional.empty();
        }
        ProductOrderEntity currProduct = currChoppingCart.get().getProducts().stream()
                .filter(prod -> prod.getProductId().equals(productId)).findAny().orElseThrow();
        if (currProduct.getCount() <= count) {
            em.remove(currProduct);
        } else {
            currProduct.setCount(currProduct.getCount() - count);
        }
        em.merge(currChoppingCart.get());
        return currChoppingCart;
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
    @Transactional
    public boolean clearShoppingCart(String userEmail) {
        Optional<UserEntity> user = getUserInfo(userEmail);
        if (user.isEmpty()) {
            throw new RuntimeException(String.format("User %s not found", userEmail));
        }
        if (user.get().getShoppingCart() != null) {
            ShoppingCartEntity sce = new ShoppingCartEntity();
            em.persist(sce);
            user.get().setShoppingCart(sce);
            em.merge(user.get());
            return true;
        }
        return false;
    }

    @Override
    public List<OrderEntity> getOrders(String userEmail) {
        Optional<UserEntity> user = getUserInfo(userEmail);
        if (user.isEmpty()) {
            return Collections.emptyList();
        }
        return user.get().getOrders();
    }

    @Override
    @Transactional
    public Optional<OrderEntity> checkout(String userEmail) {
        Optional<UserEntity> user = getUserInfo(userEmail);
        if (user.isEmpty()) {
            throw new RuntimeException(String.format("User %s not found", userEmail));
        }
        ShoppingCartEntity currShoppingCart = user.get().getShoppingCart();
        if (currShoppingCart.getProducts().isEmpty()) {
            return Optional.empty();
        }
        BigDecimal totalPrice = currShoppingCart.getProducts().stream()
                .map(ProductOrderEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (user.get().getBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException(String.format("Insufficient funds at balance of user. id: %s", user.get().getEmail()));
        }
        user.get().setBalance(user.get().getBalance().subtract(totalPrice));
        OrderEntity checkoutOrder = new OrderEntity();
        em.persist(checkoutOrder);
        checkoutOrder.setDate(Timestamp.valueOf(LocalDateTime.now()));
        checkoutOrder.setOwner(currShoppingCart.getOwner());
        List<ProductOrderEntity> poe = currShoppingCart.getProducts();
        poe.forEach(product -> {
            product.setOrder(checkoutOrder);
            product.setShoppingCart(null);
        });
        checkoutOrder.setProducts(poe);
        checkoutOrder.setStatus(OrderStatus.DONE);
        user.get().getOrders().add(checkoutOrder);
        currShoppingCart.setProducts(new ArrayList<>());
        return Optional.of(checkoutOrder);
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
