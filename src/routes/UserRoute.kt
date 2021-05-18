package com.codingwithjks.routes

import com.codingwithjks.API_VERSION
import com.codingwithjks.auth.JwtService
import com.codingwithjks.auth.MySession
import com.codingwithjks.repository.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val USERS = "$API_VERSION/users"
const val LOGIN = "$API_VERSION/login"
const val CREATE = "$API_VERSION/create"

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(LOGIN)
class UserLoginRoute

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(CREATE)
class UserCreateRoute


@OptIn(KtorExperimentalLocationsAPI::class)
fun Route.users(
    db:UserRepository,
    jwt: JwtService,
    hashFunction : (String) -> String
){
    post<UserCreateRoute>{
        val parameter = call.receive<Parameters>()

        val password = parameter["password"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val name = parameter["name"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val email = parameter["email"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val hash = hashFunction(password)

        try {
            val newUser = db.createUser(email,name,hash)
            newUser?.id?.let {
                call.sessions.set(MySession(it))
                call.respondText(
                    jwt.generateToken(newUser),
                    status = HttpStatusCode.Created
                )
            }
        }catch (e:Throwable){
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    post<UserLoginRoute> {
        val data = call.receive<Parameters>()

        val password = data["password"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val email = data["email"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val hash = hashFunction(password)

        try{

            val currentUser = db.findUserByEmail(email)
            currentUser?.id?.let {
                if(currentUser.password == hash){
                    call.sessions.set(MySession(it))
                    call.respondText(jwt.generateToken(currentUser))
                }else{
                    call.respond(status = HttpStatusCode.BadRequest,
                        "problem retrieving user.. ")
                }
            }

        }catch (e:Throwable){
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }


}