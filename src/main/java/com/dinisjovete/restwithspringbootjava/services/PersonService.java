package com.dinisjovete.restwithspringbootjava.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class PersonService {
    // Controla IDs sequenciais de forma segura entre múltiplas threads (simulando um banco de dados)
    private final AtomicLong counter = new AtomicLong();
    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger = Logger.getLogger(PersonService.class.getName());
}







