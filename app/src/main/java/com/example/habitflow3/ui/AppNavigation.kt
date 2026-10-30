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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import com.example.habitflow3.ui.screens.executarAcao
import kotlinx.coroutines.launch

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
fun AppNavigation(modelo: HabitFlowViewModel) {
    val navegacao = rememberNavController()
    val estado by navegacao.currentBackStackEntryAsState()
    val rota = estado?.destination?.route ?: Rotas.INICIO
    val rotasPrincipais = listOf(Rotas.INICIO, Rotas.HABITOS, Rotas.METAS, Rotas.HISTORICO, Rotas.PERFIL)
    val principal = rota in rotasPrincipais
    val escopo = rememberCoroutineScope()
    val avisos = remember { SnackbarHostState() }
    val mostrarErro: (String) -> Unit = { mensagem -> escopo.launch { avisos.showSnackbar(mensagem) } }
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
                    if (!principal) {
                        IconButton(onClick = voltar) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (principal) {
                NavigationBar {
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
            }
        },
        snackbarHost = { SnackbarHost(avisos) }
    ) { espacamento ->
        NavHost(
            navController = navegacao,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(espacamento)
        ) {
            composable(Rotas.INICIO) {
                TelaInicial(
                    modelo = modelo,
                    abrirCadastro = { navegacao.navigate(Rotas.CADASTRO_HABITO) },
                    abrirMetas = { abrirPrincipal(Rotas.METAS) },
                    abrirHistorico = { abrirPrincipal(Rotas.HISTORICO) }
                )
            }
            composable(Rotas.HABITOS) {
                TelaDashboard(
                    modelo = modelo,
                    abrirCadastro = { navegacao.navigate(Rotas.CADASTRO_HABITO) },
                    abrirDetalhe = { id -> navegacao.navigate(Rotas.detalheHabito(id)) }
                )
            }
            composable(Rotas.CADASTRO_HABITO) {
                TelaCadastroHabito(
                    existente = null,
                    modoEdicao = false,
                    aoSalvar = { habito ->
                        escopo.executarAcao(mostrarErro) {
                            modelo.salvarHabito(habito)
                            voltar()
                        }
                    },
                    aoErro = mostrarErro
                )
            }
            composable(
                route = Rotas.DETALHE_HABITO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                TelaDetalheHabito(
                    id = id,
                    modelo = modelo,
                    editar = { navegacao.navigate(Rotas.editarHabito(id)) },
                    voltar = voltar,
                    aoErro = mostrarErro
                )
            }
            composable(
                route = Rotas.EDITAR_HABITO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                LaunchedEffect(id) { modelo.carregarHabitos() }
                val habito = modelo.habitos.firstOrNull { it.id == id }
                if (habito != null) {
                    TelaCadastroHabito(
                        existente = habito,
                        modoEdicao = true,
                        aoSalvar = { modificado ->
                            escopo.executarAcao(mostrarErro) {
                                modelo.salvarHabito(modificado)
                                voltar()
                            }
                        },
                        aoErro = mostrarErro
                    )
                } else {
                    Text("Carregando hábito...")
                }
            }
            composable(Rotas.METAS) {
                TelaMetas(
                    modelo = modelo,
                    abrirDetalhe = { id -> navegacao.navigate(Rotas.detalheMeta(id)) },
                    aoErro = mostrarErro
                )
            }
            composable(
                route = Rotas.DETALHE_META,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                TelaDetalheMeta(id = id, modelo = modelo, voltar = voltar, aoErro = mostrarErro)
            }
            composable(Rotas.HISTORICO) {
                TelaHistorico(modelo = modelo, abrirDetalhe = { id -> navegacao.navigate(Rotas.detalheHabito(id)) })
            }
            composable(Rotas.PERFIL) {
                TelaPerfil(modelo)
            }
        }
    }
}
