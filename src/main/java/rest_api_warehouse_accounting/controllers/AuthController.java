package rest_api_warehouse_accounting.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import rest_api_warehouse_accounting.dto.AuthenticationDTO;
import rest_api_warehouse_accounting.dto.PersonDTO;
import rest_api_warehouse_accounting.dto.PersonDeleteDTO;
import rest_api_warehouse_accounting.dto.PersonUpdateDTO;
import rest_api_warehouse_accounting.model.directory.Person;
import rest_api_warehouse_accounting.service.PeopleService;
import rest_api_warehouse_accounting.util.JWTUtil;
import rest_api_warehouse_accounting.validation.PersonValidator;


import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final PeopleService peopleService;
    private final JWTUtil jwtUtil;
    private final PersonValidator personValidator;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/login") // Обрабатываем POST-запрос по адресу /login
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthenticationDTO authDTO) {
        Logger log = LoggerFactory.getLogger(AuthController.class);

        // Логируем попытку входа
        log.info("Login attempt for user: {}", authDTO.getUsername());

        try {
            // Создаем объект аутентификации с введенными данными
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());

            // Выполняем аутентификацию через Spring Security
            authenticationManager.authenticate(authenticationToken);

            // Если аутентификация успешна, получаем пользователя из базы
            Optional<Person> personOptional = peopleService.findByUsername(authDTO.getUsername());

            // Проверяем, что пользователь существует (на всякий случай)
            if (personOptional.isEmpty()) {
                log.warn("Login failed: User '{}' not found", authDTO.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            // Получаем роль пользователя из базы данных
            Person person = personOptional.get();
            String role = person.getRole(); // Получаем роль из сущности Person (можно настроить на основе данных)

            // Генерируем JWT токен с учетом роли
            String token = jwtUtil.generateToken(authDTO.getUsername(), role);

            log.info("User '{}' successfully logged in", authDTO.getUsername());

            // Возвращаем успешный ответ с токеном
            return ResponseEntity.ok(Map.of("jwt-token", token));

        } catch (BadCredentialsException e) {
            log.warn("Login failed: Incorrect password for user '{}'", authDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Incorrect login or password"));
        }
    }

    @PostMapping("/registration") // Обрабатываем POST-запрос по адресу /registration
    public Map<String, String> register(@RequestBody @Valid PersonDTO personDTO,
                                        BindingResult bindingResult) {

        // Конвертируем DTO в сущность Person (чтобы затем работать с ней)
        Person person = peopleService.convertDTOToPerson(personDTO);

        // Проверяем валидность данных пользователя (например, уникальность email, username и т. д.)
        personValidator.validate(person, bindingResult);

        // Если есть ошибки валидации, возвращаем их список
        if (bindingResult.hasErrors()) {
            return Map.of("message", "Validation failed", "errors", bindingResult.getAllErrors().toString());
        }

        // Проверяем, существует ли пользователь с таким же username (лучше проверять перед созданием)
        if (peopleService.findByUsername(person.getUsername()).isPresent()) {
            return Map.of("message", "User with this username already exists");
        }


        // Обрабатываем роль (добавляем префикс "ROLE_", если его нет)
        String role = personDTO.getRole() != null ? personDTO.getRole().toUpperCase() : "USER";
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        person.setRole(role);

        // Сохраняем пользователя в базе
        peopleService.savePerson(person);

        // Генерируем JWT-токен для нового пользователя
        String token = jwtUtil.generateToken(person.getUsername(), role);

        // Возвращаем токен клиенту (чтобы он мог сразу использовать авторизацию)
        return Map.of("jwt-token", token);
    }

    @PostMapping("/updateUser") // Обрабатываем POST-запрос по адресу /updateUser
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> updateUser(@RequestBody @Valid PersonUpdateDTO personDTO,
                                          BindingResult bindingResult) {

        // Ищем пользователя по username в базе данных
        Optional<Person> existingPerson = peopleService.findByUsername(personDTO.getUsername());

        // Если пользователь не найден, возвращаем сообщение об ошибке
        if (existingPerson.isEmpty()) {
            return Map.of("message", "User not found");
        }

        // Получаем объект Person из Optional (теперь он точно не пустой)
        Person personToUpdate = existingPerson.get();

        // Проверяем, есть ли ошибки валидации (например, пустые или некорректные поля)
        if (bindingResult.hasErrors()) {
            return Map.of("message", "error body");
        }

        // Обновляем только переданные поля
        if (personDTO.getYearOfBirth() != null) {
            personToUpdate.setYearOfBirth(personDTO.getYearOfBirth());
        }
        if (personDTO.getEmail() != null) {
            personToUpdate.setEmail(personDTO.getEmail());
        }
        if (personDTO.getRole() != null) {
            // Приводим роль к верхнему регистру и добавляем "ROLE_", если его нет
            String role = personDTO.getRole().toUpperCase();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            personToUpdate.setRole(role);
        }


        // Сохраняем обновленного пользователя в базе данных
        peopleService.updatePerson(personToUpdate);

        // Возвращаем успешный ответ с обновленными данными
        return Map.of(
                "username", personToUpdate.getUsername(),
                "yearOfBirth", personToUpdate.getYearOfBirth(),
                "email", personToUpdate.getEmail(),
                "role", personToUpdate.getRole(),
                "status", "updated"
        );
    }

    @PostMapping("/deleteUser") // Обрабатываем POST-запрос по адресу /deleteUser
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> deleteUser(@RequestBody @Valid PersonDeleteDTO personDTO,
                                          BindingResult bindingResult) {

        // Проверяем, есть ли ошибки валидации (например, пустые поля)
        if (bindingResult.hasErrors()) {
            return Map.of("message", "error body");
        }

        // Ищем пользователя по username в базе данных
        Optional<Person> person = peopleService.findByUsername(personDTO.getUsername());

        // Если пользователь не найден, возвращаем сообщение об ошибке
        if (person.isEmpty()) {
            return Map.of("message", "User not found");
        }

        // Получаем объект Person из Optional (теперь он точно не пустой)
        Person personToDelete = person.get();

        // Удаляем пользователя из базы данных по ID
        peopleService.deletePerson(personToDelete.getId());

        // Возвращаем успешный ответ о том, что пользователь удален
        return Map.of(
                "username", personToDelete.getUsername(),
                "status", "delete"
        );
    }

}
