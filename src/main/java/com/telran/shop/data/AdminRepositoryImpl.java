package com.telran.shop.data;

import com.telran.shop.data.entity.CategoryEntity;
import com.telran.shop.data.entity.ProductEntity;
import com.telran.shop.data.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public class AdminRepositoryImpl implements AdminRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public String addCategory(String categoryName) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(categoryName);
        em.persist(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public String addProduct(String productName, BigDecimal price, String categoryId) {
        CategoryEntity ce = em.find(CategoryEntity.class, categoryId);
        ProductEntity entity = new ProductEntity();
        entity.setName(productName);
        entity.setCategory(ce);
        entity.setPrice(price);
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public boolean removeProduct(String productId) {
        ProductEntity product = em.find(ProductEntity.class, productId);
        if (em != null) {
            em.remove(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeCategory(String categoryId) {
        CategoryEntity category = em.find(CategoryEntity.class, categoryId);
        if (category != null) {
            em.remove(category);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCategory(String categoryId, String categoryName) {
        CategoryEntity category = em.find(CategoryEntity.class, categoryId);
        if (category != null) {
            category.setName(categoryName);
            em.merge(category);
            return true;
        }
        return false;
    }

    @Override
    public boolean changeProductPrice(String productId, BigDecimal price) {
        ProductEntity product = em.find(ProductEntity.class, productId);
        if(product != null) {
            product.setPrice(price);
            em.merge(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean addBalance(String userEmail, BigDecimal balance) {
        UserEntity user = em.find(UserEntity.class, userEmail);
        if (user != null) {
            user.setBalance(balance);
            em.merge(user);
            return true;
        }
        return false;
    }

}
