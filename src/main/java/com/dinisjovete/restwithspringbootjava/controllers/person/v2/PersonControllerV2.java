package com.dinisjovete.restwithspringbootjava.controllers.person.v2;

import com.dinisjovete.restwithspringbootjava.config.hateoas.person.v2.PersonHateoasAssemblerV2;
import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonPatchDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.services.person.v2.PersonServiceV2;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*
 * ============================================================================
 * CONTROLLER - PersonControllerV2 (v2)
 * ============================================================================
 *
 * RESPONSABILIDADE
 * ----------------
 * Traduzir HTTP <-> Java. Recebe a requisição, valida o formato da entrada,
 * delega ao Service e devolve a resposta com o status HTTP correto e os links
 * HATEOAS.
 *
 * O Controller NÃO contém regra de negócio: ela mora no PersonServiceV2.
 *
 * A documentação Swagger NÃO mora aqui: ela fica na interface
 * PersonControllerDocsV2, que esta classe implementa (inclusive o @Tag).
 * ============================================================================
 */
@RestController
/*
 * ============================================================================
 * @RequestMapping
 * ============================================================================
 * Define o caminho base (Base URL) deste Controller.
 *
 * Todas as requisições para este Controller começarão com:
 *
 *      http://localhost:8080/person/v2
 *
 * Assim, todos os métodos desta classe herdam esse caminho base.
 * ============================================================================
 */
@RequestMapping("/person/v2")
public class PersonControllerV2 implements PersonControllerDocsV2 { // documentação Swagger na interface

    // =========================================================================
    // Injeção de Dependência (Dependency Injection)
    // =========================================================================
    // 'final' garante que a referência não poderá ser alterada após a
    // construção do objeto, tornando a classe mais segura e imutável.
    // =========================================================================
    private final PersonServiceV2 service;
    private final PersonHateoasAssemblerV2 assemblerV2; // adiciona os links HATEOAS aos DTOs
    private final PagedResourcesAssembler<PersonDTOV2> pagedAssembler; // monta o PagedModel com os links de paginação

    // =========================================================================
    // Construtor utilizado pelo Spring para realizar a Injeção de Dependência.
    //
    // A partir do Spring Boot 3.x, quando existe apenas um construtor,
    // a anotação @Autowired não é mais necessária.
    // =========================================================================
    public PersonControllerV2(
            PersonServiceV2 personService,
            PersonHateoasAssemblerV2 personHateoasAssembler,
            PagedResourcesAssembler<PersonDTOV2> pagedAssembler
    ) {
        this.service = personService;
        this.assemblerV2 = personHateoasAssembler;
        this.pagedAssembler = pagedAssembler;
    }

    /*
     * ============================================================================
     * GET /person/v2/{id}  ->  BUSCAR UMA PESSOA PELO ID
     * ============================================================================
     *
     * Retorna uma pessoa a partir do seu ID, repassando o DTO retornado pelo Service.
     *
     *      GET http://localhost:8080/person/v2/4
     *
     *      /person/v2  -> recurso (Resource)
     *      /4          -> Path Parameter: o valor 4 é recebido no parâmetro "id"
     *
     * O formato da resposta é escolhido pelo cabeçalho Accept:
     *
     *      Accept: application/json
     *      Accept: application/xml
     *      Accept: application/yaml
     *
     * Respostas:
     *
     *      200 OK          pessoa encontrada (corpo: PersonDTOV2 com links)
     *      404 Not Found   ID inexistente
     *      400 Bad Request ID que não é número (ex.: /person/v2/abc)
     * ============================================================================
     */
    // @Override: este método implementa findById(Long) de PersonControllerDocsV2.
    // A assinatura (nome + tipos dos parâmetros) precisa bater; senão o compilador acusa erro.
    @Override
    @GetMapping(
            // Caminho relativo ao @RequestMapping("/person/v2") da classe.
            // "{id}" representa um Path Parameter.
            value = "/{id}",

            // Formatos que o endpoint produz (negociados pelo cabeçalho Accept).
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    /*
                     * ============================================================================
                     * MEDIA TYPE YAML PERSONALIZADO
                     * ============================================================================
                     *
                     * CustomMediaType.APPLICATION_YAML_VALUE representa o valor:
                     *
                     *      application/yaml
                     *
                     * Este MediaType não existe como constante padrão no Spring, diferente de:
                     *
                     *      MediaType.APPLICATION_JSON_VALUE
                     *      MediaType.APPLICATION_XML_VALUE
                     *
                     * Por isso foi criada uma constante personalizada na classe CustomMediaType.
                     *
                     * ----------------------------------------------------------------------------
                     *
                     * Este MediaType funciona em conjunto com o conversor personalizado:
                     *
                     *      YamlJackson2HttpMessageConverter
                     *
                     * Esse conversor utiliza o YAMLMapper do Jackson para transformar objetos Java
                     * em respostas no formato YAML.
                     *
                     * ----------------------------------------------------------------------------
                     *
                     * O conversor permanece como um componente "adormecido" dentro da aplicação.
                     *
                     * Ele não é chamado diretamente por nenhum Controller, Service ou Repository.
                     *
                     * O Spring mantém esse conversor registrado e somente "acorda" ele no momento
                     * em que uma requisição HTTP solicitar uma resposta compatível com YAML.
                     *
                     * Exemplo:
                     *
                     *      GET http://localhost:8080/person/v2/4
                     *
                     *      Header:
                     *
                     *          Accept: application/yaml
                     *
                     *
                     * Nesse momento o fluxo acontece:
                     *
                     *      Cliente solicita YAML
                     *              |
                     *              v
                     *      Spring MVC analisa o Header Accept
                     *              |
                     *              v
                     *      Encontra o YamlJackson2HttpMessageConverter
                     *              |
                     *              v
                     *      YAMLMapper converte o objeto Java
                     *              |
                     *              v
                     *      Resposta YAML enviada ao cliente
                     *
                     * ----------------------------------------------------------------------------
                     *
                     * Enquanto o cliente não solicitar YAML, esse conversor continua "adormecido"
                     * e o Spring utiliza os conversores padrões para JSON ou XML.
                     *
                     * Exemplo:
                     *
                     *      Accept: application/json
                     *
                     *      -> MappingJackson2HttpMessageConverter
                     *
                     *
                     *      Accept: application/xml
                     *
                     *      -> Conversor XML
                     *
                     * ============================================================================
                     */
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTOV2 findById(@PathVariable("id") Long id) {
        /*
         * Fluxo da requisição:
         *
         * Controller
         *      ↓
         * Aciona o método findById() do Service.
         *
         * Service
         *      ↓
         * Aciona o método findById() do Repository.
         *
         * Repository
         *      ↓
         * Consulta o banco de dados utilizando o ID informado.
         *
         * Banco de Dados
         *      ↓
         * Retorna a Entity encontrada.
         *
         * Service
         *      ↓
         * Converte a Entity em PersonDTOV2.
         *
         * Controller
         *      ↓
         * Retorna o PersonDTOV2 como resposta da API.
         */
        PersonDTOV2 person = service.findById(id);

        // O DTO volta enriquecido com os links HATEOAS (ex.: link "self").
        return assemblerV2.addLinks(person);
    }


    /*
     * ============================================================================
     * GET /person/v2  ->  LISTAR PESSOAS COM PAGINAÇÃO (com HATEOAS)
     * ============================================================================
     *
     * Retorna uma PÁGINA de registros de pessoas com suporte completo a HATEOAS
     * (Hypermedia as the Engine of Application State).
     *
     * POR QUE FAZEMOS ISSO?
     * Para não carregar a tabela inteira a cada requisição. O cliente pede só a
     * página que precisa e navega entre elas pelos links dinâmicos (first, prev,
     * self, next, last) que a própria API devolve, além dos links individuais de
     * cada item.
     *
     * Parâmetros (opcionais, vêm na URL):
     *
     *      page  número da página, começando em 0      (padrão: 0)
     *      size  quantidade de itens por página        (padrão: 10, máximo: 100)
     *      sort  campo e direção, ex.: lastName,desc   (padrão: firstName,asc)
     *
     *      GET http://localhost:8080/person/v2
     *      GET http://localhost:8080/person/v2?page=1&size=12&sort=lastName,desc
     *
     * O tamanho máximo (100) é definido no application.properties:
     *
     *      spring.data.web.pageable.max-page-size=100
     *
     * Respostas:
     *
     *      200 OK           sempre. Se não houver ninguém, a página vem vazia (isso não é erro).
     *      400 Bad Request  parâmetros inválidos (ex.: sort por um campo que não existe)
     * ============================================================================
     */
// @Override: implementa findAll(Pageable) de PersonControllerDocsV2.
    @Override
    @GetMapping(
            value = "",

            // Formatos que o endpoint produz (negociados pelo cabeçalho Accept).
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public ResponseEntity<PagedModel<PersonDTOV2>> findAll(
            // Expõe page, size e sort como parâmetros separados no Swagger.
            @ParameterObject
            // Valores padrão quando a URL não informa size ou sort.
            @PageableDefault(
                    page = 1,
                    size = 10,
                    sort = "firstName",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {

        // 1º Passo: Pede ao Service apenas a página solicitada (e não a tabela inteira).
        // Motivo: o corte acontece na query (LIMIT/OFFSET) e o retorno é um Page de DTOs,
        // que já traz os itens e os dados da paginação (total de páginas, total de registros...).
        Page<PersonDTOV2> page = service.findAll(pageable);

        // 2º Passo: Monta o PagedModel e adiciona os links HATEOAS individuais em cada item.
        // Motivo: o PagedResourcesAssembler cria sozinho os links da paginação
        // (first, prev, self, next, last) a partir da URL da requisição, e o
        // 'assemblerV2::addLinks' continua montando os links de cada item (ex.: "self").
        PagedModel<PersonDTOV2> model = pagedAssembler.toModel(page, assemblerV2::addLinks);

        // 3º Passo: Devolve 200 OK com a página, os itens e todos os links.
        return ResponseEntity.ok(model);
    }


    /*
     * ============================================================================
     * POST /person/v2  ->  CRIAR UMA NOVA PESSOA
     * ============================================================================
     *
     * Recebe um PersonInsertDTOV2 no corpo da requisição (@RequestBody), valida
     * com @Valid e devolve o PersonDTOV2 criado com o status 201 (Created).
     *
     * Respostas:
     *
     *      201 Created     pessoa criada (corpo: PersonDTOV2 com links)
     *      400 Bad Request corpo malformado ou que viola as regras de validação
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/person/v2)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "firstName": "Elon",
     *        "lastName": "Musk",
     *        "birthDate": "28/06/1971",
     *        "cpf": "99988877766",
     *        "phone": "+1 512 99999-9999",
     *        "email": "elon.musk@email.com",
     *        "password": "123456",
     *        "address": "Texas - EUA",
     *        "gender": "Male",
     *        "role": "CLIENT"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (POST http://localhost:8080/person/v2)
     * ----------------------------------------------------------------------------
     *
     *      <PersonInsertDTOV2>
     *        <firstName>Elon</firstName>
     *        <lastName>Musk</lastName>
     *        <birthDate>28/06/1971</birthDate>
     *        <cpf>99988877766</cpf>
     *        <phone>+1 512 99999-9999</phone>
     *        <email>elon.musk@email.com</email>
     *        <password>123456</password>
     *        <address>Texas - EUA</address>
     *        <gender>Male</gender>
     *        <role>CLIENT</role>
     *      </PersonInsertDTOV2>
     *
     * O DTO de entrada não possui "id": quem gera o identificador é o banco.
     * ============================================================================
     */
    // @Override: implementa create(PersonInsertDTOV2) de PersonControllerDocsV2.
    @Override
    @PostMapping(
            value = "",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
                    /*
                     * CustomMediaType.APPLICATION_YAML_VALUE é uma constante definida na classe CustomMediaType
                     * que representa o Media Type "application/yaml". Isso permite que o Spring
                     * reconheça este conversor como capaz de produzir respostas nesse formato.
                     */
            }
    )
    public ResponseEntity<PersonDTOV2> create(@Valid @RequestBody PersonInsertDTOV2 personDTO) {
        PersonDTOV2 createdPerson = service.create(personDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assemblerV2.addLinks(createdPerson)); // devolve o DTO criado, já com links HATEOAS
    }


    /*
     * ============================================================================
     * PUT /person/v2/{id}  ->  ATUALIZAR UMA PESSOA
     * ============================================================================
     *
     *      PUT http://localhost:8080/person/v2/1
     *
     * O ID vem pela URL (@PathVariable) e os novos dados vêm pelo corpo da
     * requisição (@RequestBody), convertidos em um PersonUpdateDTOV2 e validados
     * com @Valid.
     *
     * Respostas:
     *
     *      200 OK          pessoa atualizada (corpo: PersonDTOV2 com links)
     *      400 Bad Request corpo malformado, inválido ou ID que não é número
     *      404 Not Found   ID inexistente
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (PUT http://localhost:8080/person/v2/1)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "firstName": "Ayrton",
     *        "lastName": "Senna da Silva",
     *        "address": "São Paulo - SP - Brasil",
     *        "gender": "Male",
     *        "role": "ADMIN"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (PUT http://localhost:8080/person/v2/1)
     * ----------------------------------------------------------------------------
     *
     *      <PersonUpdateDTOV2>
     *        <firstName>Ayrton</firstName>
     *        <lastName>Senna da Silva</lastName>
     *        <address>São Paulo - SP - Brasil</address>
     *        <gender>Male</gender>
     *        <role>ADMIN</role>
     *      </PersonUpdateDTOV2>
     * ============================================================================
     */
    // @Override: implementa update(Long, PersonUpdateDTOV2) de PersonControllerDocsV2.
    @Override
    @PutMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTOV2 update(
            @PathVariable("id") Long id,
            @Valid @RequestBody PersonUpdateDTOV2 personUpdate
    ) {
        return assemblerV2.addLinks(
                service.update(id, personUpdate)
        );
    }


    /*
     * ============================================================================
     * DELETE /person/v2/{id}  ->  REMOVER UMA PESSOA
     * ============================================================================
     *
     *      DELETE http://localhost:8080/person/v2/{id}
     *
     * O ID da pessoa a remover vem pela URL.
     *
     * Respostas:
     *
     *      204 No Content  removida com sucesso (sem corpo na resposta)
     *      404 Not Found   ID inexistente
     *      400 Bad Request ID que não é número
     *
     * O status 204 é o correto para uma operação concluída que não tem nada a
     * devolver. É o ResponseEntity.noContent() que define esse status e garante
     * o corpo vazio; o tipo ResponseEntity<?> apenas indica que não há corpo.
     * ============================================================================
     */
    // @Override: implementa delete(Long) de PersonControllerDocsV2.
    @Override
    @DeleteMapping(
            value = "/{id}"
    )
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204, sem conteúdo no corpo
    }

    /*
       ============================================================================
       - PATCH /person/v2/{id}  ->  ATUALIZAÇÃO PARCIAL DE UMA PESSOA
       ============================================================================
        *
        *      PATCH http://localhost:8080/person/v2/1
        *
        * O ID vem pela URL (@PathVariable) e apenas os campos que precisam ser
        * alterados vêm no corpo da requisição (@RequestBody). Campos omitidos
        * permanecem inalterados no banco de dados.
        *
        * Respostas:
        *      200 OK          pessoa atualizada parcialmente (corpo: PersonDTOV2 com links)
        *      400 Bad Request corpo malformado ou ID que não é número
        *      404 Not Found   ID inexistente
        *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
        *
        * ----------------------------------------------------------------------------
        * EXEMPLO DE JSON PARA O PATCH (Atualizar apenas o primeiro nome e o telefone)
        * ----------------------------------------------------------------------------
        *      {
        *        "firstName": "Ayrton",
        *        "phone": "+55 11 98888-8888"
        *      }
       ============================================================================
        */
    // @Override: implementa o método patch correspondente na interface de documentação (PersonControllerDocsV2).
    @Override
    @PatchMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTOV2 patch(
            @PathVariable("id") Long id,
            @RequestBody PersonPatchDTOV2 personPatch // DTO contendo apenas os campos opcionais/parciais
    ) {
        /*
         * Fluxo da requisição PATCH:
         * 1. Recebe o ID da URL e o payload parcial com os dados a alterar.
         * 2. Delega para o Service realizar a busca, identificar quais campos foram preenchidos (não nulos)
         *    e aplicar as alterações de forma seletiva na entidade.
         * 3. Salva no banco de dados.
         * 4. Converte em DTO, adiciona os links HATEOAS via assembler e retorna 200 OK.
         */
        return assemblerV2.addLinks(
                service.patch(id, personPatch)
        );
    }

}

//Swagger UI: http://localhost:8080/swagger-ui/index.html
//YAML da documentação: http://localhost:8080/v3/api-docs.yaml

/*
MOdelo de criação
{
  "firstName": "Domingos",
  "lastName": "Jovete",
  "birthDate": "28/06/1971",
  "cpf": "99988877766",
  "phone": "+1 512 99999-9999",
  "email": "beckymel27@gmail.com",
  "password": "Ag123456@",
  "address": "Texas - EUA",
  "gender": "Male",
  "role": "CLIENT"
}
 */

/*
 * Exemplo de requisição paginada:
 *
 * GET /person/v2?page=0&size=12&sort=firstName,asc
 *  http://localhost:8080/person/v2?page=0&size=10&sort=lastName,asc
 *
 * Query params:
 * - page: número da página, começando em 0 (a primeira página é 0).
 * - size: quantidade de pessoas por página.
 * - sort: campo usado para ordenar e direção da ordenação.
 *         Neste exemplo, ordena por firstName em ordem crescente.
 *
 * O Spring reúne esses parâmetros no objeto Pageable. O controller o envia
 * ao service, que consulta o repositório e retorna somente a página solicitada.
 * O PagedResourcesAssembler transforma o resultado em um PagedModel com os
 * dados da página e os links HATEOAS de navegação.
 */