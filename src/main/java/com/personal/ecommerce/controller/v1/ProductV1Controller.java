package com.personal.ecommerce.controller.v1;

import com.personal.ecommerce.entity.PagedResponse;
import com.personal.ecommerce.entity.Product;
import com.personal.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProductV1Controller {

    @Autowired
    private ProductService _productService;

    @GetMapping("/products")
    public PagedResponse<Product> getProducts(
            @RequestParam(name="pageSize" ,defaultValue="10") int pageSize,
            @RequestParam(name="sortBy",defaultValue="productId") String sortBy,
            @RequestParam(name="pageNumber",defaultValue="1") int pageNumber,
            @RequestParam(name="sortDir",defaultValue="asc") String sortDir,
            @RequestParam(name="categoryId") Long categoryId
    ){

        if(pageSize < 0){
            pageSize = 10;
        }
        if(!(sortBy.equals("productId") || sortBy.equals("productName"))){
            sortBy = "productId";
        }
        if(pageNumber > 2000){
            pageNumber = 0;
        }
        if(!(sortDir.equals("asc") || sortDir.equals("desc"))){
            sortDir = "asc";
        }
        return _productService.getPaginatedProducts(pageSize, sortBy, pageNumber, sortDir, categoryId);
    }
}
