package com.dinisjovete.restwithspringbootjava.repositories;

import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * public interface JpaRepository<T, ID>
 * JpaRepository -> essa interface nativa espera receber como parâmetro
 * um tipo no caso(Person), e um Id do tipo Long, que é o tipo do id da entidade Person.
 * A interface JpaRepository fornece métodos prontos para realizar operações de CRUD (Create, Read, etc).
 *
 * Busca por ID: já vem pronta (findById). É o identificador padrão da API (/person/{id}).
 */
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Chave natural única (e-mail não se repete): login, seed e buscas por e-mail.
    Optional<Person> findByEmail(String email);

    // Chave natural única (CPF não se repete): busca interna. Não expor o CPF na URL (LGPD).
    Optional<Person> findByCpf(String cpf);

    // Verifica duplicidade no cadastro sem carregar a entidade inteira.
    boolean existsByEmail(String email);

    // Chave natural única (CPF não se repete): verificação de duplicidade no cadastro.
    boolean existsByCpf(String cpf);

}