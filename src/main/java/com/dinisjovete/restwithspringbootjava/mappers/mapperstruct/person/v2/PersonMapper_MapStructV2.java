package com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.person.v2;

/*
 * =====================================================================
 * TÍTULO DA CLASSE: PersonMapper_MapStructV2
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Interface de mapeamento baseada em MapStruct. Responsável por gerar
 * automaticamente o código de conversão (em tempo de compilação) entre
 * a entidade `Person` e os DTOs de Person (único e lista), utilizando
 * o container do Spring.
 *
 * Person representa apenas o CADASTRO. Livros, pedidos e produtos não
 * são mapeados aqui: cada um tem o seu próprio mapper e DTO.
 * =====================================================================
 */

import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE) // Diz ao Spring para gerenciar esta interface como um Bean
public interface PersonMapper_MapStructV2 {

    // Com o componentModel = "spring", o Spring injeta este mapper onde for preciso.

    /*
     * mapEntityToDto -> converte uma entidade Person em PersonDTOV2.
     * O MapStruct gera a implementação em tempo de compilação.
     * Usado por findAll, findById, create e update.
     */
    // Mapeamento de Objeto Único: Entity ➔ DTO
    PersonDTOV2 mapEntityToDto(Person person);

    /*
     * mapDtoToEntity -> converte o DTO de criação (PersonInsertDTOV2)
     * na entidade de persistência (Person).
     */
    // Mapeamento de Objeto Único: DTO ➔ Entity (criação)
    Person mapDtoToEntity(PersonInsertDTOV2 personDto);

    /*
     * mapEntityListToDtoList -> converte List<Person> em List<PersonDTOV2>.
     * O MapStruct gera um loop que aplica mapEntityToDto a cada elemento.
     */
    // Mapeamento de Listas: Entity List ➔ DTO List
    List<PersonDTOV2> mapEntityListToDtoList(List<Person> personList);

    /*
     * mapDtoListToEntityList -> conversão reversa: List<PersonDTOV2> em List<Person>.
     * Usa o método mapDtoToEntity(PersonDTOV2) abaixo para cada elemento.
     */
    // Mapeamento de Listas: DTO List ➔ Entity List
    List<Person> mapDtoListToEntityList(List<PersonDTOV2> personDtoList);

    /*
     * Mapeamento de Objeto Único: DTO ➔ Entity, usado pela lista acima.
     */
    Person mapDtoToEntity(PersonDTOV2 personDto);

    /*
     * ============================================================================
     * ATUALIZAÇÃO DE ENTIDADE EXISTENTE (UPDATE IN-PLACE)
     * ============================================================================
     */
    /*
     * updateEntityFromDTO -> atualiza uma entidade existente gerenciada pelo Hibernate.
     * @MappingTarget indica que o MapStruct não cria um novo objeto: ele copia os
     * dados do PersonUpdateDTOV2 para a instância de Person já carregada do banco.
     *
     * Atualiza apenas os campos permitidos; id, cpf, email, senha e role
     * permanecem intactos por segurança (não existem no PersonUpdateDTOV2).
     */
    void updateEntityFromDTO(PersonUpdateDTOV2 dto, @MappingTarget Person entity);
}