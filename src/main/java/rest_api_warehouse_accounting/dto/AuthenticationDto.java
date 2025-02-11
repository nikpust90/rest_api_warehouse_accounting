package rest_api_warehouse_accounting.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDto {

    @NotEmpty(message = "Поле не может быть пустым")
    @Size(min = 2, max = 20, message = "Поле должно быть от 2 до 20 символов")
    private String username;

    private String password;
}

