package com.telran.shop.controller;

import com.telran.shop.controller.dto.AddUnitResponseDto;
import com.telran.shop.controller.dto.CategoryDto;
import com.telran.shop.controller.dto.ProductDto;
import com.telran.shop.data.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    AdminRepository repository;

    @PostMapping("category")
    public AddUnitResponseDto addCategory(@RequestBody CategoryDto dto){
        String id = repository.addCategory(dto.getName());
        return AddUnitResponseDto.builder().id(id).build();
    }

    @PostMapping("product")
    public AddUnitResponseDto addProduct(@RequestBody ProductDto dto){
        String id = repository.addProduct(dto.getName(), dto.getPrice(),dto.getCategory().getId());
        return AddUnitResponseDto.builder().id(id).build();
    }
}
