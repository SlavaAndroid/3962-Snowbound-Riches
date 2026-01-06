package jp.co.tai.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoadingScreen

@Serializable
object ConnectScreen

@Serializable
object StartScreen

@Serializable
object LevelsScreen

@Serializable
data class SnowboundScreen(val level: Int)