package com.example.demo.controller;


import com.example.demo.model.Product;
import com.example.demo.model.ProductForm;
import com.example.demo.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/products")
public class ProductRestController {
    @Value("${file-upload}")
    String uploadPath;
    @Autowired
    private IProductService productService;

    //    @GetMapping
//    public ResponseEntity<Page<Product>> fillAllProduct(@RequestParam(name = "q") Optional<String> q, @PageableDefault(size = 8) Pageable pageable) {
//        Page<Product> productPage = null;
//        if (q.isPresent()) {
//            productPage = productService.findProductByNameContaining(q.get(), pageable);
//        } else {
//            productPage = productService.findAll(pageable);
//        }
//        if (productPage.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(productPage, HttpStatus.OK);
//    }
    @GetMapping
    public ResponseEntity<Iterable<Product>> findAll() {
        Iterable<Product> products = productService.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(productOptional.get(), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Product> saveProduct(@ModelAttribute ProductForm productForm) {
        String fileName = productForm.getImage().getOriginalFilename();
        Long currenTime = System.currentTimeMillis();
        fileName = currenTime + fileName;
        try {
            FileCopyUtils.copy(productForm.getImage().getBytes(), new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), productForm.getDescription(), fileName, productForm.getCategory());
        product.setCategory(productForm.getCategory());
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    private ResponseEntity<Product> editProduct(@PathVariable Long id, @ModelAttribute ProductForm productForm) {
        Optional<Product> productOptional = productService.findById(id);

        if (!productOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();
        MultipartFile multipartFile = productForm.getImage();
        if (multipartFile !=null && multipartFile.getSize() != 0) {
            File file = new File(uploadPath + product.getImage());
            if (file.exists()) {
                file.delete();
            }
            String fileName = productForm.getImage().getOriginalFilename();
            Long currenTime = System.currentTimeMillis();
            fileName = currenTime + fileName;
            product.setImage(fileName);
            try {
                FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());
        product.setDescription(productForm.getDescription());
        product.setCategory(productForm.getCategory());
        productService.save(product);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productService.removeById(id);
        return new ResponseEntity<>(productOptional.get(), HttpStatus.OK);
    }
}
