package com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.v1;

/*
 * =====================================================================
 * TÍTULO DA CLASSE: PersonMapper_MapStruct
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Interface de mapeamento baseada em MapStruct. Responsável por gerar
 * automaticamente o código de conversão (em tempo de compilação) entre
 * objetos da entidade `Person` e o DTO oficial `PersonDTO` (tanto para
 * instâncias únicas quanto para listas), utilizando o container do Spring.
 * =====================================================================
 */

import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)// Diz ao Spring para gerenciar esta interface como um Bean
public interface PersonMapper_MapStruct {

    // Com o componentModel = "spring", removemos o INSTANCE.
    // O Spring injetará este mapper automaticamente onde você precisar.

    /*
     * mapEntityToDto -> retorna um objeto do tipo PersonDTO
     * Este método é responsável por mapear uma instância da entidade Person para o DTO PersonDTO.
     * O MapStruct gera automaticamente a implementação (convertendo a entidade em DTO)
     * deste método em tempo de compilação.
     */
    // Mapeamento de Objeto Único: Entity ➔ DTO
    PersonDTO mapEntityToDto(Person person);

    /*
     * mapDtoToEntity -> retorna uma entidade do tipo Person
     * Este método é responsável por mapear um objeto de transferência (PersonInsertDTO)
     * de volta para a entidade de domínio e persistência (Person).
     * Assim como no método anterior, o MapStruct gera toda a lógica de conversão
     * automaticamente em tempo de compilação, garantindo alta performance
     * e segurança de tipos.
     */
     // Mapeamento de Objeto Único: DTO ➔ Entity (criação)
    Person mapDtoToEntity(PersonInsertDTO personDto);

    /*
     * mapEntityListToDtoList -> retorna uma lista de objetos do tipo PersonDTO
     * Este método é responsável por converter uma coleção de entidades (List<Person>)
     * em uma lista de contratos de transferência (List<PersonDTO>).
     * Por baixo dos panos, o MapStruct gera em tempo de compilação um loop otimizado
     * (como um stream ou laço for) que aplica o mapeamento de cada elemento
     * individualmente, garantindo máxima performance sem overhead de reflexão.
     */
     // Mapeamento de Listas: Entity List ➔ DTO List
    List<PersonDTO> mapEntityListToDtoList(List<Person> personList);

    /*
     * mapDtoListToEntityList -> retorna uma lista de entidades do tipo Person
     * Este método realiza a conversão reversa de uma lista de contratos de transferência
     * (List<PersonDTO>) para uma lista de entidades de persistência (List<Person>).
     * Assim como na conversão anterior, o MapStruct gera um código otimizado em tempo de
     * compilação para iterar sobre a coleção e mapear cada DTO para sua respectiva entidade,
     * mantendo alta performance e segurança de tipos sem o uso de reflexão em tempo de execução.
     */
     // Mapeamento de Listas: DTO List ➔ Entity List
    List<Person> mapDtoListToEntityList(List<PersonDTO> personDtoList);

    /*
     * ============================================================================
     * ATUALIZAÇÃO DE ENTIDADE EXISTENTE (UPDATE IN-PLACE)
     * ============================================================================
     */
    /*
     * updateEntityFromDTO -> Atualiza uma entidade existente gerenciada pelo Hibernate
     * O MapStruct utiliza a anotação @MappingTarget para indicar que a conversão
     * não deve instanciar um novo objeto, mas sim injetar os dados do `PersonUpdateDTO`
     * diretamente na instância de `Person` já recuperada do banco de dados.
     *
     * Comportamento equivalente ao Mapper Manual:
     * Atualiza os campos permitidos, mantendo id, cpf, email, senha e role intactos por segurança.
     */
    void updateEntityFromDTO(PersonUpdateDTO dto, @MappingTarget Person entity);
}