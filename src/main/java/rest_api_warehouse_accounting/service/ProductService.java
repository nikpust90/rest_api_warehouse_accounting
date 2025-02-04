package rest_api_warehouse_accounting.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest_api_warehouse_accounting.model.directory.Product;
import rest_api_warehouse_accounting.model.document.IncomingDocument;
import rest_api_warehouse_accounting.model.document.item.IncomingItem;
import rest_api_warehouse_accounting.repositories.IncomingDocumentRepository;
import rest_api_warehouse_accounting.repositories.IncomingItemRepository;
import rest_api_warehouse_accounting.repositories.ProductRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден: " + id));

        product.setName(productDetails.getName());
        product.setQuantity(productDetails.getQuantity());
        product.setSku(productDetails.getSku());
        product.setCategory(productDetails.getCategory());

        return productRepository.save(product);
    }
}
