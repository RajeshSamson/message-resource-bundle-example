package com.techpastors

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router


@SpringBootApplication
class MessageResourceExampleApplication

fun main(args: Array<String>) {
    runApplication<MessageResourceExampleApplication>(*args) {
        addInitializers(
                beans {
                    bean {
                        var userDetailService = ref<UserDetailService>()
                        router {
                            contentType(MediaType.APPLICATION_JSON).nest {
                                GET("/user") { userDetailService.getUserDetail(it)}
                            }
                        }
                    }
                }
        )
    }
}

@Service
class UserDetailService(private val userDetails: UserDetails){

	fun getUserDetail(serverRequest: ServerRequest): ServerResponse{
		return ServerResponse.ok().body(userDetails.getEmployeeDetails())
	}
}

data class User(val userName: String, val age: Int, val email: String)

@Component
class UserDetails(private val messageSource: ResourceBundleMessageSource) {

	private val age: String = "user.age"
	private val email: String = "user.email"
	private val user: String = "user.name"

	fun getEmployeeDetails(): User {
		val locale = LocaleContextHolder.getLocale()
		val userName = messageSource.getMessage(user, null, locale)
        val userEmail = messageSource.getMessage(email, null, locale)
        val userAge = Integer.parseInt(messageSource.getMessage(age, null, locale))
        return User(userName = userName, age = userAge, email = userEmail)
    }
}


@Configuration
class Config {
    @Bean
    fun messageSource(): ResourceBundleMessageSource? {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("classpath:messages")
        messageSource.setCacheSeconds(10)
        return messageSource
    }
}
