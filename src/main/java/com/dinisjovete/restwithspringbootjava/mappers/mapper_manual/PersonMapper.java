package com.dinisjovete.restwithspringbootjava.mappers.mapper_manual;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import org.springframework.stereotype.Component;

import java.util.Optional;

// ============================================================================
// CAMADA DE MAPPER - UTILITÁRIO DE CONVERSÃO ENTRE ENTIDADE PERSON <-> DTO
// ============================================================================
// Propósito Principal:
// Centraliza toda a lógica de conversão entre a camada de persistência (Entidade Person)
// e a camada de contrato da API (DTOs).
// ============================================================================

/**
 * Componente Spring responsável por mapear entidades Person e DTOs.
 */
@Component
public class PersonMapper {

    // ------------------------------------------------------------------------
    // ENTITY -> DTO (LEITURA / RESPOSTA DA API)
    // ------------------------------------------------------------------------
    public PersonDTO toDTO(Person entity) {
        return Optional.ofNullable(entity)
                /*
                 * Optional.ofNullable(entity)
                 * - cria um Optional contendo a entidade Person.
                 * - se entity for null, será criado um Optional vazio.
                 *
                 * .map(...)
                 * - executa a conversão apenas se existir um objeto Person.
                 * - recebe a entidade (e) e cria um novo PersonDTO, preenchendo-o com os dados da entidade Person.
                 *
                 * .orElse(null)
                 * - se o Optional estiver vazio (entity == null),
                 *   retorna null em vez de tentar criar o DTO.
                 */
                .map(e -> new PersonDTO(
                        e.getId(),
                        e.getFirstName(),
                        e.getLastName(),
                        e.getCpf(),
                        e.getEmail(),
                        e.getAddress(),
                        e.getGender(),
                        e.getRole()
                ))
                .orElse(null);
    }

    // ------------------------------------------------------------------------
    // INSERT DTO -> ENTITY (CRIAÇÃO PURA DE DADOS)
    // ------------------------------------------------------------------------
    public Person toEntity(PersonInsertDTO dto) {
        return Optional.ofNullable(dto)
                .map(d -> {
                    Person entity = new Person(); // a variável entity tem a referencia dos dados do novo objeto person
                    /*
                     * ============================================================================
                     * ARQUITETURA DE MAPEAMENTO E SEGURANÇA: FLUXO DE CRIAÇÃO (INSERT)
                     * ============================================================================
                     *
                     * EXPLICAÇÃO DO CICLO DE VIDA E SEPARAÇÃO DE RESPONSABILIDADES:
                     *
                     * 1. Papel do Mapper (PersonMapper / Dozer / MapStruct):
                     *    - O Mapper atua estritamente como um tradutor de estruturas de dados.
                     *    - Ele recebe o `PersonInsertDTO` (que contém todos os dados da requisição,
                     *      incluindo a senha em texto plano) e popula a nova entidade `Person`.
                     *    - Por questões de Clean Architecture e separação de responsabilidades, o
                     *      Mapper NÃO lida com criptografia. Ele mapeia os campos seguros (nome,
                     *      cpf, email, endereço, gênero e role) e deixa intencionalmente a senha
                     *      em texto plano de fora da tradução direta.
                     *
                     * 2. Papel da Camada de Serviço (PersonService):
                     *    - O Service coordena as regras de negócio e de segurança da aplicação.
                     *    - Após receber a entidade semi-pronta vinda do Mapper, o Service intercepta
                     *      o fluxo para tratar a senha sensível.
                     *    - Ele resgata a senha original em texto plano diretamente do DTO de entrada
                     *      (`personDto.getPassword()`), aplica o hash seguro utilizando o BCrypt
                     *      via `PasswordEncoder`, e injeta o resultado na entidade (`person.setPassword(...)`).
                     *
                     * 3. Persistência Final:
                     *    - Com a entidade 100% completa (dados estruturais mapeados + senha com
                     *      hash seguro), o Service aciona o Repository para persistir o registro
                     *      de forma segura no Banco de Dados.
                     *
                     * ============================================================================
                     * DIAGRAMA DE FLUXO: DA REQUISIÇÃO HTTP AO BANCO DE DADOS
                     * ============================================================================
                     *
                     *  [ Cliente Web / API ]
                     *         │
                     *         │ (Envia requisição HTTP POST com PersonInsertDTO contendo senha em texto plano)
                     *         ▼
                     *  [ PersonController ]
                     *         │
                     *         │ (Repassa o DTO para o Service)
                     *         ▼
                     *  [ PersonService.create(personDto) ]
                     *         │
                     *         ├── 1. Aciona o Mapper: PersonMapper.toEntity(personDto)
                     *         │      │
                     *         │      ├── Copia dados estruturais (Nome, CPF, Email, Endereço, Gênero, Role)
                     *         │      └── Retorna a entidade `Person` (com a senha ainda vazia/não tratada)
                     *         │
                     *         ├── 2. Aplica Regra de Negócio e Segurança de Senha:
                     *         │      │
                     *         │      ├── Lê a senha do DTO: personDto.getPassword()
                     *         │      ├── Hashea a senha via BCrypt: passwordEncoder.encode(...)
                     *         │      └── Seta na entidade: person.setPassword(hashSeguro)
                     *         │
                     *         ├── 3. Persistência: repository.save(person)
                     *         │      │
                     *         │      └── JPA/Hibernate executa o comando SQL INSERT no Banco de Dados
                     *         │
                     *         └── 4. Conversão de Resposta: mapper.toDTO(savedPerson)
                     *                │
                     *                └── Converte a entidade salva em `PersonDTO` (filtrando campos sensíveis)
                     *         │
                     *         ▼
                     *  [ Retorna Resposta HTTP 201 Created com o DTO seguro ao Cliente ]
                     * ============================================================================
                     */

                    /*
                     *seta dentro de cada atributo na entidade(person) os valores que vieram do dto, que veio do controller, que veio da requisição do cliente
                     *  1. O Controller recebe a requisição HTTP POST para criar uma nova pessoa
                     *  2. O Controller chama o método create() do Service, passando o DTO
                     *  3. O Service chama o método toEntity() do Mapper, que converte o DTO em uma nova entidade Person
                     *  4. O Mapper cria uma nova instância de Person e popula seus campos com os dados do DTO
                     *  5. O Service então aplica a criptografia da senha e chama o método save() do Repository, que persiste a nova entidade no banco de dados
                     *  6. O Repository retorna a entidade persistida (com ID gerado) para o Service
                     *  7. O Service chama o método toDTO() do Mapper para converter a entidade persistida em um DTO de resposta
                     *  8. O Mapper cria um novo PersonDTO com os dados da entidade persistida, filtrando campos sensíveis como a senha
                     *  9. O Service retorna o DTO de resposta para o Controller, que por sua vez envia a resposta HTTP ao cliente
                     *  10. O cliente recebe a resposta com os dados da nova pessoa criada, sem expor a senha ou outros dados sensíveis
                     */
                    entity.setFirstName(d.getFirstName());
                    entity.setLastName(d.getLastName());
                    entity.setCpf(d.getCpf());
                    entity.setEmail(d.getEmail());
                    entity.setAddress(d.getAddress());
                    entity.setGender(d.getGender());

                    // Atribui a role informada no DTO ou define CLIENT como padrão caso venha nula
                    if (d.getRole() != null) {
                        entity.setRole(d.getRole());
                    } else {
                        entity.setRole(PersonRole.CLIENT);
                    }

                    // Nota: A criptografia da senha (BCrypt) agora é responsabilidade da camada de Serviço (Service).

                    return entity; // retorna a nova entidade mapeada
                })
                .orElse(null);
    }

    // ------------------------------------------------------------------------
    // UPDATE DTO -> ENTIDADE GERENCIADA EXISTENTE
    // ------------------------------------------------------------------------
    public void updateEntityFromDTO(PersonUpdateDTO dto, Person entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Atualiza apenas os campos permitidos no contrato de atualização
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAddress(dto.getAddress());
        entity.setGender(dto.getGender());

        // Nota: ID, CPF, E-mail, Senha e Role são intencionalmente omitidos aqui por segurança/integridade.
    }
}


/*
==============================================================================
FLUXO COMPLETO DA REQUISIÇÃO (DTO -> ENTITY -> DTO)
==============================================================================

Cliente
   │
   ▼
Controller
   │
   ▼
Service
   │
   ├── PersonMapper.toEntity(dto)
   │        │
   │        ▼
   │      Person (Entidade)
   │        │
   │        ▼ (Service aplica BCrypt na senha)
   │   Repository.save()
   │        │
   │        ▼
   │   Person persistida
   │        │
   │        ▼
   └── PersonMapper.toDTO(entity)
            │
            ▼
         PersonDTO
            │
            ▼
Controller
   │
   ▼
Resposta HTTP para o Cliente

==============================================================================
Objetivo do Mapper:
- Centralizar toda a conversão puramente estrutural entre Entidade <-> DTO.
- Evitar duplicação de código de conversão em Controllers e Services.
- Manter o Mapper focado apenas em traduzir dados, delegando regras de segurança ao Service.
- Garantir que apenas os dados necessários sejam expostos na API.
==============================================================================
*/