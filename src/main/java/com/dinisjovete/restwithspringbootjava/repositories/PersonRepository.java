package com.dinisjovete.restwithspringbootjava.repositories;

import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * public interface JpaRepository<T, ID>
 * JpaRepository -> essa interface nativa espera receber como parâmetro
 * um tipo no caso(Person), e um Id do tipo Long, que é o tipo do id da entidade Person.
 * A interface JpaRepository fornece métodos prontos para realizar operações de CRUD (Create, Read, etc).
 */
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Adicionado para permitir a verificação individual no Seed e futuras buscas por e-mail
    Optional<Person> findByEmail(String email);

}