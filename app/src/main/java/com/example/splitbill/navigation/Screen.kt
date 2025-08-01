package com.example.splitbill.navigation

import kotlinx.serialization.Serializable

interface NavRoute {
    val route: String
}

@Serializable
data object HomeRoute : NavRoute {
    override val route: String = "HomeRoute"
}

@Serializable
data object AddEventRoute : NavRoute {
    override val route: String = "AddEventRoute"
}

@Serializable
data class EventRoute(val billId: Long) : NavRoute {
    override val route: String = "Event/$billId"
}
