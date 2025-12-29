package com.kdongsu5509.imhereuserservice.domain

data class User(
    var email: String,
    var oauthProvider: OAuth2Provider,
    var role: UserRole
)