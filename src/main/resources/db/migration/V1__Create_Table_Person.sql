/*
 * ==============================================================================
 * Migration: V2__Populate_Table_Person.sql
 * Descrição: Cria a estrutura inicial da tabela person (com IF NOT EXISTS).
 * ==============================================================================
 */

/*
 * Passo 1: Criação da tabela person de forma segura (apenas se não existir)
 */
CREATE TABLE IF NOT EXISTS public.person (
                                             id SERIAL PRIMARY KEY,
                                             first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    address VARCHAR(100) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    email VARCHAR(100),
    cpf VARCHAR(14),
    password VARCHAR(255),
    role VARCHAR(20)
    );