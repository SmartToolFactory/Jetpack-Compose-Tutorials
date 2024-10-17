package com.smarttoolfactory.tutorial3_1navigation

import kotlinx.serialization.Serializable


// Define a home route that doesn't take any arguments
@Serializable
object Home

// Define a profile route that takes an ID
@Serializable
data class Profile(val id: String)

@Serializable
object RouteA

@Serializable
object RouteB

@Serializable
object RouteC

@Serializable
object RouteD