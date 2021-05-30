package com.codingwithjks.routes

import com.codingwithjks.API_VERSION
import com.codingwithjks.auth.JwtService
import com.codingwithjks.auth.MySession
import com.codingwithjks.repository.TodoRepository
import com.codingwithjks.repository.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val USER = "$API_VERSION/user"
const val LOGIN = "$API_VERSION/login"
const val CREATE = "$API_VERSION/create"

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(LOGIN)
class UserLoginRoute

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(CREATE)
class UserCreateRoute

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(USER)
class UserRoute

@OptIn(KtorExperimentalLocationsAPI::class)
fun Route.users(
    db:UserRepository,
    todoDb:TodoRepository,
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

    delete<UserRoute>{
        val user = call.sessions.get<MySession>()?.let {
              db.findUser(it.userId)
        }

        if(user == null){
            call.respondText("problem getting user",status = HttpStatusCode.BadRequest)
        }

        try {
            user?.id?.let { it1 -> todoDb.deleteAllTodoByUserId(it1) }
            val isDelete = user?.id?.let { it1 -> db.deleteUser(it1) }
            if(isDelete == 1){
                call.respond(user)
            }else{
                call.respondText("something went wrong..",status = HttpStatusCode.BadRequest)
            }
        }catch (e:Throwable){
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems deleting User")
        }
    }

    put<UserRoute>{
        val parameters = call.receive<Parameters>()
        val user = call.sessions.get<MySession>()?.let {
            db.findUser(it.userId)
        }

        val name = parameters["name"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val email = parameters["email"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val password = parameters["password"] ?: return@put call.respondText(
            "missing fields",
            status = HttpStatusCode.Unauthorized
        )
        val hash = hashFunction(password)

        try {

            val isUpdated = user?.id?.let { it1 -> db.updateAllData(it1,name,email,hash) }

            if(isUpdated == 1){
                val updated = db.findUser(user.id)
                updated?.id?.let {
                    call.respond(updated)
                }
            }else{
                call.respond("something went wrong..")
            }

        }catch (e:Throwable){
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems deleting User")
        }
    }

    patch<UserRoute>{
        val parameters = call.receive<Parameters>()
        val user = call.sessions.get<MySession>()?.let {
            db.findUser(it.userId)
        }

        val name = parameters["name"] ?: "${user?.name}"

        val email = parameters["email"] ?: "${user?.email}"

        val password = parameters["password"] ?: "${user?.password}"

        val hash = hashFunction(password)

        try {
            val isUpdated = user?.id?.let { it1 -> db.updateAllData(it1,name,email,hash) }

            if(isUpdated == 1){
                val updated = db.findUser(user.id)
                updated?.id?.let {
                    call.respond(updated)
                }
            }else{
                call.respond("something went wrong..")
            }
        }catch (e:Throwable){
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems deleting User")
        }
    }

}