package uk.ac.tees.mad.dealzone.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.tees.mad.dealzone.data.AuthRepository
import uk.ac.tees.mad.dealzone.ui.screens.AuthScreen
import uk.ac.tees.mad.dealzone.ui.screens.AuthViewModel
import uk.ac.tees.mad.dealzone.ui.screens.SavedCouponsScreen
import uk.ac.tees.mad.dealzone.ui.screens.SplashScreen
import uk.ac.tees.mad.dealzone.viewmodel.SavedCouponsViewModel


@Composable
fun DealZoneNavGraph(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(authRepository)
    )

    NavHost(
        navController    = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                checkLoginState = {
                    authViewModel.isLoggedIn()
                }
            )
        }

        composable(Routes.AUTH) {
            val uiState by authViewModel.uiState.collectAsState()

            AuthScreen(
                uiState       = uiState,
                onSignIn      = { email, password -> authViewModel.signIn(email, password) },
                onRegister    = { email, password -> authViewModel.register(email, password) },
                onAuthSuccess = {
                    authViewModel.resetState()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val app = context.applicationContext as uk.ac.tees.mad.dealzone.DealZoneApplication
            val appViewModel: uk.ac.tees.mad.dealzone.viewmodel.AppViewModel = viewModel(
                factory = uk.ac.tees.mad.dealzone.viewmodel.AppViewModel.Factory(app.repo, app.authRepository)
            )
            uk.ac.tees.mad.dealzone.ui.screens.HomeScreen(
                viewModel = appViewModel,
                onNavigateToSaved = { navController.navigate(uk.ac.tees.mad.dealzone.navigation.Routes.SAVED) }
            )
        }

        composable(Routes.SAVED) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val app = context.applicationContext as uk.ac.tees.mad.dealzone.DealZoneApplication
            val savedViewModel: SavedCouponsViewModel = viewModel(
                factory = SavedCouponsViewModel.Factory(app.repo, app.authRepository)
            )
            SavedCouponsScreen(viewModel = savedViewModel)
        }

        composable(Routes.SETTINGS) {
            androidx.compose.material3.Text("Settings — Coming Next")
        }
    }
}