-- Atualiza a tabela books para relacionar o autor com a entidade Person

-- Remove o campo antigo que armazenava somente o nome do autor
ALTER TABLE books
DROP COLUMN author;


-- Cria a coluna que armazenará o id da pessoa autora do livro
ALTER TABLE books
    ADD COLUMN author_id BIGINT NOT NULL;


-- Cria a chave estrangeira ligando Book com Person
ALTER TABLE books
    ADD CONSTRAINT fk_books_author
        FOREIGN KEY (author_id)
            REFERENCES person(id);