package com.mock.application.routes

import com.mock.application.auth.principal.UserPrincipal
import com.mock.application.controller.UserController
import com.mock.data.database.entity.Note
import com.mock.data.model.base.WrapDataResponse
import com.mock.data.model.request.UserRequest
import com.mock.data.model.response.auth.UserResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoute() {

    val userController by inject<UserController>()

    route("/user") {
        get("/info") {
            val principal = call.principal<UserPrincipal>()
            principal?.let {
                val userId = principal.userId
                val response = userController.findUserById(userId = userId)
                call.respond(response)
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
            }

        }
        post("/create") {
            val request = call.receive<UserRequest>()
            val response = userController.createUser(request.username,request.password)
            call.respond(response)
        }
    }
}