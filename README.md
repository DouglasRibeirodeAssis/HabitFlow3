# HabitFlow3 — Trabalho 3: Persistência de Dados com Room

**Disciplina:** Desenvolvimento de Aplicativos Móveis  
**Projeto:** HabitFlow3  
**Integrante:** Douglas Ribeiro de Assis

> **Status das evidências:** a implementação e o histórico técnico estão descritos abaixo. Há registros reais de uma versão intermediári, principalmente o vídeo em que um registro continua salvo após fechar completamente e reabrir o aplicativo. Não apresentamos testes não executados como se já estivessem aprovados.

## 1. Apresentação do aplicativo

O **HabitFlow3** é um aplicativo Android para organizar hábitos e metas. O usuário pode criar hábitos, informar categoria, horário, data de início e meta de dias, registrar dias realizados, editar registros e concluir hábitos. A tela **Histórico** mostra os hábitos concluídos com a data de início, data de conclusão e duração em dias corridos. Há também uma área de metas com cadastro, edição, marcação de conclusão e exclusão, além de um perfil com o resumo das informações.

O projeto foi desenvolvido em **Kotlin**, com telas em **Jetpack Compose**, navegação em **Navigation Compose** e armazenamento **local e persistente** com **Room/SQLite**. Ele usa o pacote `com.example.habitflow3` e banco `habitflow3_database`, com `@Database(version = 1)` por se tratar de um novo projeto.

## 2. Como o projeto estava no Trabalho 2 e o que mudou no Trabalho 3?

No **Trabalho 2**, o objetivo era transformar telas estáticas em um aplicativo funcional: navegar entre telas, cadastrar e remover itens, mostrar listas e abrir detalhes. Conforme o enunciado daquela etapa, os dados podiam ficar apenas **na memória**, por exemplo com `mutableStateListOf`. Isso significa que a lista podia mudar durante o uso, mas seu conteúdo não estava garantido depois do fechamento do aplicativo.

No **Trabalho 3**, o objetivo passou a ser armazenar os dados de verdade. Para isso, estruturamos o HabitFlow3 com duas tabelas Room, duas interfaces DAO e uma classe central de banco de dados. As telas deixaram de depender de uma lista de dados fictícios como fonte de verdade e passaram a consultar o banco. Quando um hábito ou uma meta é cadastrado, editado ou removido, a operação é feita no Room e a lista da interface é recarregada.

Também mantivemos os recursos de navegação e acrescentamos um **Histórico de hábitos concluídos**. Os campos `dataInicio`, `dataConclusao` e `diasRealizados` permitem mostrar o intervalo de realização e o progresso de cada hábito.

| Aspecto | Trabalho 2 | HabitFlow3 / Trabalho 3 |
|---|---|---|
| Fonte dos dados | Memória do aplicativo | Tabelas locais do Room |
| Listas | Atualizadas em tempo de execução | Carregadas por consultas DAO |
| Cadastro e exclusão | Efeito na lista em memória | Inserção e exclusão persistentes |
| Detalhes | Informações da lista em memória | Consulta `buscarPorId(id)` |
| Fechar o aplicativo | Dados podiam ser perdidos | Registros são gravados no banco local |
| Histórico | Não era exigido | Concluídos com início, fim e duração |

## 3. Entities, DAOs e Database: quais foram criados e por quê?

### 3.1 `HabitoEntity.kt`

A entidade `HabitoEntity`, associada à tabela `habitos`, contém:

| Campo | Tipo | Motivo |
|---|---|---|
| `id` | `Int`, chave primária gerada automaticamente | Identificar cada hábito sem confundir registros semelhantes |
| `nome` | `String` | Mostrar o nome do hábito cadastrado |
| `categoria` | `String` | Organizar os hábitos e permitir filtragem |
| `horario` | `String` | Guardar o horário planejado |
| `metaDias` | `Int` | Definir o objetivo em quantidade de dias |
| `dataInicio` | `String` | Guardar quando o hábito começou |
| `dataConclusao` | `String?` | Guardar quando terminou; `null` indica hábito em andamento |
| `diasRealizados` | `Int` | Registrar e mostrar o progresso |

As datas são armazenadas como texto no formato ISO (`aaaa-MM-dd`) e apresentadas ao usuário como `dd/MM/aaaa`. Esse formato permite o uso das funções de data do Kotlin sem exigir um conversor de tipos do Room para estes campos.

### 3.2 `MetaEntity.kt`

A entidade `MetaEntity`, associada à tabela `metas`, contém:

| Campo | Tipo | Motivo |
|---|---|---|
| `id` | `Int`, chave primária gerada automaticamente | Identificar cada meta |
| `titulo` | `String` | Informar o objetivo |
| `descricao` | `String` | Dar mais detalhes sobre a meta |
| `prazo` | `String` | Registrar uma data prevista, quando informada |
| `concluida` | `Boolean` | Distinguir metas pendentes e concluídas |

Escolhemos duas entidades porque **hábitos** e **metas** são informações relacionadas à organização pessoal, mas possuem campos e regras de funcionamento diferentes.

### 3.3 `HabitoDao.kt` e `MetaDao.kt`

Cada entidade possui seu próprio DAO. Ambos incluem as quatro operações principais, com métodos `suspend`:

- `@Insert`: `inserir(...)` — grava um novo registro.
- `@Query`: `buscarTodos()` — recupera os registros para as listas.
- `@Update`: `atualizar(...)` — salva modificações num registro existente.
- `@Delete`: `remover(...)` — exclui o registro correspondente.

Também adicionamos consultas que ajudam a navegação e a filtragem:

- **Hábitos:** `buscarPorId(id)` para mostrar a tela de detalhes; `buscarPorCategoria(categoria)` para filtrar por categoria.
- **Metas:** `buscarPorId(id)` para mostrar a tela de detalhes; `buscarPorStatus(concluida)` para consultar metas pendentes ou finalizadas.

As consultas por categoria e por status **existem nos DAOs**, embora as telas principais atuais façam o filtro de ativos/concluídos a partir dos resultados de `buscarTodos()`.

### 3.4 `AppDatabase.kt`

Criamos a classe `AppDatabase`, anotada com `@Database(entities = [HabitoEntity::class, MetaEntity::class], version = 1, exportSchema = false)`. Ela reúne `habitoDao()` e `metaDao()` e usa um **Singleton**, evitando criar várias instâncias do banco durante o funcionamento do aplicativo.

O banco é local, chamado `habitflow3_database`. Por ser um projeto novo, começa diretamente na **versão 1**; não há migração de versões antigas nesse código.

## 4. Como os dados chegaram do Room às telas?

O `HabitFlowViewModel.kt` concentra as operações usadas pelas telas. Ele consulta os DAOs e mantém `habitos` e `metas` como `mutableStateOf<List<...>>`, que servem para **exibir o estado atual**, não como banco de dados.

Quando uma tela é aberta, `LaunchedEffect(Unit)` chama as funções de carregamento. O `ViewModel` usa `withContext(Dispatchers.IO)` para fazer as operações de banco fora da thread principal. Após salvar, atualizar ou excluir, ele chama novamente a consulta da lista correspondente.

Nas telas de detalhes, o item é buscado pelo **ID recebido na navegação**, através de `buscarHabitoPorId(id)` ou `buscarMetaPorId(id)`, em vez de assumir que o primeiro item da lista é o correto.

## 5. Qual foi a dificuldade de sair das listas em memória para o Room?

A principal mudança de lógica foi separar **o dado armazenado** do **estado mostrado na tela**. Em uma lista exclusivamente em memória, basta alterar a coleção visível. Com o Room, precisamos gravar ou modificar o registro no DAO e depois atualizar o estado do `ViewModel` para a interface refletir a mudança.

Também foi necessário controlar corretamente os **IDs**, porque os detalhes e as edições precisam carregar o registro certo; executar operações com `suspend` e `Dispatchers.IO`; e tratar dados que representam o andamento de um hábito, como a conclusão e a contagem de dias.

**Problemas observados durante as tentativas anteriores:** houve uma versão que fechava inesperadamente após abrir e houve erro de nome de recurso do Android causado por espaço em um nome inválido. Em vez de afirmar uma causa não comprovada para o fechamento, registramos que refizemos a estrutura em um projeto limpo `HabitFlow3`, com pacote e banco próprios, e conferimos os nomes dos recursos. Essas ocorrências se referem às versões de preparação, não a um teste de persistência aprovado da versão atual.

## 6. Teste de persistência: adicionar, fechar, reabrir e continuar lá

1. Abrir o HabitFlow3 e acessar **Hábitos**.
2. Cadastrar um hábito com nome reconhecível, por exemplo, **Ler 20 minutos**, e confirmar que ele aparece na lista.
3. Abrir os detalhes e mostrar o nome, a categoria, a data de início e a meta de dias.
4. Fechar e abrir novamente o HabitFlow3, acessar **Hábitos** e abrir o mesmo registro.
5. Mostrar que os dados continuam iguais; se possível, editar a categoria, fechar e abrir novamente para demonstrar que a atualização também é persistente.

Vídeo em anexo no como "teste."4"

## 7. Organização do código

```text
HabitFlow3/
├── app/src/main/java/com/example/habitflow3/
│   ├── data/
│   │   ├── AppDatabase.kt
│   │   ├── HabitoEntity.kt
│   │   ├── HabitoDao.kt
│   │   ├── MetaEntity.kt
│   │   ├── MetaDao.kt
│   │   └── Datas.kt
│   ├── ui/
│   │   ├── HabitFlowViewModel.kt
│   │   ├── AppNavigation.kt
│   │   ├── screens/
│   │   └── theme/
│   └── MainActivity.kt
├── app/src/androidTest/.../PersistenciaRoomTest.kt
├── docs/
│   ├── evolucao/
│   ├── evidencias/
│   ├── videos/
│   └── ROTEIRO_EVIDENCIAS.md
└── README.md
```

## 8. Como executar o projeto

**Ambiente utilizado no desenvolvimento:** Android Studio 2025.3.1, JDK 17, SDK 35, Gradle 8.11.1 e Android Gradle Plugin 8.9.2.

1. Abrir a **pasta raiz `HabitFlow3`** no Android Studio, não apenas `app`.
2. Configurar o **Gradle JDK 17** e aguardar o Gradle Sync.
3. Se o Gradle Wrapper ainda não estiver completo, executar `PREPARAR_WINDOWS.bat`, conforme a instalação do projeto.
4. Selecionar um emulador ou dispositivo com **Android 8.0 / API 26 ou superior**.
5. Executar a configuração `app`.

O aplicativo não exige login nem internet para usar suas funcionalidades locais após a instalação. Para avaliar a persistência, **não desinstalar o aplicativo ou limpar seus dados** entre as duas aberturas, pois essas 
