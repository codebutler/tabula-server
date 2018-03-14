package com.codebutler.tabulaserver

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.AuthenticationPipeline
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwtAuthentication
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

private val secret = System.getenv("JWT_SECRET") ?: "not-so-secret"

private val moshi: JsonAdapter<Any> = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter<Any>(Any::class.java)
        .indent("  ")

@Suppress("unused")
fun Application.main() {
    install(CallLogging)
    install(DefaultHeaders)
    install(StatusPages)
    install(Compression)

    routing {
        get("/") {
            call.respondText("it works!")
        }

        route("/scrape", HttpMethod.Get) {
            authentication {
                scraperAuth(environment)
            }
            handle {
                val url = call.request.queryParameters["url"]
                if (url != null) {
                    call.respondText(moshi.toJson(scrapePdf(url)), ContentType.Application.Json)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing url")
                }
            }
        }
    }
}

private fun AuthenticationPipeline.scraperAuth(environment: ApplicationEnvironment) {
    val issuer = environment.config.property("jwt.domain").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val realm = environment.config.property("jwt.realm").getString()
    val jwtVerifier = JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    jwtAuthentication(jwtVerifier, realm) { credential ->
        if (credential.payload.audience.contains(audience)) {
            JWTPrincipal(credential.payload)
        } else {
            null
        }
    }
}