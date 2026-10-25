package com.dinisjovete.restwithspringbootjava.services.book;


import com.dinisjovete.restwithspringbootjava.data.dto.book.BookDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.book.Book;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.book.BookMapper_MapStruct;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import com.dinisjovete.restwithspringbootjava.repositories.book.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class BookService {


    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger = LoggerFactory.getLogger(BookService.class.getName());

    // Injeção de Dependência do Repositório JPA para acesso ao Banco de Dados (tabela books)
    private final BookRepository repository;

    /*
     * O livro possui um autor, que é uma entidade Person.
     *
     * Por isso o BookService também precisa do PersonRepository: ao criar um livro,
     * o cliente envia apenas o authorId, e é este repositório que busca a Person
     * correspondente no banco de dados para ser vinculada ao livro.
     */
    private final PersonRepository personRepository;

    /*
     * ============================================================================
     * ARQUITETURA DE MÁSCARAS E CONVERSÃO (ESTRATÉGIAS DE MAPPER)
     * ============================================================================
     *
     * O projeto explora e compara três abordagens distintas de mapeamento entre
     * Entidades e DTOs, mantendo o ecossistema flexível para estudo comparativo:
     *
     * 1. Mapper Manual (`BookMapper`):
     *    - Implementação nativa própria, controlada linha a linha.
     *    - Total transparência, sem mágica ou dependências de bibliotecas de terceiros.
     *
     * 2. Dozer (`ObjectMapper`):
     *    - Abordagem baseada em reflexão em tempo de execução (Reflection).
     *    - Conveniente pela cópia automática por nome de propriedades, porém
     *      com menor performance e alto custo computacional em larga escala.
     *
     * 3. MapStruct (`BookMapper_MapStruct`):
     *    - Abordagem moderna baseada em anotações e processamento em tempo de compilação.
     *    - Gera código Java puro (bytecode otimizado), unindo a velocidade do mapeamento
     *      manual com a produtividade da automação.
     *
     * ----------------------------------------------------------------------------
     * CONFIGURAÇÃO ATIVA NA CAMADA DE SERVIÇO:
     * ----------------------------------------------------------------------------
     */

    // Injeção de Dependência do Mapper ativo para centralizar as conversões da API
    // private final BookMapper mapper01;  // 1ª Alternativa: Mapper Manual (Foco em controle total)
    // private final ObjectMapper mapper02;     // 2ª Alternativa: Dozer Mapper (Foco em reflexão/dinamismo)
    private final BookMapper_MapStruct mapper03; // 3ª Alternativa: MapStruct (Foco em performance em tempo de compilação)


    @Autowired   // injetando as dependências no construtor
    public BookService(BookMapper_MapStruct mapper03, BookRepository repository, PersonRepository personRepository) {
        this.mapper03 = mapper03;
        this.repository = repository;
        this.personRepository = personRepository;
    }

    public BookDTO findById(Long id) {

        logger.info("Finding one book!");

        /*
         * ============================================================================
         * FLUXO COM BANCO DE DADOS (JPA REPOSITORY)
         * ============================================================================
         * O Service solicita ao Repository que busque o livro no banco de dados
         * tendo como referência o ID enviado pelo Controller.
         *
         * Tratamento de Ausência com Optional (.orElseThrow):
         * Substitui a condicional 'if' tradicional de forma elegante e funcional:
         *
         *   [Método Tradicional com IF]
         *   Optional<Book> optional = repository.findById(id);
         *   if (!optional.isPresent()) {
         *       throw new ResourceNotFoundException("No record found for this ID!");
         *   }
         *   Book entity = optional.get();
         *
         * Caso não encontre o registro, o .orElseThrow lança a nossa ResourceNotFoundException
         * (que é capturada e tratada globalmente pelo ControllerAdvice).
         *
         * Fluxo de Camadas:
         * BookController -> BookService -> BookRepository -> Banco de Dados (Tabela books)
         * ============================================================================
         */
        Book entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID não existente na base de dados!"));
        /*
         * O Repository solicita ao JPA/Hibernate a busca da entidade Book no banco.
         *
         * O resultado retornado é uma entidade Book contendo os dados persistidos,
         * inclusive o autor, que é uma entidade Person com campos internos como a senha.
         *
         * Por isso, antes de enviar a resposta ao cliente, a entidade é convertida
         * para um DTO, que controla quais dados serão expostos pela API.
         *
         * Fluxo:
         *
         * 1. O Controller recebe a requisição HTTP GET para buscar um livro pelo ID.
         *
         * 2. O Controller chama o método findById() do Service, passando o ID recebido.
         *
         * 3. O Service chama repository.findById(id).
         *
         * 4. O Repository utiliza o JPA/Hibernate para buscar os dados no banco.
         *
         * 5. O JPA/Hibernate monta uma entidade Book com os dados encontrados.
         *
         * 6. O Mapper converte a entidade Book em BookDTO, removendo dados
         *    que não devem ser expostos, como a senha do autor.
         *
         * 7. O Service retorna o DTO para o Controller enviar ao cliente.
         */
        // Converte a entidade JPA recuperada para o DTO de resposta seguro usando o Mapper
        return mapper03.mapEntityToDto(entity); // envia a entidade para ser convertida em um DTO filtrando dados sensíveis (como a senha do autor) antes de enviar a resposta final ao cliente
    }

    //método que retorna uma lista do tipo BookDTO
    //como vai vai trabalhar com hateoas vamos trocar para collectionModel lista do próprio Hateoas
    public List<BookDTO> findAll() {

        logger.info("Finding all books!");

        // Executa a consulta SELECT * FROM books; e converte cada entidade para BookDTO
        /*
         *   // web user request -> GET http://localhost:8080/book -> controller -> service ->repository-> banco de dados
         *  1. O Controller recebe a requisição HTTP GET para listar todos os livros
         *  2. O Controller chama o método findAll() do Service
         *  3. O Service chama o método findAll() do Repository, que executa a consulta no banco de dados
         *  4. O Repository retorna uma lista de entidades Book para o Service
         *  5. O Service mapeia cada entidade Book
         */

        /*
         * pega a resposta recebida via: banco -> repository -> service -> controller -> web user
         *  e converte cada entidade Book para BookDTO, filtrando dados sensíveis (como a senha do autor), antes de enviar a resposta final ao cliente.
         *  O método .stream() cria um fluxo de dados, .map(mapper::mapEntityToDto) aplica a conversão para cada elemento do fluxo, e .toList() coleta os resultados em uma lista final de BookDTO.
         *       *
         * E interessante notar que a resposta e retornada como a entidade pura
         * esse dados são colocado na esteira (stream) para serem transformados
         * logo o map(pega esses dados(entidade pura) aplica a função que os  transforma e um novo objeto
         * do tipo BookDTO e por fim constrói uma lista com os dados da dto e apresenta essa lista ao cliente web
         */
        return repository.findAll().stream()
                .map(mapper03::mapEntityToDto) // converte cada entidade Book em BookDTO
                .toList();
    }

    public BookDTO create(BookInsertDTO bookDto) {

        logger.info("Creating a new book!"); // Registra no log que um novo livro será criado.

        /*
         * ============================================================================
         * FLUXO DE CRIAÇÃO DE UM NOVO LIVRO
         * ============================================================================
         *
         * O cliente envia os dados através da API no formato BookInsertDTO.
         *
         * O DTO representa os dados recebidos da requisição, mas não é o objeto
         * utilizado pelo JPA/Hibernate para persistência.
         *
         * Por isso, o Mapper é responsável por converter:
         *
         * BookInsertDTO  --->  Book (Entidade)
         *
         * O DTO traz apenas o authorId, e o Mapper ignora o campo author da entidade.
         * Por isso, o Service aplica a regra necessária de relacionamento:
         * - Busca a Person autora no banco pelo authorId.
         * - Lança ResourceNotFoundException (HTTP 404) caso o autor não exista.
         * - Vincula a Person encontrada ao livro.
         *
         * Depois da conversão, o Repository recebe uma entidade Book e o JPA/Hibernate
         * transforma essa entidade em comandos SQL para salvar no banco de dados.
         *
         * Após salvar, a entidade persistida retorna para o Service.
         *
         * Por segurança, essa entidade não é enviada diretamente ao cliente, pois o autor
         * é uma entidade Person que possui campos internos como senha.
         *
         * Então o Mapper converte novamente:
         *
         * Book (Entidade)  --->  BookDTO (Resposta)
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
         * BookInsertDTO
         *    |
         *    ▼
         * BookMapper.mapDtoToEntity()
         *    |
         *    ▼
         * Book (Entidade, ainda sem autor)
         *    |
         *    ▼ (Busca a Person pelo authorId e vincula ao livro no Service)
         *    ▼
         * Repository.save()
         *    |
         *    ▼
         * Banco de Dados
         *    |
         *    ▼
         * Book salvo
         *    |
         *    ▼
         * BookMapper.mapEntityToDto()
         *    |
         *    ▼
         * BookDTO
         *    |
         *    ▼
         * Resposta HTTP para o cliente
         *
         * ============================================================================
         */


        // Converte o DTO recebido pelo Controller em uma entidade Book pura através do Mapper.
        Book book = mapper03.mapDtoToEntity(bookDto);

        // Busca a pessoa autora pelo authorId informado.
        // Se não encontrar, lança ResourceNotFoundException e retorna HTTP 404.
        Person author = personRepository.findById(bookDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado para este ID!"));

        // Vincula o autor ao livro (a coluna author_id é obrigatória na tabela books).
        book.setAuthor(author);


        // Envia a entidade tratada para o Repository, que utiliza o JPA/Hibernate para salvar no banco.
        Book savedBook = repository.save(book); // Entidade Book -> INSERT no banco de dados.


        // Converte a entidade salva em DTO de resposta, ocultando dados sensíveis (como a senha do autor).
        // a entidade criada retorna a dto de retorno para o cliente
        return mapper03.mapEntityToDto(savedBook); // Entidade Book persistida -> BookDTO enviado ao cliente.
    }

    /*
     * ============================================================================
     * UPDATE - ATUALIZAÇÃO DE UM LIVRO EXISTENTE
     * ============================================================================
     *
     * O cliente envia uma requisição PUT/PATCH contendo:
     *
     * - ID do livro que será atualizado.
     * - Novos valores através do BookUpdateDTO (title, price e launchDate).
     *
     * O Service primeiro busca a entidade existente no banco.
     *
     * Caso encontre:
     * - O Mapper copia os valores permitidos do DTO para a entidade existente.
     * - O autor e o id permanecem intactos.
     * - O Repository salva a entidade alterada.
     * - O Mapper converte a entidade atualizada em BookDTO para resposta.
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
     * BookService.update()
     *    |
     *    ▼
     * repository.findById(id)
     *    |
     *    ▼
     * Banco de Dados
     *    |
     *    ▼
     * Book (entidade existente)
     *    |
     *    ▼
     * BookMapper.updateEntityFromDTO()
     *    |
     *    ▼
     * Book modificado
     *    |
     *    ▼
     * repository.save()
     *    |
     *    ▼
     * Banco de Dados (UPDATE)
     *    |
     *    ▼
     * BookMapper.mapEntityToDto()
     *    |
     *    ▼
     * BookDTO
     *    |
     *    ▼
     * Resposta HTTP para o cliente
     *
     * ============================================================================
     */
    public BookDTO update(Long id, BookUpdateDTO dto) {

        logger.info("Updating one Book!"); // Registra no log que uma atualização será realizada.


        // Busca o livro existente pelo ID informado.
        // Se não encontrar, lança ResourceNotFoundException e retorna HTTP 404.
        Book entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this ID!"));


        // Recebe o DTO de atualização e altera somente os campos permitidos
        // da entidade que foi carregada do banco.
        mapper03.updateEntityFromDTO(dto, entity);


        // Salva a entidade modificada.
        // Como a entidade possui ID, o JPA/Hibernate executa um UPDATE no banco.
        Book updatedBook = repository.save(entity);


        // Converte a entidade atualizada em DTO de resposta,
        // ocultando campos internos que não devem ser enviados ao cliente.
        return mapper03.mapEntityToDto(updatedBook);
    }


    /*
     * ============================================================================
     * DELETE
     * ============================================================================
     * Remove um livro pelo ID.
     *
     * A exclusão de um livro não remove a pessoa autora (ciclo de vida independente).
     *
     * Fluxo:
     *
     * Controller
     *      |
     *      ↓
     * BookService.delete()
     *      |
     *      ↓ (Verifica se existe pelo ID antes de deletar)
     * BookRepository.delete()
     *      |
     *      ↓
     * Remove da tabela books no Banco de Dados (DELETE)
     *
     * VOID -> não retorna conteúdo
     * ============================================================================
     */
    public void delete(Long id) {

        logger.info("Deleting one Book!");

        // Garante que o registro existe antes de tentar deletar
        Book entity = repository.findById(id)
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