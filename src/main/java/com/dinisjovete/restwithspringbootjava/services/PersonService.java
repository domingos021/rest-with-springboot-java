package com.dinisjovete.restwithspringbootjava.services;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
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

    @Autowired   // injetando a dependência repository no construtor
    public PersonService(PersonRepository repository) {
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
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this ID!"));

        // Converte a entidade JPA recuperada para o DTO de resposta seguro
        return new PersonDTO(entity);
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
         *  O método .stream() cria um fluxo de dados, .map(PersonDTO::new) aplica a conversão para cada elemento do fluxo, e .toList() coleta os resultados em uma lista final de PersonDTO.
         *       *
         * E interessante notar que a resposta e retornada como a entidade pura
         * esse dados são colocado na esteira (stream) para serem transformados
         * lodo o map(pega esses dados(entidade pura) aplica a função que os  transforma e um novo objeto
         * do tipo personDto e por fim constrói uma lista com os dados da dto e apresenta essa lista ao cliente web
         */
        return repository.findAll().stream()
                .map(PersonDTO::new)
                .toList();
    }

    public PersonDTO create(PersonInsertDTO personDto) {

        logger.info("Creating a new Person!");

        // 1. Instancia a Entidade JPA
        Person person = new Person();

        // 2. Mapeia os dados do DTO de entrada para a Entidade
        person.setFirstName(personDto.getFirstName());
        person.setLastName(personDto.getLastName());
        person.setCpf(personDto.getCpf());
        person.setEmail(personDto.getEmail());
        person.setPassword(personDto.getPassword()); // (Futuramente aqui passará pelo BCrypt)
        person.setAddress(personDto.getAddress());
        person.setGender(personDto.getGender());

        // Se o DTO enviar um role, define ele; senão, define o padrão CLIENT
        person.setRole(personDto.getRole() != null ? personDto.getRole() : PersonRole.CLIENT);

        // 3. Salva a Entidade no banco de dados (INSERT)
        Person savedPerson = repository.save(person);

        // 4. Retorna convertido para PersonDTO (filtrando dados internos e a senha)
        return new PersonDTO(savedPerson);
    }


    /*
     * ============================================================================
     * UPDATE
     * ============================================================================
     * Atualiza os dados de uma pessoa existente.
     *
     * Fluxo:
     *
     * Controller
     *      |
     *      ↓
     * PersonService.update()
     *      |
     *      ↓ (Busca se existe, atualiza os campos e salva)
     * PersonRepository.save()
     *      |
     *      ↓
     * Banco de Dados (UPDATE)
     * ============================================================================
     */
    public PersonDTO update(Long id, PersonUpdateDTO dto) {

        logger.info("Updating one Person!");

        // Primeiro verifica se o registro existe no banco. Se não existir, lança 404.
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this ID!"));

        // Atualiza os dados da entidade existente com os novos valores enviados pelo DTO de atualização
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAddress(dto.getAddress());
        entity.setGender(dto.getGender());

        // Salva as alterações no banco de dados (UPDATE)
        Person updatedPerson = repository.save(entity);

        return new PersonDTO(updatedPerson);
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
    }
}