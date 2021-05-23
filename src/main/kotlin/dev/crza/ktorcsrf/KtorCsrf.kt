package dev.crza.ktorcsrf

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class KtorCsrf(configuration: Configuration) {
    private suspend fun intercept(
        content: PipelineContext<Unit, ApplicationCall>,
        configuration: Configuration,
        manager: StatelessHmacNonceManager
    ) {
        val token: String?
        when (content.context.request.httpMethod) {
            HttpMethod.Get, HttpMethod.Head, HttpMethod.Options -> {
                token = content.context.request.cookies[configuration.cookieName]
            }
            else -> {
                token = content.context.request.cookies[configuration.cookieName]
                if (token != null) {
                    val ok = manager.verifyNonce(token)
                    if (!ok) {
                        content.context.respond(HttpStatusCode.Forbidden)
                    }
                } else {
                    content.context.respond(HttpStatusCode.Forbidden)
                }
            }
        }

        content.context.response.cookies.append(
            Cookie(
                configuration.cookieName,
                manager.newNonce(),
                CookieEncoding.URI_ENCODING,
                configuration.expiration.toInt(),
                null,
                configuration.cookieDomain,
                configuration.cookiePath,
                configuration.cookieSecure,
                configuration.cookieHTTPOnly
            )
        )

    }

    class Configuration {


        // Name of the session cookie. This cookie will store session key.
        // Optional. Default value "csrf_".
        var cookieName = "csrf_"

        // Domain of the CSRF cookie.
        // Optional. Default value "".
        var cookieDomain = ""

        // Path of the CSRF cookie.
        // Optional. Default value "".
        var cookiePath = ""

        // Indicates if CSRF cookie is secure.
        // Optional. Default value false.
        var cookieSecure = false

        // Indicates if CSRF cookie is HTTP only.
        // Optional. Default value false.
        var cookieHTTPOnly = false


        // Expiration is the duration before csrf token will expire
        //
        // Optional. Default: 3600000 ms
        var expiration: Long = 3600000


        // Secret key generate CSRF token
        // Required
        var secretKey: ByteArray? = null


    }

    companion object Feature :
        ApplicationFeature<ApplicationCallPipeline, KtorCsrf.Configuration, KtorCsrf> {
        override val key = AttributeKey<KtorCsrf>("dev.crza.ktorcsrffeature")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): KtorCsrf {
            val configuration = KtorCsrf.Configuration().apply(configure)

            val feature = KtorCsrf(configuration)
            if (configuration.secretKey == null) {
                throw Exception("Secret key must be configured!")
            } else {
                val manager =
                    StatelessHmacNonceManager(configuration.secretKey!!, "HmacSHA256", configuration.expiration)
                pipeline.intercept(ApplicationCallPipeline.Call) {
                    feature.intercept(this, configuration, manager)
                }
                return feature
            }

        }
    }
}