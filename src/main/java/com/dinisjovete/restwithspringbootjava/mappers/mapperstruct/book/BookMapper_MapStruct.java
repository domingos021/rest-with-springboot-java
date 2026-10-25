package com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.book;

/*
 * =====================================================================
 * TÍTULO DA CLASSE: Bookapper_MapStruct
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Interface de mapeamento baseada em MapStruct. Responsável por gerar
 * automaticamente o código de conversão (em tempo de compilação) entre
 * objetos da entidade `Book` e os DTOs de livro (`BookDTO`, `BookInsertDTO`
 * e `BookUpdateDTO`), tanto para instâncias únicas quanto para listas,
 * utilizando o container do Spring.
 * =====================================================================
 */

import com.dinisjovete.restwithspringbootjava.data.dto.book.BookDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.book.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)// Diz ao Spring para gerenciar esta interface como um Bean
public interface BookMapper_MapStruct {

    // Com o componentModel = "spring", removemos o INSTANCE.
    // O Spring injetará este mapper automaticamente onde você precisar.

    /*
     * mapEntityToDto -> retorna um objeto do tipo BookDTO
     * Este método é responsável por mapear uma instância da entidade Book para o DTO BookDTO.
     * O MapStruct gera automaticamente a implementação (convertendo a entidade em DTO)
     * deste método em tempo de compilação.
     */
    // Mapeamento de Objeto Único: Entity ➔ DTO
    BookDTO mapEntityToDto(Book book);

    /*
     * mapDtoToEntity -> retorna uma entidade do tipo Book
     * Este método é responsável por mapear um objeto de transferência (BookInsertDTO)
     * de volta para a entidade de domínio e persistência (Book).
     * Assim como no método anterior, o MapStruct gera toda a lógica de conversão
     * automaticamente em tempo de compilação, garantindo alta performance
     * e segurança de tipos.
     */
    // Mapeamento de Objeto Único: DTO ➔ Entity (criação)
    Book mapDtoToEntity(BookInsertDTO bookDto);

    /*
     * mapEntityListToDtoList -> retorna uma lista de objetos do tipo BookDTO
     * Este método é responsável por converter uma coleção de entidades (List<Book>)
     * em uma lista de contratos de transferência (List<BookDTO>).
     * Por baixo dos panos, o MapStruct gera em tempo de compilação um loop otimizado
     * (como um stream ou laço for) que aplica o mapeamento de cada elemento
     * individualmente, garantindo máxima performance sem overhead de reflexão.
     */
    // Mapeamento de Listas: Entity List ➔ DTO List
    List<BookDTO> mapEntityListToDtoList(List<Book> bookList);

    /*
     * mapDtoListToEntityList -> retorna uma lista de entidades do tipo Book
     * Este método realiza a conversão reversa de uma lista de contratos de transferência
     * (List<BookDTO>) para uma lista de entidades de persistência (List<Book>).
     * Assim como na conversão anterior, o MapStruct gera um código otimizado em tempo de
     * compilação para iterar sobre a coleção e mapear cada DTO para sua respectiva entidade,
     * mantendo alta performance e segurança de tipos sem o uso de reflexão em tempo de execução.
     */
    // Mapeamento de Listas: DTO List ➔ Entity List
    List<Book> mapDtoListToEntityList(List<BookDTO> bookDtoList);

    /*
     * ============================================================================
     * ATUALIZAÇÃO DE ENTIDADE EXISTENTE (UPDATE IN-PLACE)
     * ============================================================================
     */
    /*
     * updateEntityFromDTO -> Atualiza uma entidade existente gerenciada pelo Hibernate
     * O MapStruct utiliza a anotação @MappingTarget para indicar que a conversão
     * não deve instanciar um novo objeto, mas sim injetar os dados do `BookUpdateDTO`
     * diretamente na instância de `Book` já recuperada do banco de dados.
     *
     * Comportamento equivalente ao Mapper Manual:
     * Atualiza os campos permitidos (title, price e launchDate), mantendo id e author
     * intactos por segurança.
     */
    void updateEntityFromDTO(BookUpdateDTO dto, @MappingTarget Book entity);
}