/*
 * ==============================================================================
 * Migration: V4__Add_Phone_To_Person.sql
 * Descrição: Adiciona a coluna phone na tabela person para armazenar
 *            o telefone da pessoa.
 *
 * Observação:
 *     - A coluna é criada permitindo valores NULL, pois o recurso
 *       ainda está em desenvolvimento.
 * ==============================================================================
 */

/*
 * Passo único: Adiciona a coluna phone na tabela person
 */
ALTER TABLE public.person
    ADD COLUMN phone VARCHAR(20);