/*
 * ==============================================================================
 * Migration: V2__Add_Birth_Date_To_Person.sql
 * Descrição: Adiciona a coluna birth_date na tabela person para suportar
 *            a evolução do contrato na Versão 2 da API (PersonDTOV2).
 * ==============================================================================
 */

/*
 * Passo único: Adiciona a coluna do tipo DATE na tabela person
 */
ALTER TABLE public.person
    ADD COLUMN birth_date DATE;