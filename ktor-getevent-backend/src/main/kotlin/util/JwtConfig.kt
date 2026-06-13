package util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import model.Role
import model.User
import java.util.Date

class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expirationMs: Long = 86_400_000
) {
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(user: User): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", user.id)
        .withClaim("email", user.email)
        .withClaim("role", user.role.name)
        .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
        .sign(algorithm)

    fun verifier() = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}
