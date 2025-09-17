package biblioteca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BibliotecaSimples extends JFrame {
    
    // ALTERE AQUI SUA SENHA DO MYSQL
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_escolar";
    private static final String USER = "root";
    private static final String PASS = "123456"; // COLOQUE SUA SENHA AQUI
    
    private JTextArea areaResultados;
    
    public BibliotecaSimples() {
        setTitle("Sistema Biblioteca Escolar - Recuperação Etapa 4");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        criarInterface();
        testarConexaoInicial();
    }
    
    private void criarInterface() {
        setLayout(new BorderLayout());
        
        // Título
        JLabel titulo = new JLabel("SISTEMA DE BIBLIOTECA ESCOLAR", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titulo.setBackground(new Color(70, 130, 180));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new GridLayout(3, 3, 10, 10));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Criar botões
        JButton btnTestar = new JButton("1. Testar Conexão");
        JButton btnCadAluno = new JButton("2. Cadastrar Aluno");
        JButton btnCadLivro = new JButton("3. Cadastrar Livro");
        JButton btnEmprestimo = new JButton("4. Fazer Empréstimo");
        JButton btnDevolucao = new JButton("5. Devolver Livro");
        JButton btnListAlunos = new JButton("6. Listar Alunos");
        JButton btnListLivros = new JButton("7. Listar Livros");
        JButton btnEmprestimos = new JButton("8. Ver Empréstimos");
        JButton btnRelatorio = new JButton("9. Relatório Geral");
        
        // Estilizar botões
        JButton[] botoes = {btnTestar, btnCadAluno, btnCadLivro, btnEmprestimo, 
                           btnDevolucao, btnListAlunos, btnListLivros, btnEmprestimos, btnRelatorio};
        
        for (JButton botao : botoes) {
            botao.setFont(new Font("Arial", Font.BOLD, 12));
            botao.setBackground(new Color(70, 130, 180));
            botao.setForeground(Color.WHITE);
            botao.setFocusPainted(false);
            painelBotoes.add(botao);
        }
        
        // Área de resultados
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaResultados.setBackground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(areaResultados);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados das Operações"));
        scroll.setPreferredSize(new Dimension(0, 300));
        
        // Adicionar ações aos botões
        btnTestar.addActionListener(e -> testarConexao());
        btnCadAluno.addActionListener(e -> cadastrarAluno());
        btnCadLivro.addActionListener(e -> cadastrarLivro());
        btnEmprestimo.addActionListener(e -> fazerEmprestimo());
        btnDevolucao.addActionListener(e -> devolverLivro());
        btnListAlunos.addActionListener(e -> listarAlunos());
        btnListLivros.addActionListener(e -> listarLivros());
        btnEmprestimos.addActionListener(e -> listarEmprestimos());
        btnRelatorio.addActionListener(e -> gerarRelatorio());
        
        // Montagem
        add(titulo, BorderLayout.NORTH);
        add(painelBotoes, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
    }
    
    private void testarConexaoInicial() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                adicionarTexto("✅ SISTEMA INICIADO COM SUCESSO!\n");
                adicionarTexto("✅ Conexão com MySQL estabelecida!\n");
                adicionarTexto("Banco: biblioteca_escolar\n");
                adicionarTexto("Status: Pronto para uso\n");
                adicionarTexto("=" .repeat(50) + "\n\n");
            }
        } catch (Exception e) {
            adicionarTexto("❌ ERRO DE INICIALIZAÇÃO:\n");
            adicionarTexto(e.getMessage() + "\n");
            adicionarTexto("Verifique se o MySQL está rodando e se a senha está correta!\n\n");
        }
    }
    
    private void testarConexao() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            adicionarTexto("✅ TESTE DE CONEXÃO REALIZADO COM SUCESSO!\n");
            adicionarTexto("Host: localhost:3306\n");
            adicionarTexto("Banco: biblioteca_escolar\n");
            adicionarTexto("Status: Conectado\n\n");
        } catch (SQLException e) {
            adicionarTexto("❌ ERRO NA CONEXÃO:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void cadastrarAluno() {
        String nome = JOptionPane.showInputDialog(this, "Nome do aluno:");
        if (nome == null || nome.trim().isEmpty()) return;
        
        String email = JOptionPane.showInputDialog(this, "Email do aluno:");
        if (email == null || email.trim().isEmpty()) return;
        
        String telefone = JOptionPane.showInputDialog(this, "Telefone:");
        if (telefone == null) telefone = "";
        
        String endereco = JOptionPane.showInputDialog(this, "Endereço:");
        if (endereco == null) endereco = "";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO alunos (nome, email, telefone, endereco) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.setString(3, telefone);
                stmt.setString(4, endereco);
                stmt.executeUpdate();
                
                adicionarTexto("✅ ALUNO CADASTRADO COM SUCESSO!\n");
                adicionarTexto("Nome: " + nome + "\n");
                adicionarTexto("Email: " + email + "\n\n");
            }
        } catch (SQLException e) {
            adicionarTexto("❌ ERRO AO CADASTRAR ALUNO:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void cadastrarLivro() {
        String titulo = JOptionPane.showInputDialog(this, "Título do livro:");
        if (titulo == null || titulo.trim().isEmpty()) return;
        
        String autor = JOptionPane.showInputDialog(this, "Autor do livro:");
        if (autor == null || autor.trim().isEmpty()) return;
        
        String isbn = JOptionPane.showInputDialog(this, "ISBN:");
        if (isbn == null) isbn = "";
        
        String anoStr = JOptionPane.showInputDialog(this, "Ano de publicação:");
        int ano = 2000;
        try {
            ano = Integer.parseInt(anoStr);
        } catch (Exception e) {
            ano = 2000;
        }
        
        String categoria = JOptionPane.showInputDialog(this, "Categoria:");
        if (categoria == null) categoria = "Geral";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO livros (titulo, autor, isbn, ano_publicacao, categoria, disponivel) VALUES (?, ?, ?, ?, ?, TRUE)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, titulo);
                stmt.setString(2, autor);
                stmt.setString(3, isbn);
                stmt.setInt(4, ano);
                stmt.setString(5, categoria);
                stmt.executeUpdate();
                
                adicionarTexto("✅ LIVRO CADASTRADO COM SUCESSO!\n");
                adicionarTexto("Título: " + titulo + "\n");
                adicionarTexto("Autor: " + autor + "\n\n");
            }
        } catch (SQLException e) {
            adicionarTexto("❌ ERRO AO CADASTRAR LIVRO:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void fazerEmprestimo() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            // Listar alunos
            StringBuilder alunos = new StringBuilder("Alunos disponíveis:\n");
            String sqlAlunos = "SELECT id, nome FROM alunos ORDER BY nome";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlAlunos)) {
                while (rs.next()) {
                    alunos.append(rs.getInt("id")).append(" - ").append(rs.getString("nome")).append("\n");
                }
            }
            
            String alunoIdStr = JOptionPane.showInputDialog(this, alunos.toString() + "\nDigite o ID do aluno:");
            if (alunoIdStr == null) return;
            int alunoId = Integer.parseInt(alunoIdStr);
            
            // Listar livros disponíveis
            StringBuilder livros = new StringBuilder("Livros disponíveis:\n");
            String sqlLivros = "SELECT id, titulo FROM livros WHERE disponivel = TRUE ORDER BY titulo";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlLivros)) {
                while (rs.next()) {
                    livros.append(rs.getInt("id")).append(" - ").append(rs.getString("titulo")).append("\n");
                }
            }
            
            String livroIdStr = JOptionPane.showInputDialog(this, livros.toString() + "\nDigite o ID do livro:");
            if (livroIdStr == null) return;
            int livroId = Integer.parseInt(livroIdStr);
            
            // Realizar empréstimo
            conn.setAutoCommit(false);
            
            String sqlEmp = "INSERT INTO emprestimos (aluno_id, livro_id, data_emprestimo, data_prevista_devolucao, devolvido) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), FALSE)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlEmp)) {
                stmt.setInt(1, alunoId);
                stmt.setInt(2, livroId);
                stmt.executeUpdate();
            }
            
            String sqlUpdate = "UPDATE livros SET disponivel = FALSE WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setInt(1, livroId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            adicionarTexto("✅ EMPRÉSTIMO REALIZADO COM SUCESSO!\n");
            adicionarTexto("Aluno ID: " + alunoId + "\n");
            adicionarTexto("Livro ID: " + livroId + "\n");
            adicionarTexto("Prazo: 15 dias\n\n");
            
        } catch (Exception e) {
            adicionarTexto("❌ ERRO AO FAZER EMPRÉSTIMO:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void devolverLivro() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            // Listar empréstimos ativos
            StringBuilder emprestimos = new StringBuilder("Empréstimos ativos:\n");
            String sql = "SELECT e.id, a.nome, l.titulo FROM emprestimos e JOIN alunos a ON e.aluno_id = a.id JOIN livros l ON e.livro_id = l.id WHERE e.devolvido = FALSE";
            
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    emprestimos.append(rs.getInt("id")).append(" - ")
                              .append(rs.getString("nome")).append(" - ")
                              .append(rs.getString("titulo")).append("\n");
                }
            }
            
            String empIdStr = JOptionPane.showInputDialog(this, emprestimos.toString() + "\nDigite o ID do empréstimo:");
            if (empIdStr == null) return;
            int empId = Integer.parseInt(empIdStr);
            
            // Buscar livro do empréstimo
            int livroId = 0;
            String sqlBusca = "SELECT livro_id FROM emprestimos WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlBusca)) {
                stmt.setInt(1, empId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        livroId = rs.getInt("livro_id");
                    }
                }
            }
            
            // Realizar devolução
            conn.setAutoCommit(false);
            
            String sqlDev = "UPDATE emprestimos SET devolvido = TRUE, data_efetiva_devolucao = CURDATE() WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDev)) {
                stmt.setInt(1, empId);
                stmt.executeUpdate();
            }
            
            String sqlDisp = "UPDATE livros SET disponivel = TRUE WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDisp)) {
                stmt.setInt(1, livroId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            adicionarTexto("✅ DEVOLUÇÃO REALIZADA COM SUCESSO!\n");
            adicionarTexto("Empréstimo ID: " + empId + "\n\n");
            
        } catch (Exception e) {
            adicionarTexto("❌ ERRO AO DEVOLVER LIVRO:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void listarAlunos() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            adicionarTexto("📋 LISTA DE ALUNOS:\n");
            adicionarTexto("=" .repeat(50) + "\n");
            
            String sql = "SELECT * FROM alunos ORDER BY nome";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    adicionarTexto("ID: " + rs.getInt("id") + "\n");
                    adicionarTexto("Nome: " + rs.getString("nome") + "\n");
                    adicionarTexto("Email: " + rs.getString("email") + "\n");
                    adicionarTexto("Telefone: " + rs.getString("telefone") + "\n");
                    adicionarTexto("-".repeat(30) + "\n");
                }
            }
            adicionarTexto("\n");
        } catch (SQLException e) {
            adicionarTexto("❌ ERRO AO LISTAR ALUNOS:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void listarLivros() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            adicionarTexto("📚 LISTA DE LIVROS:\n");
            adicionarTexto("=" .repeat(50) + "\n");
            
            String sql = "SELECT * FROM livros ORDER BY titulo";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    adicionarTexto("ID: " + rs.getInt("id") + "\n");
                    adicionarTexto("Título: " + rs.getString("titulo") + "\n");
                    adicionarTexto("Autor: " + rs.getString("autor") + "\n");
                    adicionarTexto("Categoria: " + rs.getString("categoria") + "\n");
                    adicionarTexto("Status: " + (rs.getBoolean("disponivel") ? "Disponível" : "Emprestado") + "\n");
                    adicionarTexto("-".repeat(30) + "\n");
                }
            }
            adicionarTexto("\n");
        } catch (SQLException e) {
            adicionarTexto("❌ ERRO AO LISTAR LIVROS:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void listarEmprestimos() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            adicionarTexto("📋 LISTA DE EMPRÉSTIMOS:\n");
            adicionarTexto("=" .repeat(50) + "\n");
            
            String sql = """
                SELECT e.id, a.nome as aluno, l.titulo as livro, 
                       e.data_emprestimo, e.data_prevista_devolucao,
                       CASE WHEN e.devolvido THEN 'Devolvido' ELSE 'Ativo' END as status
                FROM emprestimos e
                JOIN alunos a ON e.aluno_id = a.id
                JOIN livros l ON e.livro_id = l.id
                ORDER BY e.data_emprestimo DESC
                """;
                
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    adicionarTexto("ID: " + rs.getInt("id") + "\n");
                    adicionarTexto("Aluno: " + rs.getString("aluno") + "\n");
                    adicionarTexto("Livro: " + rs.getString("livro") + "\n");
                    adicionarTexto("Empréstimo: " + rs.getDate("data_emprestimo") + "\n");
                    adicionarTexto("Previsão: " + rs.getDate("data_prevista_devolucao") + "\n");
                    adicionarTexto("Status: " + rs.getString("status") + "\n");
                    adicionarTexto("-".repeat(30) + "\n");
                }
            }
            adicionarTexto("\n");
        } catch (SQLException e) {
            adicionarTexto("❌ ERRO AO LISTAR EMPRÉSTIMOS:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void gerarRelatorio() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            adicionarTexto("📊 RELATÓRIO GERAL DO SISTEMA:\n");
            adicionarTexto("=" .repeat(50) + "\n");
            
            // Estatísticas gerais
            String sql = "SELECT COUNT(*) as total FROM alunos";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    adicionarTexto("Total de Alunos: " + rs.getInt("total") + "\n");
                }
            }
            
            sql = "SELECT COUNT(*) as total FROM livros";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    adicionarTexto("Total de Livros: " + rs.getInt("total") + "\n");
                }
            }
            
            sql = "SELECT COUNT(*) as total FROM livros WHERE disponivel = TRUE";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    adicionarTexto("Livros Disponíveis: " + rs.getInt("total") + "\n");
                }
            }
            
            sql = "SELECT COUNT(*) as total FROM emprestimos WHERE devolvido = FALSE";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    adicionarTexto("Empréstimos Ativos: " + rs.getInt("total") + "\n");
                }
            }
            
            adicionarTexto("\n📈 RESUMO DE ATIVIDADES:\n");
            sql = "SELECT COUNT(*) as total FROM emprestimos";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    adicionarTexto("Total de Empréstimos Realizados: " + rs.getInt("total") + "\n");
                }
            }
            
            adicionarTexto("\nRelatório gerado em: " + new java.util.Date() + "\n\n");
            
        } catch (SQLException e) {
            adicionarTexto("❌ ERRO AO GERAR RELATÓRIO:\n" + e.getMessage() + "\n\n");
        }
    }
    
    private void adicionarTexto(String texto) {
        areaResultados.append(texto);
        areaResultados.setCaretPosition(areaResultados.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BibliotecaSimples().setVisible(true);
        });
    }
}