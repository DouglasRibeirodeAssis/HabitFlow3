# Roteiro de evidências — Trabalho 4

> Os prints e vídeos reais precisam ser capturados depois de executar o projeto.
> Não há imagens simuladas apresentadas como teste executado.

1. Abra a árvore de pacotes mostrando `ui`, `data`, `data/local`, `data/repository` e `viewmodel`.
2. Mostre os dois `Repository`, um por entidade, encapsulando os DAOs.
3. Mostre os métodos `buscarTodos()` retornando `Flow<List<...>>` nos dois DAOs.
4. Mostre `HabitosUiState`, `MetasUiState`, os dois `MutableStateFlow` privados e os `StateFlow` públicos.
5. Mostre a `ViewModelFactory` e a criação dos ViewModels em `MainActivity`.
6. No emulador: cadastre um hábito e uma meta; edite, marque/desmarque, registre dias, conclua/reabra hábito.
7. Feche o aplicativo por completo e abra-o novamente; filme que os registros continuam no Room.
8. Execute `:app:assembleDebug`, `:app:testDebugUnitTest` e `:app:connectedDebugAndroidTest` e registre o resultado real.
9. Inclua no README o link para vídeos e imagens do teste após realmente produzi-los.
