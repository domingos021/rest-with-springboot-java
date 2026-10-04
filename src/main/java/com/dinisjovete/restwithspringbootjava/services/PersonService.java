package com.dinisjovete.restwithspringbootjava.services;


import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.dozer.ObjectMpper;
import com.dinisjovete.restwithspringbootjava.mappers.mapper_manual.PersonMapper;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.PersonMapper_MapStruct;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class PersonService {

    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    // Injeção de Dependência do Repositório JPA para acesso ao Banco de Dados
    private final PersonRepository repository;

    /*
     * ============================================================================
     * ARQUITETURA DE MÁSCARAS E CONVERSÃO (ESTRATÉGIAS DE MAPPER)
     * ============================================================================
     *
     * O projeto explora e compara três abordagens distintas de mapeamento entre
     * Entidades e DTOs, mantendo o ecossistema flexível para estudo comparativo:
     *
     * 1. Mapper Manual (`PersonMapper`):
     *    - Implementação nativa própria, controlada linha a linha.
     *    - Total transparência, sem mágica ou dependências de bibliotecas de terceiros.
     *
     * 2. Dozer (`ObjectMapper`):
     *    - Abordagem baseada em reflexão em tempo de execução (Reflection).
     *    - Conveniente pela cópia automática por nome de propriedades, porém
     *      com menor performance e alto custo computacional em larga escala.
     *
     * 3. MapStruct (`PersonMapper_MapStruct`):
     *    - Abordagem moderna baseada em anotações e processamento em tempo de compilação.
     *    - Gera código Java puro (bytecode otimizado), unindo a velocidade do mapeamento
     *      manual com a produtividade da automação.
     *
     * ----------------------------------------------------------------------------
     * CONFIGURAÇÃO ATIVA NA CAMADA DE SERVIÇO:
     * ----------------------------------------------------------------------------
     */

    // Injeção de Dependência do Mapper ativo para centralizar as conversões da API
   // private final PersonMapper mapper01;  // 1ª Alternativa: Mapper Manual (Foco em controle total)
    // private final ObjectMapper mapper02;     // 2ª Alternativa: Dozer Mapper (Foco em reflexão/dinamismo)
     private final PersonMapper_MapStruct mapper03; // 3ª Alternativa: MapStruct (Foco em performance em tempo de compilação)

    // Injeção de Dependência do PasswordEncoder para tratar a regra de negócio de segurança da senha
    private final PasswordEncoder passwordEncoder;

    @Autowired   // injetando as dependências no construtor
    public PersonService(PersonMapper_MapStruct mapper03, PasswordEncoder passwordEncoder, PersonRepository repository) {
        this.mapper03 = mapper03;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
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
                .orElseThrow(() -> new ResourceNotFoundException("ID não existente na base de dados!"));
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
        return mapper03.mapEntityToDto(entity); // envia a entidade para ser convertida em um DTO filtrando dados sensíveis como a senha antes de enviar a resposta final ao cliente
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
                .map(mapper03::mapEntityToDto) // converte cada entidade Person em PersonDTO
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
         * Durante essa conversão, o Service aplica regras necessárias de segurança:
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
         *    ▼ (Aplica PasswordEncoder e Role padrão no Service)
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


        // Converte o DTO recebido pelo Controller em uma entidade Person pura através do Mapper.
        Person person = mapper03.mapDtoToEntity(personDto);

        // Aplica a regra de negócio/segurança de criptografia da senha via BCrypt (caso informada)
        if (personDto.getPassword() != null) {
            person.setPassword(passwordEncoder.encode(personDto.getPassword()));
        }

        // Garante a atribuição da Role (padrão CLIENT se vier nula)
        if (person.getRole() == null) {
            person.setRole(PersonRole.CLIENT);
        }


        // Envia a entidade tratada para o Repository, que utiliza o JPA/Hibernate para salvar no banco.
        Person savedPerson = repository.save(person); // Entidade Person -> INSERT no banco de dados.


        // Converte a entidade salva em DTO de resposta, ocultando dados sensíveis como senha.
        // a entidade criada retorna a dto de retorno para o cliente
        return mapper03.mapEntityToDto(savedPerson); // Entidade Person persistida -> PersonDTO enviado ao cliente.
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
        mapper03.updateEntityFromDTO(dto, entity);


        // Salva a entidade modificada.
        // Como a entidade possui ID, o JPA/Hibernate executa um UPDATE no banco.
        Person updatedPerson = repository.save(entity);


        // Converte a entidade atualizada em DTO de resposta,
        // ocultando campos internos que não devem ser enviados ao cliente.
        return mapper03.mapEntityToDto(updatedPerson);
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