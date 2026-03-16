<<<<<<<< HEAD:src/main/kotlin/com/kdongsu5509/support/exception/BusinessException.kt
package com.kdongsu5509.support.exception
========
package com.kdongsu5509.user.support.exception
>>>>>>>> d7b9cc0345ce1535419ec55566096c1a808887e4:src/main/kotlin/com/kdongsu5509/user/support/exception/BusinessException.kt

open class BusinessException(
    val errorCode: BaseErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)
