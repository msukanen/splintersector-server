package net.msukanen.splintersector_server.db.srvonly

import net.msukanen.splintersector_server.model.UserRole

@Target(AnnotationTarget.CLASS,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.LOCAL_VARIABLE,
        AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class FatallyInsecure

//TODO: figure out a better db name... x)
const val DATABASE_URL = "jdbc:mysql://localhost:3306/sss_test"
@FatallyInsecure
const val DATABASE_USER = "sss"
@FatallyInsecure
const val DATABASE_PASSWORD = "1234passthegas"
@FatallyInsecure
const val NOT_VERY_SECRET_KEY = "not-very-secret-key"

const val DATABASE_TEST_USER = "Matti Meikalainen"
const val DATABASE_TEST_PWD = "1234"
val DATABASE_TEST_ROLE = UserRole.DM
