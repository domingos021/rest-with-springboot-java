package com.dinisjovete.restwithspringbootjava.mappers;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    public PersonMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
    // INSERT DTO -> ENTITY (CRIAÇÃO COM BCRYPT & ROLE PADRÃO)
    // ------------------------------------------------------------------------
    public Person toEntity(PersonInsertDTO dto) {
        return Optional.ofNullable(dto)
                .map(d -> {
                    Person entity = new Person(); //a variável entity tem a referencia dos dados do novo objeto person

                    /*
                    *seta dentro de cada atributo na entidade(person) os valores que vieram do dto, que veio do controller, que veio da requisição do cliente
                    *  1. O Controller recebe a requisição HTTP POST para criar uma nova pessoa
                    *  2. O Controller chama o método create() do Service, passando o DTO
                    *  3. O Service chama o método toEntity() do Mapper, que converte o DTO em uma nova entidade Person
                    *  4. O Mapper cria uma nova instância de Person e popula seus campos com os dados do DTO
                    *  5. O Service então chama o método save() do Repository, que persiste a nova entidade no banco de dados
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

                    /*
                    * embora o insertDto fornece os dados para criação da nova pessoa ela deixa para p mapper
                    * fazer a logica da criptográfia da senha via BCrypt
                     */
                    // Criptógrafa a senha em texto plano com BCrypt antes da persistência
                    if (d.getPassword() != null) {
                        entity.setPassword(passwordEncoder.encode(d.getPassword()));
                    }

                    // Atribui a role informada no DTO ou define CLIENT como padrão
                    if (d.getRole() != null) {
                        entity.setRole(d.getRole());
                    } else {
                        entity.setRole(PersonRole.CLIENT);
                    }

                    return entity; //retorna a nova entidade criada no banco
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
   │        ▼
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
- Centralizar toda a conversão entre Entidade <-> DTO.
- Evitar duplicação de código de conversão em Controllers e Services.
- Manter o Service focado apenas nas regras de negócio.
- Garantir que apenas os dados necessários sejam expostos na API.
==============================================================================
*/