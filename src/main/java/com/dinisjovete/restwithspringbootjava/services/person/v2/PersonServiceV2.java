package com.dinisjovete.restwithspringbootjava.services.person.v2;

import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;

import com.dinisjovete.restwithspringbootjava.exception.ResourceConflictException;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.person.v2.PersonMapper_MapStructV2;

import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import com.dinisjovete.restwithspringbootjava.repositories.book.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * ============================================================================
 * PersonServiceV2
 * ----------------------------------------------------------------------------
 * Regras de negócio do CADASTRO de pessoas.
 *
 * Person não carrega livros, pedidos nem produtos: esses dados são tratados
 * pelos serviços e DTOs de cada contexto (Book, Order, Product).
 *
 * Fluxo de camadas:
 *
 *   Controller  -> recebe a requisição HTTP e valida a entrada
 *       |
 *   Service     -> aplica regras de negócio e coordena as operações
 *       |--> Mapper     (DTO <-> Entity)
 *       |--> Repository (acesso ao banco de dados)
 * ============================================================================
 */
@Service
public class PersonServiceV2 {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceV2.class.getName());

    private static final String NOT_FOUND_MESSAGE = "Nenhum registro encontrado para este ID!";

    private final PersonRepository repository;
    private final BookRepository bookRepository; // usado para proteger a exclusão de autores
    private final PersonMapper_MapStructV2 mapper; // MapStruct: conversão gerada em tempo de compilação
    private final PasswordEncoder passwordEncoder;

    // Construtor único: o Spring injeta as dependências sem precisar de @Autowired
    public PersonServiceV2(PersonRepository repository,
                           BookRepository bookRepository,
                           PersonMapper_MapStructV2 mapper,
                           PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.bookRepository = bookRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * FIND BY ID
     * Busca a pessoa pelo ID. Se não existir, lança ResourceNotFoundException
     * (tratada pelo ControllerAdvice como HTTP 404).
     * A entidade é convertida em DTO para não expor dados internos, como a senha.
     */
    @Transactional(readOnly = true)
    public PersonDTOV2 findById(Long id) {

        logger.info("Finding one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        return mapper.mapEntityToDto(entity);
    }

    /*
     * FIND ALL
     * Lista todas as pessoas convertendo cada entidade em DTO de resposta.
     */
    @Transactional(readOnly = true)
    public List<PersonDTOV2> findAll() {

        logger.info("Finding all Persons!");

        return mapper.mapEntityListToDtoList(repository.findAll());
    }

    /*
     * CREATE
     * PersonInsertDTOV2 -> Person -> (regras) -> save -> PersonDTOV2
     *
     * Regras aplicadas:
     * - E-mail e CPF não podem estar cadastrados (HTTP 409).
     * - A senha é criptografada com BCrypt antes de salvar.
     * - Role padrão CLIENT quando nenhuma for informada.
     *
     * As constraints unique do banco continuam como última barreira para
     * requisições simultâneas.
     */
    @Transactional
    public PersonDTOV2 create(PersonInsertDTOV2 personDto) {

        logger.info("Creating a new Person!");

        if (repository.existsByEmail(personDto.getEmail())) {
            throw new ResourceConflictException("E-mail já cadastrado!");
        }
        if (repository.existsByCpf(personDto.getCpf())) {
            throw new ResourceConflictException("CPF já cadastrado!");
        }

        Person person = mapper.mapDtoToEntity(personDto);

        // A senha é obrigatória (@NotBlank no PersonInsertDTOV2) e nunca é salva em texto puro
        person.setPassword(passwordEncoder.encode(personDto.getPassword()));

        if (person.getRole() == null) {
            person.setRole(PersonRole.CLIENT);
        }

        Person savedPerson = repository.save(person);

        return mapper.mapEntityToDto(savedPerson);
    }

    /*
     * UPDATE
     * Busca a entidade existente, copia somente os campos permitidos do
     * PersonUpdateDTOV2 (id, cpf, email, senha e role permanecem intactos)
     * e devolve o DTO atualizado.
     *
     * Como a entidade é gerenciada dentro da transação, o Hibernate grava
     * a alteração no commit; o save() mantém a intenção explícita.
     */
    @Transactional
    public PersonDTOV2 update(Long id, PersonUpdateDTOV2 dto) {

        logger.info("Updating one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        mapper.updateEntityFromDTO(dto, entity);

        Person updatedPerson = repository.save(entity);

        return mapper.mapEntityToDto(updatedPerson);
    }

    /*
     * DELETE
     * Uma pessoa que é autora de livros não pode ser removida: o livro exige
     * um autor (author_id NOT NULL) e as vidas das entidades são independentes,
     * sem exclusão em cascata. Nesse caso retorna HTTP 409.
     *
     * Quando a entidade Order existir, a mesma checagem vale para pedidos:
     * orderRepository.existsByPersonId(id).
     */
    @Transactional
    public void delete(Long id) {

        logger.info("Deleting one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        if (bookRepository.existsByAuthorId(id)) {
            throw new ResourceConflictException("Pessoa possui livros cadastrados e não pode ser removida!");
        }

        repository.delete(entity);
    }
}

/*
 * ============================================================================
 * POR QUE USAMOS @Transactional
 * ----------------------------------------------------------------------------
 * Uma transação agrupa várias operações no banco em um bloco único, do tipo
 * "tudo ou nada":
 *
 *   - Se o método termina sem erro  -> COMMIT   (todas as alterações são gravadas)
 *   - Se lança uma RuntimeException -> ROLLBACK (todas as alterações são desfeitas)
 *
 * Isso evita dados pela metade. Exemplo: criar um pedido e baixar o estoque;
 * se a baixa falhar, o pedido também é desfeito.
 *
 * Também mantém a entidade "gerenciada" pelo Hibernate durante o método:
 * alterações feitas nela são detectadas e gravadas automaticamente no commit
 * (dirty checking), e as checagens feitas antes de gravar (como e-mail
 * duplicado ou autor com livros) rodam no mesmo contexto da gravação.
 *
 * Onde é aplicada:
 *   - Métodos que gravam (create, update, delete) -> @Transactional
 *   - Métodos que só leem (findById, findAll)     -> @Transactional(readOnly = true)
 *     Avisa ao Hibernate que nada será alterado, evitando trabalho
 *     desnecessário e protegendo contra gravações acidentais.
 *
 * Fica no Service porque é onde as regras de negócio e as operações no banco
 * se juntam. Controller e Mapper não acessam o banco, e o Repository já é
 * transacional nas operações do JpaRepository.
 *
 * Observação: só métodos públicos chamados de FORA da classe são interceptados.
 * Chamar outro método da mesma classe (this.metodo()) ignora a anotação.
 * ============================================================================
 */