package rest_api_warehouse_accounting.controllers;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.model.referenceBooks.Product;
import rest_api_warehouse_accounting.service.ProductService;

// Аннотация @RestController указывает, что данный класс является REST-контроллером
// и все его методы будут возвращать JSON-ответы.
@RestController

// @RequestMapping указывает базовый URL для всех эндпоинтов этого контроллера.
// Все запросы, начинающиеся с "/api/inventory", будут обрабатываться в этом классе.
@RequestMapping("/api/directory")
public class ProductController {

    // Автоматически внедряем сервис IncomingService, который отвечает за бизнес-логику
    @Autowired
    private ProductService productService;

    /**
     * Получение списка всех Товаров.
     *
     * @return ResponseEntity со списком документов или сообщением об ошибке.
     */
    @GetMapping("/products") // Обработчик GET-запросов по пути "/api/referenceBooks/products"
    public ResponseEntity<?> getAllProducts() {
        try {
            // Запрашиваем список всех товаров из сервиса
            return ResponseEntity.ok(productService.getAllProducts());
        } catch (Exception e) {
            // В случае ошибки возвращаем статус 500 (внутренняя ошибка сервера)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении товаров");
        }
    }


    /**
     * Создание нового товара.
     *
     * @param product - объект Product, который приходит в теле запроса.
     * @return ResponseEntity с созданным товаром или сообщением об ошибке.
     */
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении товара");
        }
    }

    /**
     * Обновление существующего товара.
     *
     * @param id - ID товара, который нужно обновить.
     * @param productDetails - новые данные для товара.
     * @return Обновленный товар или сообщение об ошибке.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении товара");
        }
    }
}
