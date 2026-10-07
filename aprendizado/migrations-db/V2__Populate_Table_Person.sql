/*
 * ==============================================================================
 * Migration: V2__Populate_Table_Person.sql
 * Descrição: Popula a tabela person com os dados iniciais e ajusta a sequence.
 * ==============================================================================
 */

/*
 * Passo 1: Inserção dos registros iniciais na tabela person
 */
INSERT INTO public.person (id, address, cpf, email, first_name, gender, last_name, password, role)
VALUES (12, 'Smiljan - Croácia', '22233344455', 'nikola.tesla@email.com', 'Nikola', 'Male', 'Tesla',
        '$2a$10$S7c9sSYJxe1TP1JpMFCcxeeeMUQHEyIeEGtnN46J8iuG4E6PPT4RW', 'CLIENT'),
       (14, 'Ulm - Alemanha', '44455566777', 'albert.einstein@email.com', 'Albert', 'Male', 'Einstein',
        '$2a$10$S7c9sSYJxe1TP1JpMFCcxeeeMUQHEyIeEGtnN46J8iuG4E6PPT4RW', 'CLIENT'),
       (15, 'Varsóvia - Polônia', '55566677888', 'marie.curie@email.com', 'Marie', 'Female', 'Curie',
        '$2a$10$S7c9sSYJxe1TP1JpMFCcxeeeMUQHEyIeEGtnN46J8iuG4E6PPT4RW', 'CLIENT'),
       (16, 'Angola - Luanda', '700660020', 'domingos.jovete@email.com', 'Domingos', 'Male', 'Jovete',
        '$2a$10$Q6C6j0hvjdyJQfvoCLKZm.aZ9WDez6pRjSCdoqtvmgrCeWqTvKvsu', 'ADMIN'),
       (17, 'São Paulo - SP - Brasil', '99988877766', 'eline.aline@email.com', 'Aline carvalho', 'Male',
        'Senna da Silva', '$2a$10$7vyIxfJpFkc./TvhwTrd0eldTx6mqPhpHfYcYgJEBNoMGS0cz1vmm', 'CLIENT'),
       (18, 'São Paulo - Brasil', '11122233344', 'ayrton.senna@email.com', 'Ayrton', 'Male', 'Senna',
        '$2a$10$G6ZpKTriewJ/1lcPrTIb3udDwa25cfTj0whVfpV/ZiUkX3XmSwKL.', 'CLIENT'),
       (22, 'Londres - Inglaterra', '33344455666', 'ada.lovelace@email.com', 'Ada', 'Female', 'Lovelace',
        '$2a$10$MVpbmfibQk2oyb8ZhD5Ez.X4Dka0D5YWnefcl2oPvqpEaoebE9lKm', 'CLIENT'),
       (28, 'Palmira - Brasil', '12345678901', 'santos.dumont@email.com', 'Alberto', 'Male', 'Santos Dumont',
        '$2a$10$m.eTkHjoYEYbeFtuviqUMusS4VSrcF1SUMrapRdUOTMjpIVOSAyPi', 'CLIENT');

/*
 * Passo 2: Sincronização da sequence do ID
 * Garante que novos cadastros feitos pela aplicação continuem a partir do ID 28
 */
SELECT pg_catalog.setval('public.person_id_seq', 28, true);