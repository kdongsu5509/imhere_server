package com.kdongsu5509.imhereuserservice.domain.user

data class User(
    var email: String,
    var nickname: String,
    var oauthProvider: OAuth2Provider,
    var role: UserRole
)