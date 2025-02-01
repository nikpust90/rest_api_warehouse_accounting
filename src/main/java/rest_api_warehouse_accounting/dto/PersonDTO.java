package rest_api_warehouse_accounting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    @NotEmpty(message = "Поле не может быть пустым")
    @Size(min = 2, max = 20, message = "Поле должно быть от 2 до 20 символов")
    private String username;

    @NotEmpty(message = "Поле не может быть пустым")
    @Size(min = 2, max = 20, message = "Поле должно быть от 2 до 20 символов")
    private String password;

    private Integer yearOfBirth;

    @NotEmpty(message = "Поле не может быть пустым")
    @Email
    private String email;

    private String role;

}