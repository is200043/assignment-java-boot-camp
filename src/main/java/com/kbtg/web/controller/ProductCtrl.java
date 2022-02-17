package com.kbtg.web.controller;

import com.kbtg.web.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductCtrl {

    private final ProductService productService;

    public ProductCtrl(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductByName(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(productService.getProductList(PageRequest.of(page, size)));
    }


    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductByName(@PathVariable String name,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(productService.searchProductListByName(name, PageRequest.of(page, size)));
    }

}
