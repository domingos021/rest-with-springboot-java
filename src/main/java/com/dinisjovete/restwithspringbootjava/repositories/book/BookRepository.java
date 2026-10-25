package com.dinisjovete.restwithspringbootjava.repositories.book;

import com.dinisjovete.restwithspringbootjava.data_model.entities.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;


/*
 * public interface JpaRepository<T, ID>
 * JpaRepository -> essa interface nativa espera receber como parâmetro
 * um tipo no caso(Book), e um Id do tipo Long, que é o tipo do id da entidade Book.
 * A interface JpaRepository fornece métodos prontos para realizar operações de CRUD (Create, Read, etc).
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    // Usado pelo seed (BookTestDataConfig) para não duplicar livros pelo título.
    // O Spring Data gera a consulta pelo nome do método (campo "title", herdado de Product).
    boolean existsByTitle(String title);
    boolean existsByAuthorId(Long authorId);

}