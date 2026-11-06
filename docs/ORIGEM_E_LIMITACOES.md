# Origem, alterações e limitações de validação

- Fonte de referência pública: https://github.com/DouglasRibeirodeAssis/HabitFlow3 (branch master, consulta em novembro de 2026).
- A versão foi **reconstituída como um projeto Android integral** com base nos arquivos públicos acessíveis (Gradle, entidades, DAOs, banco, ViewModel antigo, navegação e telas). Não é um clone binário integral do ZIP do GitHub: o ambiente não permitiu baixar diretamente o repositório como arquivo compactado.
- As funcionalidades e as nove rotas originais foram reproduzidas. A arte do logotipo/ícone foi recriada como um vetor simples; o vídeo `teste.mp4` original não integra este pacote.
- O binário oficial `gradle-wrapper.jar` é obtido pelo `PREPARAR_WINDOWS.bat` conforme a estratégia do próprio projeto original (o script verifica SHA-256). Todo o código-fonte e os arquivos de configuração necessários estão dentro do ZIP.
- Esta entrega **não** afirma teste de compilação Android realizado: são necessários Android SDK/Gradle e execução local do projeto.
- O PDF `trabalho 4.pdf` do professor é a fonte dos critérios arquiteturais; os requisitos de entrega referentes a prints reais e publicação no mesmo GitHub só podem ser concluídos após rodar o app e registrar os resultados.
