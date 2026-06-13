import api.ApiTestClient
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun rootReturnsApiInfo() = testApplication {
        environment { config = TestFixtures.testConfig() }
        application { module() }
        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }

    @Test
    fun apiDocsEndpoint() = testApplication {
        environment { config = TestFixtures.testConfig() }
        application { module() }
        assertEquals(HttpStatusCode.OK, client.get("/api").status)
    }

    @Test
    fun loginWithSeededAdmin() = ApiTestClient.runApiTest(seedAdmin = true) {
        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"admin@getevent.local","password":"admin123"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
