package rest_api_warehouse_accounting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Исключаем null-значения
public class PersonUpdateDTO {

    @NotEmpty(message = "Имя пользователя не может быть пустым")
    @Size(min = 2, max = 20, message = "Имя пользователя должно быть от 2 до 20 символов")
    private String username; // Используется как идентификатор

    private Integer yearOfBirth;

    @Email(message = "Некорректный email")
    private String email;

    private String role;
}
