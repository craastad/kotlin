package kotlin

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.jvm.internal.Intrinsic

/**
 * This annotation indicates what exceptions should be declared by a function when compiled to a JVM method
 *
 * Example:
 *
 *      throws(javaClass<IOException>())
 *      fun readFile(name: String): String {...}
 *
 * will be translated to
 *
 *      String readFile(String name) throws IOException {...}
 */
Retention(RetentionPolicy.SOURCE)
public annotation class throws(vararg val exceptionClasses: Class<out Throwable>)

[Intrinsic("kotlin.javaClass.property")] public val <T> T.javaClass : Class<T>
    get() = (this as java.lang.Object).getClass() as Class<T>

[Intrinsic("kotlin.javaClass.function")] fun <reified T> javaClass() : Class<T> = null as Class<T>

Retention(RetentionPolicy.SOURCE)
public annotation class volatile

[Intrinsic("kotlin.synchronized")] public fun <R> synchronized(lock: Any, block: () -> R): R = block()

public fun <T : Annotation> T.annotationType() : Class<out T> =
    (this as java.lang.annotation.Annotation).annotationType() as Class<out T>
