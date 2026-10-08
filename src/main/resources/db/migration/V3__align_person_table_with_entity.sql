-- =====================================================================
-- V3: Alinhamento estrutural da tabela person com a Entidade Java JPA
-- =====================================================================

-- 1. Ajustar os tamanhos dos campos (VARCHAR) conforme as anotações @Column(length = ...)
ALTER TABLE public.person ALTER COLUMN first_name TYPE VARCHAR(70);
ALTER TABLE public.person ALTER COLUMN last_name TYPE VARCHAR(70);
ALTER TABLE public.person ALTER COLUMN cpf TYPE VARCHAR(14);
ALTER TABLE public.person ALTER COLUMN email TYPE VARCHAR(100);
ALTER TABLE public.person ALTER COLUMN password TYPE VARCHAR(100);
ALTER TABLE public.person ALTER COLUMN address TYPE VARCHAR(100);
ALTER TABLE public.person ALTER COLUMN gender TYPE VARCHAR(10);

-- 2. Aplicar as restrições de obrigatoriedade (NOT NULL) conforme o @Column(nullable = false)
-- Nota: Certifique-se de que não existem valores NULL antigos nessas colunas antes de rodar.
ALTER TABLE public.person ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE public.person ALTER COLUMN last_name SET NOT NULL;
ALTER TABLE public.person ALTER COLUMN cpf SET NOT NULL;
ALTER TABLE public.person ALTER COLUMN email SET NOT NULL;
ALTER TABLE public.person ALTER COLUMN password SET NOT NULL;
ALTER TABLE public.person ALTER COLUMN address SET NOT NULL;
ALTER TABLE public.person ALTER COLUMN role SET NOT NULL;

-- 3. Garantir as restrições de unicidade (UNIQUE) para CPF e E-mail
DO $$
BEGIN
    -- Adiciona constraint UNIQUE para o CPF caso ela ainda não exista
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_person_cpf') THEN
ALTER TABLE public.person ADD CONSTRAINT uk_person_cpf UNIQUE (cpf);
END IF;

    -- Adiciona constraint UNIQUE para o E-mail caso ela ainda não exista
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_person_email') THEN
ALTER TABLE public.person ADD CONSTRAINT uk_person_email UNIQUE (email);
END IF;
END $$;