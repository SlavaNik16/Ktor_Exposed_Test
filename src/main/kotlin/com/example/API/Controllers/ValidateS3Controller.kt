package com.example.API.Controllers

import com.example.Models.Response.ErrorResponse
import com.example.Services.Authentication.AWS.AWSV4Auth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.security.MessageDigest
import java.util.TreeMap


fun Route.initValidateS3Controller() {
    fun generateHex(data:String):String{
        var digest  = MessageDigest.getInstance("SHA-256")
        digest.update(data.toByteArray(Charsets.UTF_8))
        var digests: ByteArray = digest.digest()
        return with(StringBuilder()){
            digests.forEach { b-> append(String.format("%02x", b)) }
            toString().lowercase()
        }
    }
    route("/ser") {
        get("/validate") {
            try {
                var message =call.parameters["message"]
                var postman = call.request.headers["x-amz-content-sha256"]
                var headers = TreeMap<String,String>()
                headers.put("host", "127.0.0.1:8080")
                var queryParametes = TreeMap<String,String>()
                queryParametes.put("message", message.toString())
                var resultMap = AWSV4Auth.Builder("AKIDEXAMPLE","wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")
                    .queryParametes(queryParametes)
                    .awsHeaders(headers)
                    .payload(null)
                    .debug()
                    .build()
                    .getHeaders()
                if(resultMap == null){
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse(403, "Недействительный запрос") )
                    return@get
                }
                var result = resultMap["Authorization"]
                var resultServer = call.request.headers["Authorization"]
                if(result == null || resultServer == null){
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse(403, "Недействительный запрос") )
                    return@get
                }
                call.respond(HttpStatusCode.OK,result + "\n" + resultServer + "\n" + message)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse(406, e.message ?: Constants.GENERAL))
            }
        }
    }
}