-- Apagar banco se existir e criar novo
DROP DATABASE IF EXISTS biblioteca_escolar;
CREATE DATABASE biblioteca_escolar;
USE biblioteca_escolar;

-- Tabela de Alunos
CREATE TABLE alunos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    endereco VARCHAR(200) NOT NULL
);

-- Tabela de Livros
CREATE TABLE livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    ano_publicacao INT NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    disponivel BOOLEAN DEFAULT TRUE
);

-- Tabela de Empréstimos
CREATE TABLE emprestimos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aluno_id INT NOT NULL,
    livro_id INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_prevista_devolucao DATE NOT NULL,
    data_efetiva_devolucao DATE NULL,
    devolvido BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    FOREIGN KEY (livro_id) REFERENCES livros(id) ON DELETE CASCADE
);

-- Dados iniciais para teste
INSERT INTO alunos (nome, email, telefone, endereco) VALUES
('João Silva', 'joao.silva@email.com', '(11) 99999-1111', 'Rua das Flores, 123 - Centro'),
('Maria Santos', 'maria.santos@email.com', '(11) 99999-2222', 'Avenida Brasil, 456 - Jardim'),
('Pedro Oliveira', 'pedro.oliveira@email.com', '(11) 99999-3333', 'Rua da Escola, 789 - Vila Nova'),
('Ana Costa', 'ana.costa@email.com', '(11) 99999-4444', 'Praça da Sé, 101 - Centro'),
('Carlos Souza', 'carlos.souza@email.com', '(11) 99999-5555', 'Rua do Comércio, 202 - Centro');

INSERT INTO livros (titulo, autor, isbn, ano_publicacao, categoria, disponivel) VALUES
('Dom Casmurro', 'Machado de Assis', '978-85-359-0277-5', 1899, 'Literatura Brasileira', TRUE),
('1984', 'George Orwell', '978-85-250-4708-4', 1949, 'Ficção Científica', TRUE),
('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', '978-85-359-0277-6', 1943, 'Literatura Infantil', TRUE),
('Harry Potter e a Pedra Filosofal', 'J.K. Rowling', '978-85-325-1101-4', 1997, 'Fantasia', TRUE),
('O Cortiço', 'Aluísio Azevedo', '978-85-359-0278-2', 1890, 'Literatura Brasileira', TRUE),
('Capitães da Areia', 'Jorge Amado', '978-85-359-0279-9', 1937, 'Literatura Brasileira', TRUE),
('A Arte da Guerra', 'Sun Tzu', '978-85-250-4709-1', 500, 'Filosofia', TRUE),
('O Hobbit', 'J.R.R. Tolkien', '978-85-325-1102-1', 1937, 'Fantasia', TRUE);

-- Alguns empréstimos para demonstração
INSERT INTO emprestimos (aluno_id, livro_id, data_emprestimo, data_prevista_devolucao, devolvido) VALUES
(1, 1, '2024-09-01', '2024-09-16', FALSE),
(2, 3, '2024-09-05', '2024-09-20', FALSE),
(3, 2, '2024-08-20', '2024-09-04', TRUE);

-- Atualizar disponibilidade dos livros emprestados
UPDATE livros SET disponivel = FALSE WHERE id IN (1, 3);

-- Verificar se tudo foi criado corretamente
SHOW TABLES;
SELECT 'Alunos cadastrados:' as Info, COUNT(*) as Total FROM alunos;
SELECT 'Livros cadastrados:' as Info, COUNT(*) as Total FROM livros;
SELECT 'Empréstimos registrados:' as Info, COUNT(*) as Total FROM emprestimos;