# 🚢 Batalha Naval - Refactor OO

Um jogo de Batalha Naval implementado em Java com arquitetura orientada a objetos. O projeto inclui suporte para modo manual (jogador vs CPU), modo automático (CPU vs CPU) e persistência de dados em banco de dados SQLite.

## 📋 Requisitos

- **Java 11+** (obrigatório para suportar text blocks)
- **Windows** (scripts de compilação fornecidos em `.bat`)
- **SQLite JDBC 3.53.1.0** (incluído em `lib/`)

## 📂 Estrutura do Projeto

```
batalhanaval/
├── domain/
│   ├── model/          # Modelos de domínio (Navio, Tabuleiro, Coordenada, etc)
│   └── player/         # Classes de jogadores (Humano, CPU)
├── service/            # Lógica de jogo (GameEngine, GameTester)
├── repository/         # Camada de persistência (banco de dados)
├── config/             # Configurações do jogo
├── ui/                 # Interface com usuário (Main.java, TerminalUI.java)
├── data/               # Banco de dados SQLite (criado automaticamente)
├── lib/                # Dependências externas (sqlite-jdbc)
├── out/                # Classe compiladas (gerado automaticamente)
├── compile.bat         # Script para compilar apenas
└── build_and_run.bat   # Script para compilar e executar
```

## 🚀 Como Executar

### Opção 1: Executar via Script (Recomendado - Windows)

1. Abra o Explorador de Arquivos e navegue até a pasta do projeto
2. **Clique duplo** em `build_and_run.bat`

Ou via Command Prompt:
```cmd
build_and_run.bat
```

### Opção 2: Compilar e Executar Manualmente

```cmd
# Compilar
javac -d out domain\model\*.java domain\player\*.java service\*.java repository\*.java config\*.java ui\*.java

# Executar o jogo
java -cp "out;lib\*" com.batalhanaval.ui.Main
```

### Opção 3: Executar via VS Code

Se tiver a extensão **Extension Pack for Java** instalada:
1. Abra o workspace: `batalhanaval.code-workspace`
2. Clique em `ui/Main.java`
3. Use o botão "Run" ou pressione `Ctrl+F5`

## 🎮 Menu Principal

Após executar, você verá o seguinte menu:

```
=== BATALHA NAVAL REFACTOR (OO) ===
1) Jogar Nova Partida
2) Ver Histórico de Partidas
3) Teste Automático (CPU vs CPU)
4) Sair
```

### Opção 1: Jogar Nova Partida
- Escolha entre posicionamento **manual** ou **automático** dos navios
- Jogue contra a CPU
- Digite coordenadas no formato: `A1`, `B5`, etc.
- Veja seus tiros e o tabuleiro do seu lado

### Opção 2: Ver Histórico
- Lista todas as partidas salvas no banco de dados
- Mostra o vencedor e data de cada partida

### Opção 3: Teste Automático (CPU vs CPU)
- Executa um teste automático com dois CPUs jogando entre si
- Validações de:
  - ✓ Inicialização do jogo
  - ✓ Posicionamento automático de frotas
  - ✓ Loop principal do jogo
  - ✓ Detecção de fim de jogo
  - ✓ Determinação de vencedor
  - ✓ Operações de banco de dados
  - ✓ Validação de navios restantes
  - ✓ Estado final dos tabuleiros

## 🧪 Testes

### Executar Teste Automático (Integração)

No menu principal, selecione a **opção 3** para executar o teste automático CPU vs CPU.

O teste valida:
- Inicialização correta do sistema
- Posicionamento automático de frotas
- Execução do loop de jogo
- Detecção de vitória/derrota
- Persistência de dados no banco de dados
- Cálculo correto de navios restantes

Exemplo de saída:
```
============================================================
INICIANDO TESTE AUTOMATIZADO - CPU vs CPU
============================================================

✓ Inicializando jogo...
✓ Jogo inicializado com sucesso

[TESTE 2] Posicionamento automático de frotas...
✓ CPU-1: 5 navios posicionados
✓ CPU-2: 5 navios posicionados

[TESTE 3] Executando loop principal do jogo...
✓ Loop principal executado: 127 turnos

[TESTE 4] Detecção de fim de jogo...
✓ Jogo finalizou corretamente após 127 turnos

[TESTE 5] Determinação de vencedor...
✓ Vencedor determinado: CPU-1

[TESTE 6] Operações de banco de dados...
✓ Partida salva com ID: 1
✓ Partida atualizada com sucesso
✓ Vencedor registrado: CPU-1

[TESTE 7] Validação de navios restantes...
✓ CPU-1 navios restantes: 5
✓ CPU-2 navios restantes: 0
✓ Validação OK: Pelo menos um jogador perdeu todos os navios

============================================================
TESTE CONCLUÍDO
============================================================
```

## 🗄️ Banco de Dados

O projeto usa **SQLite** para persistência de dados.

### Arquivo do Banco
- **Localização**: `data/batalha_naval.db`
- **Criado automaticamente** na primeira execução
- **Requer** permissão de escrita na pasta `data/`

### Tabelas

#### `match`
```sql
id          INTEGER PRIMARY KEY
winner      TEXT
created_at  TIMESTAMP
```

#### `move`
```sql
id          INTEGER PRIMARY KEY
match_id    INTEGER (FK)
player      TEXT
coordinate  TEXT
result      TEXT
created_at  TIMESTAMP
```

### Visualizar Dados do Banco

Execute o utilitário incluído:

```cmd
java -cp "out;lib\*" DataViewer
```

Isso exibirá:
- Histórico de partidas
- Detalhes de movimentos
- Estatísticas gerais

## 📖 Legenda do Tabuleiro

```
S  = Seu navio
X  = Acerto no navio inimigo
o  = Água (disparo na água)
.  = Vazio/Desconhecido (não disparado)
```

## 🛠️ Dependências

| Dependência | Versão | Localização | Uso |
|------------|--------|-------------|-----|
| Java | 11+ | Sistema | Runtime |
| SQLite JDBC | 3.53.1.0 | `lib/sqlite-jdbc-3.53.1.0.jar` | Persistência |

## ❌ Solução de Problemas

### Erro: "javac: comando não encontrado"
- **Solução**: Instale o JDK 11+ e adicione ao PATH do Windows
- Verifique: `java -version` e `javac -version`

### Erro: "Compilation failed"
- Verifique se todos os arquivos `.java` existem nas pastas esperadas
- Tente limpar com: `rmdir /s out` e compilar novamente

### Erro: "Permission denied" no banco de dados
- Verifique permissões de escrita na pasta `data/`
- Tente criar a pasta manualmente: `mkdir data`

### Jogo não inicia menu
- Verifique se a compilação foi bem-sucedida
- Verifique se `lib/sqlite-jdbc-*.jar` existe

## 📚 Arquitetura

### Padrões Utilizados
- **MVC**: Separação de modelo, visualização e controle
- **Repository Pattern**: Abstração de acesso a dados
- **Strategy Pattern**: Diferentes estratégias de IA para CPU

### Componentes Principais
- **GameEngine**: Orquestra a partida (turnos, verificação de vitória)
- **Board**: Tabuleiro 10x10 com navios
- **Player**: Classe base para Humano e CPU
- **CpuPlayer**: IA que escolhe alvos inteligentemente
- **MatchRepository**: Persistência de partidas
- **GameTester**: Testes automáticos

## 📝 Notas

- Sementes (seed) aleatórias podem ser fornecidas para reproduzir partidas
- O banco de dados persiste entre execuções
- CPUs usam algoritmos de targeting para melhorar estratégia
- Limite máximo de turnos: 500 (para evitar loops infinitos em testes)

## 👨‍💻 Desenvolvimento

Para adicionar funcionalidades:

1. **Novo tipo de jogador**: Estenda `Player` e implemente `chooseShot()`
2. **Nova estratégia de IA**: Implemente em `CpuPlayer`
3. **Novos dados**: Adicione tabelas em `DatabaseConnection`
4. **Novos testes**: Estenda `GameTester`

## 📄 Licença

Este projeto é fornecido como está para fins educacionais.

---

**Última atualização**: Maio 2026
