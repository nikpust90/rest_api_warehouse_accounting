package rest_api_warehouse_accounting.dto.referenceBooks;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDeleteDto {

    //@NotEmpty(message = "Имя пользователя не может быть пустым")
    private String username;

    //@NotEmpty(message = "Пароль не может быть пустым")
    private String password;
}
