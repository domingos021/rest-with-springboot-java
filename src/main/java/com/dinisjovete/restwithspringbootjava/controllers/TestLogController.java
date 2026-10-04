package com.dinisjovete.restwithspringbootjava.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TestLogController {

    private final Logger logger = LoggerFactory.getLogger(TestLogController.class);

    @GetMapping("/test")
    public String testLog() {
        // imprimindo os níveis de logs
        // Imprimindo os diferentes níveis de logs
        logger.trace("This is a test TRACE log");
        logger.debug("This is a test DEBUG log");
        logger.info("This is a test INFO log");
        logger.warn("This is a test WARN log");
        logger.error("This is a test ERROR log");
        return "Logs gerados com sucesso!";
    }
}
