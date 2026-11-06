# Decisões da refatoração — respostas diretas para o professor

**Como estava no Trabalho 3?** Havia duas entidades Room, dois DAOs e um ViewModel único (`HabitFlowViewModel`) que acessava os DAOs, guardava duas listas em `mutableStateOf` e chamava as consultas depois de inserção/edição/exclusão.

**O que mudou?** Os cinco componentes foram separados: `ui`, `data`, `data.local`, `data.repository`, `viewmodel`. O ViewModel único foi substituído por dois ViewModels, cada qual com um `UiState` e um `StateFlow`. Entraram repositórios por entidade e uma `ViewModelFactory`. As listas usam `Flow` diretamente dos DAOs. A navegação e os dados do usuário foram preservados.

**Por que dividir os pacotes?** `ui` apresenta e interage; `data.local` é o detalhe da persistência; `data.repository` abstrai operações; `viewmodel` conserva estado e ações de apresentação. Isso separa responsabilidades e facilita testes.

**Por que o Repository existe?** Para que `ViewModel` não dependa da tecnologia Room/DAO. O Repository é a única fonte de verdade e acesso à persistência para a camada acima.

**O que tem no UiState de cada tela e por quê?** Listas para desenhar itens, item selecionado para detalhes/edição, indicadores de carregamento e mensagem de erro. Assim uma única classe imutável descreve o estado da área.

**Dificuldade e correção:** a troca para `Flow<List<...>>` exige que consumidores deixem de esperar `List<...>` síncrona. O erro de compilação acontece se `HabitFlowViewModel` antigo permanecer; nesta versão ele foi totalmente removido e substituído.

**Preservação:** o schema de duas tabelas e o mesmo banco `habitflow3_database` foram mantidos (Room versão 1), sem migração destrutiva.

**Como comprovar?** Executar o projeto, adicionar/editar/excluir e finalizar/reabrir; tirar prints e gravar vídeo real. Testes automatizados escritos não são provas de sucesso até serem executados.


## Refinamento do estado de apresentação

Os formulários do cadastro e edição de hábitos passaram a usar `HabitoFormularioUiState`, mantido pelo `HabitosViewModel`. Os formulários de metas passaram a usar `MetaFormularioUiState` dentro de `MetasUiState` (cadastro e edição separados). As telas apenas coletam `StateFlow` e enviam eventos aos ViewModels. Estados puramente visuais como abertura de diálogos continuam com `remember`.

As coleções filtradas de hábitos ativos e concluídos e os totais de perfil/início passaram a ser propriedades derivadas dos `UiState`, preservando o banco e as regras de negócio.

Incluímos `FlowReativoRoomTest` para observar uma única coleta reativa durante operações de inserir, atualizar e remover. Esses testes precisam ser executados em emulador/dispositivo pelo Android Studio.

**Evidências pendentes de execução real:** anexar capturas e/ou vídeos do app funcionando e demonstrar reabertura com dados persistidos. Registrar no README as dificuldades efetivamente vividas pelos integrantes, sem inventá-las.
