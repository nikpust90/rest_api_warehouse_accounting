package rest_api_warehouse_accounting.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import rest_api_warehouse_accounting.model.Person;
import rest_api_warehouse_accounting.security.PersonDetailsService;


@Component
@RequiredArgsConstructor
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        try {
            personDetailsService.loadUserByUsername(person.getUsername());
        } catch (UsernameNotFoundException e) {
            return;
        }

        errors.rejectValue("username", "user.found.name",
                "User with name " + person.getUsername() + " already exists");
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }
}