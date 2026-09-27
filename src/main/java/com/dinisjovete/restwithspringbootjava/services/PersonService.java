package com.dinisjovete.restwithspringbootjava.services;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.PersonMapper;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class PersonService {

    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger =
            Logger.getLogger(PersonService.class.getName());

    // Injeção de Dependência do Repositório JPA para acesso ao Banco de Dados
    private final PersonRepository repository;

    // Injeção de Dependência do Mapper para centralizar conversões e criptografia
    private final PersonMapper mapper;

    @Autowired   // injetando as dependências no construtor
    public PersonService(PersonRepository repository, PersonMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public PersonDTO findById(Long id) {

        logger.info("Finding one Person!");

        /*
         * ============================================================================
         * FLUXO COM BANCO DE DADOS (JPA REPOSITORY)
         * ============================================================================
         * O Service solicita ao Repository que busque a pessoa no banco de dados
         * tendo como referência o ID enviado pelo Controller.
         *
         * Tratamento de Ausência com Optional (.orElseThrow):
         * Substitui a condicional 'if' tradicional de forma elegante e funcional:
         *
         *   [Método Tradicional com IF]
         *   Optional<Person> optional = repository.findById(id);
         *   if (!optional.isPresent()) {
         *       throw new ResourceNotFoundException("No record found for this ID!");
         *   }
         *   Person entity = optional.get();
         *
         * Caso não encontre o registro, o .orElseThrow lança a nossa ResourceNotFoundException
         * (que é capturada e tratada globalmente pelo ControllerAdvice).
         *
         * Fluxo de Camadas:
         * PersonController -> PersonService -> PersonRepository -> Banco de Dados (Tabela person)
         * ============================================================================
         */
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this ID!"));
        /*
         * O Repository solicita ao JPA/Hibernate a busca da entidade Person no banco.
         *
         * O resultado retornado é uma entidade Person contendo os dados persistidos,
         * inclusive campos internos da entidade como a senha.
         *
         * Por isso, antes de enviar a resposta ao cliente, a entidade é convertida
         * para um DTO, que controla quais dados serão expostos pela API.
         *
         * Fluxo:
         *
         * 1. O Controller recebe a requisição HTTP GET para buscar uma pessoa pelo ID.
         *
         * 2. O Controller chama o método findById() do Service, passando o ID recebido.
         *
         * 3. O Service chama repository.findById(id).
         *
         * 4. O Repository utiliza o JPA/Hibernate para buscar os dados no banco.
         *
         * 5. O JPA/Hibernate monta uma entidade Person com os dados encontrados.
         *
         * 6. O Mapper converte a entidade Person em PersonDTO, removendo dados
         *    que não devem ser expostos, como a senha.
         *
         * 7. O Service retorna o DTO para o Controller enviar ao cliente.
         */
        // Converte a entidade JPA recuperada para o DTO de resposta seguro usando o Mapper
        return mapper.toDTO(entity); // envia a entidade para ser convertida em um DTO filtrando dados sensíveis como a senha antes de enviar a resposta final ao cliente
    }

    //método que retorna uma lista do tipo personDto
    public List<PersonDTO> findAll() {

        logger.info("Finding all Persons!");

        // Executa a consulta SELECT * FROM person; e converte cada entidade para PersonDTO
        /*
         *   // web user request -> GET http://localhost:8080/person -> controller -> service ->repository-> banco de dados
         *  1. O Controller recebe a requisição HTTP GET para listar todas as pessoas
         *  2. O Controller chama o método findAll() do Service
         *  3. O Service chama o método findAll() do Repository, que executa a consulta no banco de dados
         *  4. O Repository retorna uma lista de entidades Person para o Service
         *  5. O Service mapeia cada entidade Person
         */

        /*
         * pega a resposta recebida via: banco -> repository -> service -> controller -> web user
         *  e converte cada entidade Person para PersonDTO, filtrando dados sensíveis como a senha, antes de enviar a resposta final ao cliente.
         *  O método .stream() cria um fluxo de dados, .map(mapper::toDTO) aplica a conversão para cada elemento do fluxo, e .toList() coleta os resultados em uma lista final de PersonDTO.
         *       *
         * E interessante notar que a resposta e retornada como a entidade pura
         * esse dados são colocado na esteira (stream) para serem transformados
         * lodo o map(pega esses dados(entidade pura) aplica a função que os  transforma e um novo objeto
         * do tipo personDto e por fim constrói uma lista com os dados da dto e apresenta essa lista ao cliente web
         */
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public PersonDTO create(PersonInsertDTO personDto) {

        logger.info("Creating a new Person!"); // Registra no log que uma nova pessoa será criada.

        /*
         * ============================================================================
         * FLUXO DE CRIAÇÃO DE UMA NOVA PESSOA
         * ============================================================================
         *
         * O cliente envia os dados através da API no formato PersonInsertDTO.
         *
         * O DTO representa os dados recebidos da requisição, mas não é o objeto
         * utilizado pelo JPA/Hibernate para persistência.
         *
         * Por isso, o Mapper é responsável por converter:
         *
         * PersonInsertDTO  --->  Person (Entidade)
         *
         * Durante essa conversão, o Mapper aplica regras necessárias:
         * - Criptografa a senha utilizando BCrypt.
         * - Define a Role padrão CLIENT caso nenhuma seja informada.
         *
         * Depois da conversão, o Repository recebe uma entidade Person e o JPA/Hibernate
         * transforma essa entidade em comandos SQL para salvar no banco de dados.
         *
         * Após salvar, a entidade persistida retorna para o Service.
         *
         * Por segurança, essa entidade não é enviada diretamente ao cliente, pois possui
         * campos internos como senha.
         *
         * Então o Mapper converte novamente:
         *
         * Person (Entidade)  --->  PersonDTO (Resposta)
         *
         * O DTO de resposta contém apenas os dados permitidos para exposição na API.
         *
         * Fluxo completo:
         *
         * Cliente
         *    |
         *    ▼
         * Controller
         *    |
         *    ▼
         * PersonInsertDTO
         *    |
         *    ▼
         * PersonMapper.toEntity()
         *    |
         *    ▼
         * Person (Entidade)
         *    |
         *    ▼
         * Repository.save()
         *    |
         *    ▼
         * Banco de Dados
         *    |
         *    ▼
         * Person salva
         *    |
         *    ▼
         * PersonMapper.toDTO()
         *    |
         *    ▼
         * PersonDTO
         *    |
         *    ▼
         * Resposta HTTP para o cliente
         *
         * ============================================================================
         */


        // Converte o DTO recebido pelo Controller em uma entidade Person pronta para persistência.
        Person person = mapper.toEntity(personDto); // DTO de entrada -> Entidade Person (aplica BCrypt e regras padrão).


        // Envia a entidade para o Repository, que utiliza o JPA/Hibernate para salvar no banco.
        Person savedPerson = repository.save(person); // Entidade Person -> INSERT no banco de dados.


        // Converte a entidade salva em DTO de resposta, ocultando dados sensíveis como senha.
        return mapper.toDTO(savedPerson); // Entidade Person persistida -> PersonDTO enviado ao cliente.
    }

    /*
     * ============================================================================
     * UPDATE - ATUALIZAÇÃO DE UMA PESSOA EXISTENTE
     * ============================================================================
     *
     * O cliente envia uma requisição PUT/PATCH contendo:
     *
     * - ID da pessoa que será atualizada.
     * - Novos valores através do PersonUpdateDTO.
     *
     * O Service primeiro busca a entidade existente no banco.
     *
     * Caso encontre:
     * - O Mapper copia os valores permitidos do DTO para a entidade existente.
     * - O Repository salva a entidade alterada.
     * - O Mapper converte a entidade atualizada em PersonDTO para resposta.
     *
     * Caso não encontre:
     * - O Service lança ResourceNotFoundException.
     * - O ControllerAdvice transforma a exceção em resposta HTTP 404.
     *
     *
     * Fluxo:
     *
     * Cliente
     *    |
     *    ▼
     * Controller
     *    |
     *    ▼
     * PersonService.update()
     *    |
     *    ▼
     * repository.findById(id)
     *    |
     *    ▼
     * Banco de Dados
     *    |
     *    ▼
     * Person (entidade existente)
     *    |
     *    ▼
     * PersonMapper.updateEntityFromDTO()
     *    |
     *    ▼
     * Person modificada
     *    |
     *    ▼
     * repository.save()
     *    |
     *    ▼
     * Banco de Dados (UPDATE)
     *    |
     *    ▼
     * PersonMapper.toDTO()
     *    |
     *    ▼
     * PersonDTO
     *    |
     *    ▼
     * Resposta HTTP para o cliente
     *
     * ============================================================================
     */
    public PersonDTO update(Long id, PersonUpdateDTO dto) {

        logger.info("Updating one Person!"); // Registra no log que uma atualização será realizada.


        // Busca a pessoa existente pelo ID informado.
        // Se não encontrar, lança ResourceNotFoundException e retorna HTTP 404.
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this ID!"));


        // Recebe o DTO de atualização e altera somente os campos permitidos
        // da entidade que foi carregada do banco.
        mapper.updateEntityFromDTO(dto, entity);


        // Salva a entidade modificada.
        // Como a entidade possui ID, o JPA/Hibernate executa um UPDATE no banco.
        Person updatedPerson = repository.save(entity);


        // Converte a entidade atualizada em DTO de resposta,
        // ocultando campos internos que não devem ser enviados ao cliente.
        return mapper.toDTO(updatedPerson);
    }



    /*
     * ============================================================================
     * DELETE
     * ============================================================================
     * Remove uma pessoa pelo ID.
     *
     * Fluxo:
     *
     * Controller
     *      |
     *      ↓
     * PersonService.delete()
     *      |
     *      ↓ (Verifica se existe pelo ID antes de deletar)
     * PersonRepository.delete()
     *      |
     *      ↓
     * Remove da tabela person no Banco de Dados (DELETE)
     *
     * VOID -> não retorna conteúdo
     * ============================================================================
     */
    public void delete(Long id) {

        logger.info("Deleting one Person!");

        // Garante que o registro existe antes de tentar deletar
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this ID!"));

        repository.delete(entity);

        // não retona nada ao cliente
    }
}

    /*
    Controller
        |
        | recebe requisição HTTP
        | valida entrada básica
        |
        ▼
    Service
        |
        | regra de negócio
        | coordena operações
        |
        ├── Mapper
        |       |
        |       └── DTO <-> Entity
        |
        └── Repository
                |
                └── Banco de Dados
     */