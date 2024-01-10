package io.github.mrgsrylm.skso.ui.navigation

interface IAppNavDest {
    val route: String
}

object AppSplash : IAppNavDest {
    override val route = "splash"
}

object AppHome : IAppNavDest {
    override val route = "home"
}

object AppLog : IAppNavDest {
    override val route = "log"
}

object AppNotification : IAppNavDest {
    override val route = "notification"
}