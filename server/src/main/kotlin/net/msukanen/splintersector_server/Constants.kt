package net.msukanen.splintersector_server

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
