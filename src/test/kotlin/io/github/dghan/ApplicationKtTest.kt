package io.github.dghan

import io.ktor.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document

@ExtendWith(RestDocumentationExtension::class)
class ApplicationKtTest {
    private lateinit var embeddedServer: ApplicationEngine

    private lateinit var spec: RequestSpecification

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        embeddedServer = embeddedServer(
            factory = Netty,
            port = 9000,
            module = Application::module
        ).apply { start() }
        this.spec = RequestSpecBuilder()
            .addFilter(
                RestAssuredRestDocumentation.documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(
                        Preprocessors.prettyPrint()
                    )
                    .withResponseDefaults(
                        Preprocessors.prettyPrint()
                    )
            )
            .build()
    }

    @AfterEach
    fun unset() {
        embeddedServer.stop(1000, 1000)
    }

    @Test
    fun `id가 1인 사용자를 조회할 수 있다`() {
        given(this.spec)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .filter(document("get-account"))
            .`when`()
            .port(9000)
            .get("/accounts/1")
            .then()
            .assertThat()
            .statusCode(200)
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
    }

    @Test
    fun `사용자 목록을 조회할 수 있다`() {
        given(this.spec)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .filter(document("get-accounts"))
            .`when`()
            .port(9000)
            .get("/accounts")
            .then()
            .assertThat()
            .statusCode(200)
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
    }

    @Test
    fun `사용자를 등록할 수 있다`() {
        given(this.spec)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .filter(document("create-account"))
            .body(Account(1, "name"))
            .`when`()
            .port(9000)
            .post("/accounts")
            .then()
            .assertThat()
            .statusCode(200)
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
    }

    @Test
    fun `id가 1인 사용자를 삭제할 수 있다`() {
        given(this.spec)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .filter(document("delete-account"))
            .`when`()
            .port(9000)
            .delete("/accounts/1")
            .then()
            .assertThat()
            .statusCode(200)
            .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
    }
}