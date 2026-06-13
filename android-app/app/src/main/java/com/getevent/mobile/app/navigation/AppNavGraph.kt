package com.getevent.mobile.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.getevent.mobile.app.model.Role
import com.getevent.mobile.app.ui.AppViewModel
import com.getevent.mobile.app.ui.admin.AdminDashboardScreen
import com.getevent.mobile.app.ui.admin.CrudEventScreen
import com.getevent.mobile.app.ui.admin.CrudLocationScreen
import com.getevent.mobile.app.ui.admin.ManageUsersScreen
import com.getevent.mobile.app.ui.admin.StatisticsScreen
import com.getevent.mobile.app.ui.boardmember.BoardDashboardScreen
import com.getevent.mobile.app.ui.boardmember.QrScannerScreen
import com.getevent.mobile.app.ui.boardmember.ReservationListScreen
import com.getevent.mobile.app.ui.login.AuthViewModel
import com.getevent.mobile.app.ui.login.LoginScreen
import com.getevent.mobile.app.ui.login.RegisterScreen
import com.getevent.mobile.app.ui.student.EventDetailScreen
import com.getevent.mobile.app.ui.student.MyReservationsScreen
import com.getevent.mobile.app.ui.student.MyTicketQrScreen
import com.getevent.mobile.app.ui.student.ProfileScreen
import com.getevent.mobile.app.ui.student.StudentHomeScreen
import com.getevent.mobile.app.utils.SessionManager

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val appViewModel: AppViewModel = viewModel()

    val role by authViewModel.role.collectAsState()
    val activeUser by SessionManager.user.collectAsState()

    fun Role.destination(): String =
        when (this) {
            Role.ADMIN -> AppDestinations.ADMIN_DASHBOARD
            Role.BOARD_MEMBER -> AppDestinations.QR_SCANNER
            Role.STUDENT -> AppDestinations.STUDENT_HOME
        }

    // ✅ KEEP ONLY LOGIN FLOW NAVIGATION
    LaunchedEffect(role) {
        val r = role ?: return@LaunchedEffect

        navController.navigate(r.destination()) {
            popUpTo(AppDestinations.LOGIN) { inclusive = true }
            launchSingleTop = true
        }

        authViewModel.consumeRole()
    }

    // ❌ FIX: remove automatic session navigation (CAUSES BLACK SCREEN)
    // LaunchedEffect(activeUser?.role) { ... }  <-- DELETE THIS

    NavHost(
        navController = navController,
        startDestination = AppDestinations.LOGIN
    ) {

        composable(AppDestinations.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                }
            )
        }

        composable(AppDestinations.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.STUDENT_HOME) {
            LaunchedEffect(Unit) { appViewModel.loadEvents() }

            StudentHomeScreen(
                viewModel = appViewModel,
                onEventClick = {
                    navController.navigate("${AppDestinations.EVENT_DETAIL_BASE}/$it")
                },
                onMyReservations = {
                    appViewModel.loadMyReservations()
                    navController.navigate(AppDestinations.MY_RESERVATIONS)
                },
                onProfile = {
                    navController.navigate(AppDestinations.PROFILE)
                }
            )
        }

        composable(
            route = AppDestinations.EVENT_DETAIL,
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L

            EventDetailScreen(
                viewModel = appViewModel,
                eventId = eventId,
                onReserve = { appViewModel.reserve(it) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.MY_RESERVATIONS) {
            MyReservationsScreen(
                viewModel = appViewModel,
                onTicket = {
                    navController.navigate("${AppDestinations.MY_TICKET_QR_BASE}/$it")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestinations.MY_TICKET_QR,
            arguments = listOf(navArgument("reservationId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getLong("reservationId") ?: 0L

            MyTicketQrScreen(
                viewModel = appViewModel,
                reservationId = reservationId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.PROFILE) {
            ProfileScreen(
                user = activeUser,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.BOARD_DASHBOARD) {
            BoardDashboardScreen(
                onScan = { navController.navigate(AppDestinations.QR_SCANNER) },
                onReservations = {
                    appViewModel.loadAllReservations()
                    navController.navigate(AppDestinations.RESERVATION_LIST)
                }
            )
        }

        composable(AppDestinations.QR_SCANNER) {
            QrScannerScreen(
                viewModel = appViewModel,
                onBack = { navController.popBackStack() },
                onOpenDashboard = {
                    navController.navigate(AppDestinations.BOARD_DASHBOARD)
                },
                onOpenReservations = {
                    appViewModel.loadAllReservations()
                    navController.navigate(AppDestinations.RESERVATION_LIST)
                }
            )
        }

        composable(AppDestinations.RESERVATION_LIST) {
            ReservationListScreen(
                viewModel = appViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onEventCrud = { navController.navigate(AppDestinations.CRUD_EVENT) },
                onLocationCrud = { navController.navigate(AppDestinations.CRUD_LOCATION) },
                onUsers = { navController.navigate(AppDestinations.MANAGE_USERS) },
                onReservations = {
                    appViewModel.loadAllReservations()
                    navController.navigate(AppDestinations.RESERVATION_LIST)
                },
                onStats = { navController.navigate(AppDestinations.STATISTICS) }
            )
        }

        composable(AppDestinations.CRUD_EVENT) {
            LaunchedEffect(Unit) { appViewModel.loadEvents() }
            CrudEventScreen(viewModel = appViewModel, onBack = { navController.popBackStack() })
        }

        composable(AppDestinations.CRUD_LOCATION) {
            CrudLocationScreen(viewModel = appViewModel, onBack = { navController.popBackStack() })
        }

        composable(AppDestinations.MANAGE_USERS) {
            ManageUsersScreen(viewModel = appViewModel, onBack = { navController.popBackStack() })
        }

        composable(AppDestinations.STATISTICS) {
            StatisticsScreen(viewModel = appViewModel, onBack = { navController.popBackStack() })
        }
    }
}