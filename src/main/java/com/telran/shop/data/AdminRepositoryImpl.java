package com.telran.shop.data;

import com.telran.shop.data.entity.CategoryEntity;
import com.telran.shop.data.entity.ProductEntity;
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
        return false;
    }

    @Override
    public boolean removeCategory(String categoryId) {
        return false;
    }

    @Override
    public boolean updateCategory(String categoryId, String categoryName) {
        return false;
    }

    @Override
    public boolean changeProductPrice(String productId, BigDecimal price) {
        return false;
    }

    @Override
    public boolean addBalance(String userEmail, BigDecimal balance) {
        return false;
    }
}
