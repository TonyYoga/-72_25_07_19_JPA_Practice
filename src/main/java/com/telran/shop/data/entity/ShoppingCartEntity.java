package com.telran.shop.data.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "shopping_carts")
public class ShoppingCartEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    private Timestamp date;
    @OneToMany(mappedBy = "shoppingCart")
    private List<ProductOrderEntity> products;
    @OneToOne(mappedBy = "shoppingCart")
    private UserEntity owner;
}
