package com.example.habitflow3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habitflow3.ui.screens.TelaCadastroHabito
import com.example.habitflow3.ui.screens.TelaDashboard
import com.example.habitflow3.ui.screens.TelaDetalheHabito
import com.example.habitflow3.ui.screens.TelaDetalheMeta
import com.example.habitflow3.ui.screens.TelaHistorico
import com.example.habitflow3.ui.screens.TelaInicial
import com.example.habitflow3.ui.screens.TelaMetas
import com.example.habitflow3.ui.screens.TelaPerfil
import com.example.habitflow3.viewmodel.HabitosViewModel
import com.example.habitflow3.viewmodel.MetasViewModel

// Mesmas nove rotas do HabitFlow3 original.
object Rotas {
    const val INICIO = "inicio"
    const val HABITOS = "habitos"
    const val CADASTRO_HABITO = "cadastro_habito"
    const val DETALHE_HABITO = "detalhe_habito/{id}"
    const val EDITAR_HABITO = "editar_habito/{id}"
    const val METAS = "metas"
    const val DETALHE_META = "detalhe_meta/{id}"
    const val HISTORICO = "historico"
    const val PERFIL = "perfil"
    fun detalheHabito(id: Int) = "detalhe_habito/$id"
    fun editarHabito(id: Int) = "editar_habito/$id"
    fun detalheMeta(id: Int) = "detalhe_meta/$id"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(habitos: HabitosViewModel, metas: MetasViewModel) {
    val navegacao = rememberNavController()
    val entrada by navegacao.currentBackStackEntryAsState()
    val rota = entrada?.destination?.route ?: Rotas.INICIO
    val principais = listOf(Rotas.INICIO, Rotas.HABITOS, Rotas.METAS, Rotas.HISTORICO, Rotas.PERFIL)
    val principal = rota in principais
    val avisos = remember { SnackbarHostState() }
    val estadoHabitos by habitos.uiState.collectAsStateWithLifecycle()
    val estadoMetas by metas.uiState.collectAsStateWithLifecycle()

    // Erros de banco/validação aparecem em Snackbar e são limpos depois da exibição.
    LaunchedEffect(estadoHabitos.erro) {
        val erro = estadoHabitos.erro
        if (erro != null) { avisos.showSnackbar(erro); habitos.limparErro() }
    }
    LaunchedEffect(estadoMetas.erro) {
        val erro = estadoMetas.erro
        if (erro != null) { avisos.showSnackbar(erro); metas.limparErro() }
    }

    val voltar: () -> Unit = { navegacao.popBackStack(); Unit }
    val abrirPrincipal: (String) -> Unit = { destino ->
        navegacao.navigate(destino) {
            popUpTo(Rotas.INICIO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    val titulo = when (rota) {
        Rotas.INICIO -> "HabitFlow"
        Rotas.HABITOS -> "Hábitos"
        Rotas.CADASTRO_HABITO -> "Novo hábito"
        Rotas.EDITAR_HABITO -> "Editar hábito"
        Rotas.DETALHE_HABITO -> "Detalhes do hábito"
        Rotas.METAS -> "Metas"
        Rotas.DETALHE_META -> "Detalhes da meta"
        Rotas.HISTORICO -> "Histórico"
        Rotas.PERFIL -> "Perfil"
        else -> "HabitFlow"
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (!principal) IconButton(onClick = voltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            if (principal) NavigationBar {
                val destinos = listOf(
                    Triple(Rotas.INICIO, "Início", Icons.Default.Home),
                    Triple(Rotas.HABITOS, "Hábitos", Icons.Default.List),
                    Triple(Rotas.METAS, "Metas", Icons.Default.Flag),
                    Triple(Rotas.HISTORICO, "Histórico", Icons.Default.History),
                    Triple(Rotas.PERFIL, "Perfil", Icons.Default.Person)
                )
                destinos.forEach { (destino, nome, icone) ->
                    NavigationBarItem(
                        selected = rota == destino,
                        onClick = { abrirPrincipal(destino) },
                        icon = { Icon(icone, contentDescription = nome) },
                        label = { Text(nome) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(avisos) }
    ) { espaco ->
        NavHost(
            navController = navegacao,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(espaco)
        ) {
            composable(Rotas.INICIO) {
                TelaInicial(
                    habitos = habitos,
                    metas = metas,
                    abrirCadastro = { navegacao.navigate(Rotas.CADASTRO_HABITO) },
                    abrirMetas = { abrirPrincipal(Rotas.METAS) },
                    abrirHistorico = { abrirPrincipal(Rotas.HISTORICO) }
                )
            }
            composable(Rotas.HABITOS) {
                TelaDashboard(
                    habitos = habitos,
                    abrirCadastro = { navegacao.navigate(Rotas.CADASTRO_HABITO) },
                    abrirDetalhe = { id -> navegacao.navigate(Rotas.detalheHabito(id)) }
                )
            }
            composable(Rotas.CADASTRO_HABITO) {
                LaunchedEffect(Unit) { habitos.prepararNovoHabito() }
                TelaCadastroHabito(
                    modelo = habitos,
                    modoEdicao = false,
                    aoSalvar = { habitos.salvarFormulario(voltar) }
                )
            }
            composable(Rotas.DETALHE_HABITO, arguments = listOf(navArgument("id") { type = NavType.IntType })) { tela ->
                val id = tela.arguments?.getInt("id") ?: 0
                TelaDetalheHabito(
                    id = id, modelo = habitos,
                    editar = { navegacao.navigate(Rotas.editarHabito(id)) },
                    voltar = voltar
                )
            }
            composable(Rotas.EDITAR_HABITO, arguments = listOf(navArgument("id") { type = NavType.IntType })) { tela ->
                val id = tela.arguments?.getInt("id") ?: 0
                LaunchedEffect(id) { habitos.selecionarHabito(id) }
                val encontrado = estadoHabitos.habitoSelecionado?.takeIf { it.id == id }
                if (encontrado != null) {
                    LaunchedEffect(id, encontrado.id) { habitos.prepararEdicaoHabito(encontrado) }
                    TelaCadastroHabito(
                        modelo = habitos,
                        modoEdicao = true,
                        aoSalvar = { habitos.salvarFormulario(voltar) }
                    )
                } else {
                    Text(if (estadoHabitos.carregandoDetalhe) "Carregando hábito..." else "Hábito não encontrado.")
                }
            }
            composable(Rotas.METAS) {
                TelaMetas(metas, abrirDetalhe = { id -> navegacao.navigate(Rotas.detalheMeta(id)) })
            }
            composable(Rotas.DETALHE_META, arguments = listOf(navArgument("id") { type = NavType.IntType })) { tela ->
                val id = tela.arguments?.getInt("id") ?: 0
                TelaDetalheMeta(id, metas, voltar)
            }
            composable(Rotas.HISTORICO) {
                TelaHistorico(habitos) { id -> navegacao.navigate(Rotas.detalheHabito(id)) }
            }
            composable(Rotas.PERFIL) { TelaPerfil(habitos, metas) }
        }
    }
}
