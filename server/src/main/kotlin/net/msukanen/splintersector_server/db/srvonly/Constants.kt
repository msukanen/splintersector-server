package net.msukanen.splintersector_server.db.srvonly

import net.msukanen.DEBUG_MODE
import net.msukanen.splintersector_server.model.UserRole

@Target(AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class FatallyInsecure

//TODO: figure out a better db name... x)
const val DATABASE_URL = "jdbc:mysql://msukanen.net:3306/sss_test"
@FatallyInsecure const val DATABASE_USER = "sss"
val DATABASE_PASSWORD by lazy {
    if (DEBUG_MODE) {"1234passthegas"}
    else {
        System.getenv("SPLNSECT_PWD") ?:
        @FatallyInsecure {
            println("<ERROR> SPLNSECT_PWD environment variable not set!")
            println("        Defaulting to hardcoded one... BEWARE!")
            "1234passthegas"
        }()
    }
}

// HMAC secret for auth0
val JWT_SECRET by lazy {
    if (DEBUG_MODE) {"not-very-secret-key"}
    else {
        System.getenv("SPLNSECT_KEY")?:
        @FatallyInsecure {
            println("<ERROR> SPLNSECT_KEY environment variable not set!")
            println("        Defaulting to hardcoded one... BEWARE!")
            "not-very-secret-key"
        }()
    }
}

const val DATABASE_TEST_USER = "Matti Meikalainen"
const val DATABASE_TEST_PWD = "1234"
val DATABASE_TEST_ROLE = UserRole.DM
