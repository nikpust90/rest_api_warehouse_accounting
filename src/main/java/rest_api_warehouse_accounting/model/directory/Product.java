package rest_api_warehouse_accounting.model.directory;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product") // Задаем имя таблицы
@Data
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false) // Первичный ключ, нельзя обновлять или оставлять null
    private Long id;

    @Column(name = "name", nullable = false, length = 255) // Название товара, обязательное поле
    private String name;

    @Column(name = "sku", unique = true, length = 100) // Уникальный артикул товара (опционально)
    private String sku;

    @Column(name = "category", length = 255) // Категория товара
    private String category;

    @Column(name = "quantity", nullable = false) // Количество товара, обязательное поле
    private int quantity;
}
