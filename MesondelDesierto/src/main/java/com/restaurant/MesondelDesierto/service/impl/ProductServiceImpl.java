package com.restaurant.MesondelDesierto.service.impl;

import com.restaurant.MesondelDesierto.dto.ProductDto;
import com.restaurant.MesondelDesierto.dto.Response;
import com.restaurant.MesondelDesierto.entity.Category;
import com.restaurant.MesondelDesierto.entity.Product;
import com.restaurant.MesondelDesierto.exception.NotFoundException;
import com.restaurant.MesondelDesierto.mapper.EntityDtoMapper;
import com.restaurant.MesondelDesierto.repository.CategoryRepo;
import com.restaurant.MesondelDesierto.repository.ProductRepo;
import com.restaurant.MesondelDesierto.service.AwsS3Service;
import com.restaurant.MesondelDesierto.service.interf.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final AwsS3Service awsS3Service;



    @Override
    public Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Categoria no encontrada"));
        String productImageUrl = awsS3Service.saveImageToS3(image);

        Product product = new Product();
        product.setCategory(category);
        product.setPrice(price);
        product.setName(name);
        product.setDescription(description);
        product.setImageUrl(productImageUrl);

        productRepo.save(product);
        return Response.builder()
                .status(200)
                .message("Producto creado correctamente")
                .build();
    }

    @Override
    public Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Producto no encontrado"));

        Category category = null;
        String productImageUrl = null;

        if(categoryId != null ){
            category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Categoria no encontrada"));
        }
        if (image != null && !image.isEmpty()){
            productImageUrl = awsS3Service.saveImageToS3(image);
        }

        if (category != null) product.setCategory(category);
        if (name != null) product.setName(name);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);
        if (productImageUrl != null) product.setImageUrl(productImageUrl);

        productRepo.save(product);
        return Response.builder()
                .status(200)
                .message("Producto actualizado correctamente")
                .build();

    }

    @Override
    public Response deleteProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Producto no encontrado"));
        productRepo.delete(product);

        return Response.builder()
                .status(200)
                .message("Producto eliminado correctamente")
                .build();
    }

    @Override
    public Response getProductById(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Producto no encontrado"));
        ProductDto productDto = entityDtoMapper.mapProductToDtoBasic(product);

        return Response.builder()
                .status(200)
                .product(productDto)
                .build();
    }

    @Override
    public Response getAllProducts() {
        List<ProductDto> productList = productRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productList(productList)
                .build();

    }

    @Override
    public Response getProductsByCategory(Long categoryId) {
        List<Product> products = productRepo.findByCategoryId(categoryId);
        if(products.isEmpty()){
            throw new NotFoundException("Productos no encontrados en esta categoria");
        }
        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();

    }

    @Override
    public Response searchProduct(String searchValue) {
        List<Product> products = productRepo.findByNameContainingOrDescriptionContaining(searchValue, searchValue);

        if (products.isEmpty()){
            throw new NotFoundException("Productos no encontrados");
        }
        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());


        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();
    }
}
