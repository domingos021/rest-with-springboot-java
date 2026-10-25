package com.dinisjovete.restwithspringbootjava.data_model.entities.product;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/*
 *                 Product (abstrata)
 *                 ------------------
 *                 id
 *                 title
 *                 price
 *                 launchDate
 *                       ▲
 *                       |
 *        ┌──────────────┼──────────────┐
 *        |              |              |
 *      Book           Movie          Game
 *      -----          -----          -----
 *      author         director       developer
 *
 *
 * A classe Product representa um conceito genérico de produto do sistema.
 *
 * Ela foi criada para concentrar atributos e comportamentos comuns a todos
 * os tipos de produtos, evitando duplicação de código nas classes filhas.
 *
 * Por ser uma classe abstrata, ela não pode ser instanciada diretamente.
 *
 * Apenas suas subclasses concretas (como Book, Movie, Game, etc.) poderão
 * ser instanciadas e persistidas como entidades JPA.
 *
 * Cada classe filha herdará automaticamente todos os atributos e métodos
 * definidos aqui, podendo ainda adicionar suas próprias características
 * e comportamentos específicos.
 *
 * A anotação @MappedSuperclass informa ao JPA que esta classe não representa
 * uma tabela própria no banco de dados.
 *
 * Seus atributos serão herdados pelas entidades filhas, e cada entidade filha
 * terá sua própria tabela contendo esses campos.
 *
 * Exemplo:
 *
 * Product
 *    |
 *    └── Book
 *
 * A tabela "books" possuirá os campos herdados:
 *
 * id
 * title
 * price
 * launch_date
 *
 * juntamente com os campos específicos de Book.
 *
 * A interface Serializable permite que objetos dessa classe possam ser
 * convertidos em uma sequência de bytes, o que é útil para persistência,
 * envio pela rede ou armazenamento em cache.
 *
 * O campo serialVersionUID é usado para garantir a compatibilidade durante
 * a serialização e desserialização, evitando problemas quando a classe é
 * modificada.
 */
@MappedSuperclass
public abstract class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /*
     * Os atributos abaixo representam informações comuns a qualquer produto.
     *
     * Toda classe que herdar de Product receberá automaticamente esses
     * atributos, sem necessidade de duplicar código.
     *
     * nullable = false -> não pode ser nulo.
     *
     * length = 255 -> tamanho máximo de um texto.
     * (válido apenas para String)
     *
     * precision e scale -> definem a precisão de valores decimais.
     *
     * Com spring.jpa.hibernate.ddl-auto=none o Hibernate NÃO cria nem altera
     * a estrutura do banco.
     *
     * Portanto, os nomes das colunas e seus respectivos tipos precisam estar
     * de acordo com as migrations criadas manualmente.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "title", nullable = false, length = 255)
    private String title;


    /*
     * BigDecimal corresponde ao tipo DECIMAL do banco de dados.
     *
     * Double não é recomendado para representar valores monetários,
     * pois pode gerar pequenos erros de arredondamento.
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;


    /*
     * LocalDateTime corresponde ao tipo TIMESTAMP da migration.
     *
     * Representa a data de lançamento do produto.
     */
    @Column(name = "launch_date", nullable = false)
    private LocalDateTime launchDate;


    public Product() {
    }


    public Product(Long id, String title, BigDecimal price, LocalDateTime launchDate) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.launchDate = launchDate;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public LocalDateTime getLaunchDate() {
        return launchDate;
    }


    public void setLaunchDate(LocalDateTime launchDate) {
        this.launchDate = launchDate;
    }


    /*
     * A comparação é feita utilizando o id herdado.
     *
     * O id representa a identidade do registro no banco de dados.
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Product product)) return false;

        return Objects.equals(getId(), product.getId());
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}