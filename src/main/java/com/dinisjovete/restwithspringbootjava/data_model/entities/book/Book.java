package com.dinisjovete.restwithspringbootjava.data_model.entities.book;

import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.product.Product;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/*
 * A classe Book corresponde a uma entidade do banco de dados, representando
 * uma tabela chamada "books".
 *
 * Cada instância da classe Book representa uma linha (registro) nessa tabela.
 *
 * A anotação @Entity indica que a classe é uma entidade JPA (Java Persistence API).
 *
 * A anotação @Table(name = "books") especifica o nome da tabela no banco de dados
 * que essa entidade representa.
 *
 * A classe Book herda da classe Product, que contém os atributos comuns a todos
 * os produtos do sistema, como:
 *
 * - id
 * - title
 * - price
 * - launchDate
 *
 * Como Product utiliza @MappedSuperclass, seus atributos são herdados por Book
 * e também fazem parte da tabela "books" no banco de dados.
 *
 * Dessa forma, Book mantém apenas características específicas de livros.
 *
 * Exemplo da estrutura da tabela:
 *
 * books
 * -----------------
 * id              (Product)
 * title           (Product)
 * price           (Product)
 * launch_date     (Product)
 * author_id       (Book)
 *
 *
 * A interface Serializable permite que objetos dessa classe possam ser
 * convertidos em uma sequência de bytes, o que é útil para persistência,
 * envio pela rede ou armazenamento em cache.
 *
 * O campo serialVersionUID é usado para garantir a compatibilidade durante
 * a serialização e desserialização, evitando problemas quando a classe é
 * modificada.
 */
@Entity
@Table(name = "books")
public class Book extends Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /*
     * Este atributo é específico da entidade Book.
     *
     * Nem todo produto possui um autor, por isso ele não pertence à classe
     * Product.
     *
     * O autor agora é representado por uma entidade Person, pois um autor
     * possui informações próprias que podem ser reutilizadas pelo sistema.
     *
     * O relacionamento é ManyToOne porque:
     *
     * - Um livro possui apenas um autor.
     * - Uma mesma pessoa pode ser autora de vários livros.
     *
     * Relacionamento:
     *
     * Person                    Book
     * ------                    ----
     * 1       ----------------  0..*
     *
     * Exemplo:
     *
     * Person
     * id = 1
     * nome = Machado de Assis
     *
     * Books
     * Dom Casmurro  -> author_id = 1
     * Quincas Borba -> author_id = 1
     *
     *
     * Esta é uma associação entre Book e Person.
     *
     * O livro possui uma referência para a pessoa autora, porém ambos possuem
     * ciclo de vida independente.
     *
     * A exclusão de um livro não remove a pessoa autora.
     *
     * No banco de dados, a tabela books não armazena todos os dados da pessoa.
     * Ela armazena apenas o identificador do autor através da coluna author_id,
     * que funciona como chave estrangeira para a tabela de pessoas.
     *
     * Exemplo:
     *
     * - Book possui author (Person).
     * - Movie possui director (Person ou outra entidade específica).
     * - Game possui developer (Person ou outra entidade específica).
     */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Person author;


    public Book() {
    }


    /*
     * O construtor chama super(...), que executa o construtor da classe pai
     * Product, responsável por inicializar os atributos comuns.
     *
     * Depois disso, Book inicializa apenas seu próprio atributo específico:
     *
     * - author
     */
    public Book(
            Long id,
            String title,
            java.math.BigDecimal price,
            java.time.LocalDateTime launchDate,
            Person author
    ) {
        super(id, title, price, launchDate);
        this.author = author;
    }


    public Person getAuthor() {
        return author;
    }


    public void setAuthor(Person author) {
        this.author = author;
    }


    /*
     * A comparação continua sendo feita pelo id herdado de Product.
     *
     * O id representa a identidade do objeto no banco de dados.
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Book book)) return false;

        return Objects.equals(getId(), book.getId());
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}