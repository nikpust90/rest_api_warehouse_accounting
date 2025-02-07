package rest_api_warehouse_accounting.repositories.referenceBooks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rest_api_warehouse_accounting.model.referenceBooks.Person;


import java.util.Optional;


public interface PeopleRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByUsername(String username);

}
