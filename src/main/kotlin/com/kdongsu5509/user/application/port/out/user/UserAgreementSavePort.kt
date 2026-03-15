package com.kdongsu5509.user.application.port.out.user

interface UserAgreementSavePort {
    fun saveAgreement(userEmail: String, termDefinitionId: Long)
    fun saveAgreements(userEmail: String, termDefinitionIds: List<Long>)
}