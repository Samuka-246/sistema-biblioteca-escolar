package biblioteca;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SistemaBiblioteca extends JFrame {
    
    // Configurações do banco - ALTERE A SENHA!
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_escolar";
    private static final String USER = "root";
    private static final String PASS = "123456"; // COLOQUE SUA SENHA AQUI
    
    // Cores do sistema
    private static final Color COR_PRIMARIA = new Color(63, 81, 181);
    private static final Color COR_SECUNDARIA = new Color(33, 150, 243);
    private static final Color COR_SUCESSO = new Color(76, 175, 80);
    private static final Color COR_AVISO = new Color(255, 193, 7);
    private static final Color COR_PERIGO = new Color(244, 67, 54);
    private static final Color COR_FUNDO = new Color(248, 250, 252);
    private static final Color COR_TEXTO = new Color(33, 37, 41);
    
    private JPanel painelPrincipal;
    private JPanel painelConteudo;
    private JLabel lblTituloPagina;
    private JLabel lblStatusConexao;
    
    public SistemaBiblioteca() {
        configurarJanelaPrincipal();
        inicializarComponentes();
        configurarBancoDados();
        mostrarDashboard();
    }
    
    private void configurarJanelaPrincipal() {
        setTitle("Sistema de Biblioteca Escolar - Versão Profissional");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Usar look padrão se não conseguir
        }
    }
    
    private void inicializarComponentes() {
        // Painel principal com gradiente
        painelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 242, 247), 
                                                         0, getHeight(), Color.WHITE);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        painelPrincipal.setLayout(new BorderLayout());
        
        // Cabeçalho
        JPanel cabecalho = criarCabecalho();
        
        // Sidebar
        JPanel sidebar = criarSidebar();
        
        // Área de conteúdo
        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setOpaque(false);
        painelConteudo.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Montagem
        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(sidebar, BorderLayout.WEST);
        painelPrincipal.add(painelConteudo, BorderLayout.CENTER);
        
        add(painelPrincipal);
    }
    
    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, COR_PRIMARIA, 
                                                         getWidth(), 0, COR_SECUNDARIA);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Sombra
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
            }
        };
        cabecalho.setLayout(new BorderLayout());
        cabecalho.setPreferredSize(new Dimension(0, 80));
        cabecalho.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        // Logo e título
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        
        JLabel icone = new JLabel("📚");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setOpaque(false);
        textoPanel.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        JLabel titulo = new JLabel("Sistema de Biblioteca Escolar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        
        JLabel subtitulo = new JLabel("Gerenciamento Inteligente de Acervo");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(255, 255, 255, 180));
        
        textoPanel.add(titulo);
        textoPanel.add(subtitulo);
        
        logoPanel.add(icone);
        logoPanel.add(textoPanel);
        
        // Status da conexão
        lblStatusConexao = new JLabel("Conectando...");
        lblStatusConexao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatusConexao.setForeground(Color.WHITE);
        
        cabecalho.add(logoPanel, BorderLayout.WEST);
        cabecalho.add(lblStatusConexao, BorderLayout.EAST);
        
        return cabecalho;
    }
    
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)),
            new EmptyBorder(30, 20, 30, 20)
        ));
        
        // Título do menu
        JLabel tituloMenu = new JLabel("NAVEGAÇÃO");
        tituloMenu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tituloMenu.setForeground(new Color(120, 120, 120));
        tituloMenu.setBorder(new EmptyBorder(0, 10, 15, 0));
        
        sidebar.add(tituloMenu);
        
        // Botões do menu
        String[][] menuItens = {
            {"📊", "Dashboard", "dashboard"},
            {"👥", "Gerenciar Alunos", "alunos"},
            {"📚", "Gerenciar Livros", "livros"},
            {"📋", "Empréstimos", "emprestimos"},
            {"📈", "Relatórios", "relatorios"},
            {"⚙️", "Configurações", "config"}
        };
        
        for (String[] item : menuItens) {
            JButton botao = criarBotaoSidebar(item[0], item[1], item[2]);
            sidebar.add(botao);
            sidebar.add(Box.createVerticalStrut(5));
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        // Botão sair
        JButton btnSair = criarBotaoSidebar("🚪", "Sair do Sistema", "sair");
        btnSair.setBackground(COR_PERIGO);
        btnSair.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(this, 
                "Deseja realmente sair do sistema?", 
                "Confirmar Saída", 
                JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        sidebar.add(btnSair);
        
        return sidebar;
    }
    
    private JButton criarBotaoSidebar(String icone, String texto, String acao) {
        JButton botao = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(COR_SECUNDARIA.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(COR_SECUNDARIA.getRed(), COR_SECUNDARIA.getGreen(), COR_SECUNDARIA.getBlue(), 30));
                } else {
                    g2d.setColor(getBackground());
                }
                
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                
                super.paintComponent(g);
            }
        };
        
        botao.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 12));
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        botao.setPreferredSize(new Dimension(240, 45));
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTexto.setForeground(COR_TEXTO);
        
        botao.add(lblIcone);
        botao.add(lblTexto);
        
        // Eventos
        switch (acao) {
            case "dashboard" -> botao.addActionListener(e -> mostrarDashboard());
            case "alunos" -> botao.addActionListener(e -> mostrarGerenciamentoAlunos());
            case "livros" -> botao.addActionListener(e -> mostrarGerenciamentoLivros());
            case "emprestimos" -> botao.addActionListener(e -> mostrarEmprestimos());
            case "relatorios" -> botao.addActionListener(e -> mostrarRelatorios());
            case "config" -> botao.addActionListener(e -> mostrarConfiguracoes());
        }
        
        return botao;
    }
    
    private void atualizarTituloPagina(String titulo, String subtitulo) {
        // Implementado nas funções de mostrar conteúdo
    }
    
    private void mostrarDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 30));
        dashboard.setOpaque(false);
        
        // Título
        JPanel tituloPanel = criarTituloSecao("📊 Dashboard", "Visão geral do sistema");
        
        // Cards de estatísticas
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setOpaque(false);
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            int totalAlunos = contarRegistros(conn, "SELECT COUNT(*) FROM alunos");
            int totalLivros = contarRegistros(conn, "SELECT COUNT(*) FROM livros");
            int livrosDisponiveis = contarRegistros(conn, "SELECT COUNT(*) FROM livros WHERE disponivel = TRUE");
            int emprestimosAtivos = contarRegistros(conn, "SELECT COUNT(*) FROM emprestimos WHERE devolvido = FALSE");
            
            cardsPanel.add(criarCardEstatistica("👥", "Total de Alunos", String.valueOf(totalAlunos), COR_SUCESSO));
            cardsPanel.add(criarCardEstatistica("📚", "Total de Livros", String.valueOf(totalLivros), COR_SECUNDARIA));
            cardsPanel.add(criarCardEstatistica("✅", "Livros Disponíveis", String.valueOf(livrosDisponiveis), COR_AVISO));
            cardsPanel.add(criarCardEstatistica("📋", "Empréstimos Ativos", String.valueOf(emprestimosAtivos), COR_PERIGO));
            
        } catch (SQLException e) {
            cardsPanel.add(criarCardEstatistica("❌", "Erro", "N/A", Color.GRAY));
        }
        
        // Ações rápidas
        JPanel acoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        acoesPanel.setOpaque(false);
        
        JButton btnNovoAluno = criarBotaoAcao("Cadastrar Aluno", COR_SUCESSO);
        JButton btnNovoLivro = criarBotaoAcao("Cadastrar Livro", COR_SECUNDARIA);
        JButton btnNovoEmprestimo = criarBotaoAcao("Novo Empréstimo", COR_AVISO);
        
        btnNovoAluno.addActionListener(e -> abrirDialogoCadastroAluno());
        btnNovoLivro.addActionListener(e -> abrirDialogoCadastroLivro());
        btnNovoEmprestimo.addActionListener(e -> abrirDialogoEmprestimo());
        
        acoesPanel.add(btnNovoAluno);
        acoesPanel.add(btnNovoLivro);
        acoesPanel.add(btnNovoEmprestimo);
        
        dashboard.add(tituloPanel, BorderLayout.NORTH);
        dashboard.add(cardsPanel, BorderLayout.CENTER);
        dashboard.add(acoesPanel, BorderLayout.SOUTH);
        
        painelConteudo.removeAll();
        painelConteudo.add(dashboard, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
    
    private void mostrarGerenciamentoAlunos() {
        JPanel alunosPanel = new JPanel(new BorderLayout(0, 20));
        alunosPanel.setOpaque(false);
        
        // Título
        JPanel tituloPanel = criarTituloSecao("👥 Gerenciamento de Alunos", "Cadastro e controle de estudantes");
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        
        JButton btnNovo = criarBotaoAcao("Novo Aluno", COR_SUCESSO);
        JButton btnAtualizar = criarBotaoAcao("Atualizar", COR_AVISO);
        
        btnNovo.addActionListener(e -> abrirDialogoCadastroAluno());
        btnAtualizar.addActionListener(e -> mostrarGerenciamentoAlunos());
        
        toolbar.add(btnNovo);
        toolbar.add(btnAtualizar);
        
        // Tabela
        JTable tabela = criarTabelaAlunos();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        
        alunosPanel.add(tituloPanel, BorderLayout.NORTH);
        alunosPanel.add(toolbar, BorderLayout.CENTER);
        alunosPanel.add(scroll, BorderLayout.SOUTH);
        
        painelConteudo.removeAll();
        painelConteudo.add(alunosPanel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
    
    private void mostrarGerenciamentoLivros() {
        JPanel livrosPanel = new JPanel(new BorderLayout(0, 20));
        livrosPanel.setOpaque(false);
        
        // Título
        JPanel tituloPanel = criarTituloSecao("📚 Gerenciamento de Livros", "Controle do acervo da biblioteca");
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        
        JButton btnNovo = criarBotaoAcao("Novo Livro", COR_SUCESSO);
        JButton btnAtualizar = criarBotaoAcao("Atualizar", COR_AVISO);
        
        btnNovo.addActionListener(e -> abrirDialogoCadastroLivro());
        btnAtualizar.addActionListener(e -> mostrarGerenciamentoLivros());
        
        toolbar.add(btnNovo);
        toolbar.add(btnAtualizar);
        
        // Tabela
        JTable tabela = criarTabelaLivros();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        
        livrosPanel.add(tituloPanel, BorderLayout.NORTH);
        livrosPanel.add(toolbar, BorderLayout.CENTER);
        livrosPanel.add(scroll, BorderLayout.SOUTH);
        
        painelConteudo.removeAll();
        painelConteudo.add(livrosPanel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
    
    private void mostrarEmprestimos() {
        JPanel emprestimosPanel = new JPanel(new BorderLayout(0, 20));
        emprestimosPanel.setOpaque(false);
        
        // Título
        JPanel tituloPanel = criarTituloSecao("📋 Sistema de Empréstimos", "Controle de empréstimos e devoluções");
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        
        JButton btnNovoEmp = criarBotaoAcao("Novo Empréstimo", COR_SUCESSO);
        JButton btnDevolver = criarBotaoAcao("Devolver", COR_SECUNDARIA);
        JButton btnAtualizar = criarBotaoAcao("Atualizar", COR_AVISO);
        
        btnNovoEmp.addActionListener(e -> abrirDialogoEmprestimo());
        btnDevolver.addActionListener(e -> abrirDialogoDevolucao());
        btnAtualizar.addActionListener(e -> mostrarEmprestimos());
        
        toolbar.add(btnNovoEmp);
        toolbar.add(btnDevolver);
        toolbar.add(btnAtualizar);
        
        // Tabela
        JTable tabela = criarTabelaEmprestimos();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        
        emprestimosPanel.add(tituloPanel, BorderLayout.NORTH);
        emprestimosPanel.add(toolbar, BorderLayout.CENTER);
        emprestimosPanel.add(scroll, BorderLayout.SOUTH);
        
        painelConteudo.removeAll();
        painelConteudo.add(emprestimosPanel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
    
    private void mostrarRelatorios() {
        JPanel relatoriosPanel = new JPanel(new BorderLayout(0, 20));
        relatoriosPanel.setOpaque(false);
        
        // Título
        JPanel tituloPanel = criarTituloSecao("📈 Relatórios", "Análises e estatísticas do sistema");
        
        StringBuilder relatorio = new StringBuilder();
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            relatorio.append("═══ RELATÓRIO SISTEMA BIBLIOTECA ═══\n\n");
            
            // Estatísticas gerais
            relatorio.append("📊 ESTATÍSTICAS GERAIS:\n");
            relatorio.append("─────────────────────\n");
            relatorio.append("Total de Alunos: ").append(contarRegistros(conn, "SELECT COUNT(*) FROM alunos")).append("\n");
            relatorio.append("Total de Livros: ").append(contarRegistros(conn, "SELECT COUNT(*) FROM livros")).append("\n");
            relatorio.append("Livros Disponíveis: ").append(contarRegistros(conn, "SELECT COUNT(*) FROM livros WHERE disponivel = TRUE")).append("\n");
            relatorio.append("Total de Empréstimos: ").append(contarRegistros(conn, "SELECT COUNT(*) FROM emprestimos")).append("\n");
            relatorio.append("Empréstimos Ativos: ").append(contarRegistros(conn, "SELECT COUNT(*) FROM emprestimos WHERE devolvido = FALSE")).append("\n\n");
            
            // Empréstimos recentes
            relatorio.append("📋 ÚLTIMOS EMPRÉSTIMOS:\n");
            relatorio.append("─────────────────────\n");
            String sql = """
                SELECT a.nome, l.titulo, e.data_emprestimo, 
                       CASE WHEN e.devolvido THEN 'Devolvido' ELSE 'Ativo' END as status
                FROM emprestimos e
                JOIN alunos a ON e.aluno_id = a.id
                JOIN livros l ON e.livro_id = l.id
                ORDER BY e.data_emprestimo DESC
                LIMIT 10
                """;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    relatorio.append("• ").append(rs.getString("nome")).append(" - ");
                    relatorio.append(rs.getString("titulo")).append(" - ");
                    relatorio.append(rs.getDate("data_emprestimo")).append(" - ");
                    relatorio.append(rs.getString("status")).append("\n");
                }
            }
            
            // Livros mais emprestados
            relatorio.append("\n📚 LIVROS MAIS EMPRESTADOS:\n");
            relatorio.append("──────────────────────────\n");
            sql = """
                SELECT l.titulo, COUNT(e.id) as total_emprestimos
                FROM livros l
                LEFT JOIN emprestimos e ON l.id = e.livro_id
                GROUP BY l.id, l.titulo
                ORDER BY total_emprestimos DESC
                LIMIT 5
                """;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    relatorio.append("• ").append(rs.getString("titulo"))
                             .append(" (").append(rs.getInt("total_emprestimos")).append(" empréstimos)\n");
                }
            }
            
        } catch (SQLException e) {
            relatorio.append("Erro ao gerar relatório: ").append(e.getMessage());
        }
        
        JTextArea textArea = new JTextArea(relatorio.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setBackground(new Color(248, 250, 252));
        textArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        relatoriosPanel.add(tituloPanel, BorderLayout.NORTH);
        relatoriosPanel.add(scroll, BorderLayout.CENTER);
        
        painelConteudo.removeAll();
        painelConteudo.add(relatoriosPanel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
    
    private void mostrarConfiguracoes() {
        JPanel configPanel = new JPanel(new BorderLayout(0, 20));
        configPanel.setOpaque(false);
        
        // Título
        JPanel tituloPanel = criarTituloSecao("⚙️ Configurações", "Ajustes do sistema");
        
        JPanel conteudoConfig = new JPanel(new GridBagLayout());
        conteudoConfig.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Informações do sistema
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informações do Sistema"));
        infoPanel.setBackground(Color.WHITE);
        
        infoPanel.add(new JLabel("Versão:"));
        infoPanel.add(new JLabel("1.0 - Profissional"));
        
        infoPanel.add(new JLabel("Desenvolvido em:"));
        infoPanel.add(new JLabel("Java + MySQL"));
        
        infoPanel.add(new JLabel("Status do Banco:"));
        infoPanel.add(new JLabel("🟢 Conectado"));
        
        infoPanel.add(new JLabel("Data/Hora:"));
        infoPanel.add(new JLabel(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        conteudoConfig.add(infoPanel, gbc);
        
        // Botões de ação
        JPanel acoesPanel = new JPanel(new FlowLayout());
        acoesPanel.setOpaque(false);
        
        JButton btnTestarConexao = criarBotaoAcao("Testar Conexão", COR_SECUNDARIA);
        JButton btnLimparDados = criarBotaoAcao("Limpar Dados", COR_PERIGO);
        
        btnTestarConexao.addActionListener(e -> testarConexaoBanco());
        btnLimparDados.addActionListener(e -> limparDadosSistema());
        
        acoesPanel.add(btnTestarConexao);
        acoesPanel.add(btnLimparDados);
        
        gbc.gridy = 1;
        conteudoConfig.add(acoesPanel, gbc);
        
        configPanel.add(tituloPanel, BorderLayout.NORTH);
        configPanel.add(conteudoConfig, BorderLayout.CENTER);
        
        painelConteudo.removeAll();
        painelConteudo.add(configPanel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
    
    // Métodos auxiliares para criação de componentes
    private JPanel criarTituloSecao(String titulo, String subtitulo) {
        JPanel tituloPanel = new JPanel();
        tituloPanel.setLayout(new BoxLayout(tituloPanel, BoxLayout.Y_AXIS));
        tituloPanel.setOpaque(false);
        tituloPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        tituloPanel.add(lblTitulo);
        tituloPanel.add(Box.createVerticalStrut(5));
        tituloPanel.add(lblSubtitulo);
        
        return tituloPanel;
    }
    
    private JPanel criarCardEstatistica(String icone, String titulo, String valor, Color cor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 15, 15);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 15, 15);
                
                g2d.setColor(cor);
                g2d.fillRoundRect(0, 0, getWidth() - 5, 8, 15, 15);
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 150));
        
        JLabel lblIcone = new JLabel(icone, SwingConstants.CENTER);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(cor);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(new Color(100, 100, 100));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(lblValor);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblTitulo);
        
        card.add(lblIcone, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton criarBotaoAcao(String texto, Color cor) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(cor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(cor.brighter());
                } else {
                    g2d.setColor(cor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setForeground(Color.WHITE);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(150, 40));
        
        return botao;
    }
    
    private JTable criarTabelaAlunos() {
        String[] colunas = {"ID", "Nome", "Email", "Telefone", "Endereço"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT * FROM alunos ORDER BY nome";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("endereco")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar alunos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(35);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(COR_SECUNDARIA);
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.setGridColor(new Color(230, 230, 230));
        
        return tabela;
    }
    
    private JTable criarTabelaLivros() {
        String[] colunas = {"ID", "Título", "Autor", "ISBN", "Ano", "Categoria", "Disponível"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT * FROM livros ORDER BY titulo";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getInt("ano_publicacao"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponivel") ? "Sim" : "Não"
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar livros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(35);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(COR_SECUNDARIA);
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.setGridColor(new Color(230, 230, 230));
        
        return tabela;
    }
    
    private JTable criarTabelaEmprestimos() {
        String[] colunas = {"ID", "Aluno", "Livro", "Data Empréstimo", "Previsão Devolução", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = """
                SELECT e.id, a.nome as aluno, l.titulo as livro, 
                       e.data_emprestimo, e.data_prevista_devolucao,
                       CASE WHEN e.devolvido THEN 'Devolvido' ELSE 'Ativo' END as status
                FROM emprestimos e
                JOIN alunos a ON e.aluno_id = a.id
                JOIN livros l ON e.livro_id = l.id
                ORDER BY e.data_emprestimo DESC
                """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("aluno"),
                        rs.getString("livro"),
                        rs.getDate("data_emprestimo"),
                        rs.getDate("data_prevista_devolucao"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar empréstimos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(35);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(COR_SECUNDARIA);
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.setGridColor(new Color(230, 230, 230));
        
        return tabela;
    }
    
    // Diálogos de cadastro e formulários
    private void abrirDialogoCadastroAluno() {
        JDialog dialog = new JDialog(this, "Cadastrar Aluno", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campos
        JTextField txtNome = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtTelefone = new JTextField(20);
        JTextField txtEndereco = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNome, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTelefone, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEndereco, gbc);
        
        // Botões
        JPanel botoes = new JPanel();
        JButton btnSalvar = criarBotaoAcao("Salvar", COR_SUCESSO);
        JButton btnCancelar = criarBotaoAcao("Cancelar", COR_PERIGO);
        
        btnSalvar.addActionListener(e -> {
            if (cadastrarAluno(txtNome.getText(), txtEmail.getText(), 
                             txtTelefone.getText(), txtEndereco.getText())) {
                dialog.dispose();
                mostrarGerenciamentoAlunos();
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(botoes, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void abrirDialogoCadastroLivro() {
        JDialog dialog = new JDialog(this, "Cadastrar Livro", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campos
        JTextField txtTitulo = new JTextField(20);
        JTextField txtAutor = new JTextField(20);
        JTextField txtIsbn = new JTextField(20);
        JTextField txtAno = new JTextField(20);
        JTextField txtCategoria = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTitulo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1;
        panel.add(txtAutor, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(txtIsbn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Ano:"), gbc);
        gbc.gridx = 1;
        panel.add(txtAno, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCategoria, gbc);
        
        // Botões
        JPanel botoes = new JPanel();
        JButton btnSalvar = criarBotaoAcao("Salvar", COR_SUCESSO);
        JButton btnCancelar = criarBotaoAcao("Cancelar", COR_PERIGO);
        
        btnSalvar.addActionListener(e -> {
            try {
                int ano = Integer.parseInt(txtAno.getText());
                if (cadastrarLivro(txtTitulo.getText(), txtAutor.getText(), 
                                 txtIsbn.getText(), ano, txtCategoria.getText())) {
                    dialog.dispose();
                    mostrarGerenciamentoLivros();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Ano deve ser um número válido!");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(botoes, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void abrirDialogoEmprestimo() {
        JDialog dialog = new JDialog(this, "Novo Empréstimo", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ComboBoxes
        JComboBox<String> cmbAlunos = new JComboBox<>();
        JComboBox<String> cmbLivros = new JComboBox<>();
        
        carregarAlunosCombo(cmbAlunos);
        carregarLivrosDisponiveis(cmbLivros);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Aluno:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbAlunos, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Livro:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbLivros, gbc);
        
        // Botões
        JPanel botoes = new JPanel();
        JButton btnSalvar = criarBotaoAcao("Emprestar", COR_SUCESSO);
        JButton btnCancelar = criarBotaoAcao("Cancelar", COR_PERIGO);
        
        btnSalvar.addActionListener(e -> {
            String alunoSel = (String) cmbAlunos.getSelectedItem();
            String livroSel = (String) cmbLivros.getSelectedItem();
            
            if (alunoSel != null && livroSel != null && realizarEmprestimo(alunoSel, livroSel)) {
                dialog.dispose();
                mostrarEmprestimos();
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(botoes, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void abrirDialogoDevolucao() {
        JDialog dialog = new JDialog(this, "Devolver Livro", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JComboBox<String> cmbEmprestimos = new JComboBox<>();
        carregarEmprestimosAtivos(cmbEmprestimos);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Empréstimo Ativo:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbEmprestimos, gbc);
        
        // Botões
        JPanel botoes = new JPanel();
        JButton btnDevolver = criarBotaoAcao("Devolver", COR_SUCESSO);
        JButton btnCancelar = criarBotaoAcao("Cancelar", COR_PERIGO);
        
        btnDevolver.addActionListener(e -> {
            String empSel = (String) cmbEmprestimos.getSelectedItem();
            if (empSel != null && devolverLivro(empSel)) {
                dialog.dispose();
                mostrarEmprestimos();
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        botoes.add(btnDevolver);
        botoes.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(botoes, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // Métodos de banco de dados
    private void configurarBancoDados() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                lblStatusConexao.setText("🟢 Conectado ao MySQL");
                System.out.println("Conexão estabelecida com sucesso!");
            }
            
        } catch (ClassNotFoundException e) {
            lblStatusConexao.setText("❌ Driver MySQL não encontrado");
            JOptionPane.showMessageDialog(this, "Driver MySQL não encontrado!\nBaixe o MySQL Connector/J", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            lblStatusConexao.setText("❌ Erro de conexão");
            JOptionPane.showMessageDialog(this, "Erro ao conectar com o banco:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int contarRegistros(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    private boolean cadastrarAluno(String nome, String email, String telefone, String endereco) {
        if (nome.trim().isEmpty() || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e email são obrigatórios!");
            return false;
        }
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO alunos (nome, email, telefone, endereco) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.setString(3, telefone);
                stmt.setString(4, endereco);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Aluno cadastrado com sucesso!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar aluno:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean cadastrarLivro(String titulo, String autor, String isbn, int ano, String categoria) {
        if (titulo.trim().isEmpty() || autor.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Título e autor são obrigatórios!");
            return false;
        }
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO livros (titulo, autor, isbn, ano_publicacao, categoria, disponivel) VALUES (?, ?, ?, ?, ?, TRUE)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, titulo);
                stmt.setString(2, autor);
                stmt.setString(3, isbn);
                stmt.setInt(4, ano);
                stmt.setString(5, categoria);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Livro cadastrado com sucesso!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar livro:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean realizarEmprestimo(String alunoInfo, String livroInfo) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);
            
            int alunoId = Integer.parseInt(alunoInfo.split(" - ")[0]);
            int livroId = Integer.parseInt(livroInfo.split(" - ")[0]);
            
            // Inserir empréstimo
            String sqlEmp = "INSERT INTO emprestimos (aluno_id, livro_id, data_emprestimo, data_prevista_devolucao, devolvido) VALUES (?, ?, ?, ?, FALSE)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlEmp)) {
                stmt.setInt(1, alunoId);
                stmt.setInt(2, livroId);
                stmt.setDate(3, Date.valueOf(LocalDate.now()));
                stmt.setDate(4, Date.valueOf(LocalDate.now().plusDays(15)));
                stmt.executeUpdate();
            }
            
            // Marcar livro como indisponível
            String sqlLivro = "UPDATE livros SET disponivel = FALSE WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlLivro)) {
                stmt.setInt(1, livroId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            JOptionPane.showMessageDialog(this, "Empréstimo realizado com sucesso!");
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao realizar empréstimo:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean devolverLivro(String emprestimoInfo) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);
            
            int emprestimoId = Integer.parseInt(emprestimoInfo.split(" - ")[0]);
            
            // Buscar o livro do empréstimo
            String sqlBusca = "SELECT livro_id FROM emprestimos WHERE id = ?";
            int livroId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlBusca)) {
                stmt.setInt(1, emprestimoId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        livroId = rs.getInt("livro_id");
                    }
                }
            }
            
            // Marcar empréstimo como devolvido
            String sqlEmp = "UPDATE emprestimos SET devolvido = TRUE, data_efetiva_devolucao = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlEmp)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setInt(2, emprestimoId);
                stmt.executeUpdate();
            }
            
            // Marcar livro como disponível
            String sqlLivro = "UPDATE livros SET disponivel = TRUE WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlLivro)) {
                stmt.setInt(1, livroId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            JOptionPane.showMessageDialog(this, "Devolução realizada com sucesso!");
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao devolver livro:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void carregarAlunosCombo(JComboBox<String> combo) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT id, nome FROM alunos ORDER BY nome";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    combo.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar alunos: " + e.getMessage());
        }
    }
    
    private void carregarLivrosDisponiveis(JComboBox<String> combo) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT id, titulo FROM livros WHERE disponivel = TRUE ORDER BY titulo";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    combo.addItem(rs.getInt("id") + " - " + rs.getString("titulo"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar livros: " + e.getMessage());
        }
    }
    
    private void carregarEmprestimosAtivos(JComboBox<String> combo) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = """
                SELECT e.id, a.nome, l.titulo 
                FROM emprestimos e
                JOIN alunos a ON e.aluno_id = a.id
                JOIN livros l ON e.livro_id = l.id
                WHERE e.devolvido = FALSE
                ORDER BY e.data_emprestimo
                """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    combo.addItem(rs.getInt("id") + " - " + rs.getString("nome") + " - " + rs.getString("titulo"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar empréstimos: " + e.getMessage());
        }
    }
    
    private void testarConexaoBanco() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            JOptionPane.showMessageDialog(this, 
                "✅ Conexão com banco de dados funcionando perfeitamente!\n" +
                "Host: localhost:3306\n" +
                "Banco: biblioteca_escolar\n" +
                "Status: Conectado", 
                "Teste de Conexão", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Erro ao conectar com o banco:\n" + e.getMessage(), 
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparDadosSistema() {
        int resposta = JOptionPane.showConfirmDialog(this,
            "⚠️ ATENÇÃO!\n\n" +
            "Esta ação irá apagar TODOS os dados do sistema:\n" +
            "• Todos os alunos cadastrados\n" +
            "• Todos os livros do acervo\n" +
            "• Histórico completo de empréstimos\n\n" +
            "Esta operação NÃO pode ser desfeita!\n\n" +
            "Deseja realmente continuar?",
            "Confirmar Limpeza de Dados",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (resposta == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                conn.setAutoCommit(false);
                
                // Ordem importante devido às chaves estrangeiras
                String[] sqlLimpeza = {
                    "DELETE FROM emprestimos",
                    "DELETE FROM livros", 
                    "DELETE FROM alunos"
                };
                
                for (String sql : sqlLimpeza) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(sql);
                    }
                }
                
                conn.commit();
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Dados do sistema limpos com sucesso!\n" +
                    "O banco está vazio e pronto para novos cadastros.", 
                    "Limpeza Concluída", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                // Atualizar a tela atual
                mostrarDashboard();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erro ao limpar dados:\n" + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Método principal
    public static void main(String[] args) {
        // Configurar look and feel
        
        
        // Splash Screen
        JWindow splash = new JWindow();
        splash.setSize(400, 300);
        splash.setLocationRelativeTo(null);
        
        JPanel splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(63, 81, 181), 
                                                         0, getHeight(), new Color(33, 150, 243));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        splashPanel.setLayout(new BorderLayout());
        
        JLabel splashLogo = new JLabel("📚", SwingConstants.CENTER);
        splashLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        
        JLabel splashTitulo = new JLabel("Sistema de Biblioteca Escolar", SwingConstants.CENTER);
        splashTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        splashTitulo.setForeground(Color.WHITE);
        
        JLabel splashSubtitulo = new JLabel("Inicializando sistema...", SwingConstants.CENTER);
        splashSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        splashSubtitulo.setForeground(new Color(255, 255, 255, 180));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(255, 255, 255, 50));
        progressBar.setForeground(Color.WHITE);
        
        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setOpaque(false);
        textoPanel.setBorder(new EmptyBorder(50, 50, 30, 50));
        
        textoPanel.add(splashTitulo);
        textoPanel.add(Box.createVerticalStrut(10));
        textoPanel.add(splashSubtitulo);
        textoPanel.add(Box.createVerticalStrut(30));
        textoPanel.add(progressBar);
        
        splashPanel.add(splashLogo, BorderLayout.CENTER);
        splashPanel.add(textoPanel, BorderLayout.SOUTH);
        
        splash.add(splashPanel);
        splash.setVisible(true);
        
        // Simular carregamento
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(3000); // 3 segundos
                splash.dispose();
                
                // Iniciar sistema principal
                SwingUtilities.invokeLater(() -> {
                    try {
                        new SistemaBiblioteca().setVisible(true);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, 
                            "Erro ao inicializar o sistema:\n" + e.getMessage(),
                            "Erro Crítico",
                            JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                });
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}