package nl.lengrand.conversations

import assistant.conversation.schema.HandlerRequest
import assistant.conversation.schema.HandlerResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.springframework.stereotype.Service

private const val PROJECT_NAME = "project_name_slot"
private const val PROJECT_TYPE = "project_type_slot"
private const val token = "test-will-not-work"

@Service
class GitHubService {

    fun createNewRepository(handlerRequest: HandlerRequest) : HandlerResponse {

        val params = handlerRequest.session.params.additionalProperties
        val projectType = params[PROJECT_TYPE].toString().toLowerCase()
        val projectName = clean(params[PROJECT_NAME].toString())

        val template = templates[projectType] // TODO: handle when no template found

        GithubApi.createRepository(template!!, projectName)

        return responseCreator(handlerRequest, "creating new repository!")
    }
}

class GithubApi{

    companion object {
        // POST /repos/{template_owner}/{template_repo}/generate
        fun createRepository(template: Template, repoName: String) : String{

            val result = Fuel.post("https://api.github.com/repos/${template.owner}/${template.repo}/generate")
                    .header("Accept", "application/vnd.github.baptiste-preview+json")
                    .header("Authorization", "token ${token}")
                    .jsonBody("{ \"name\" : \"${repoName}\" }")
                    .also { println(it) }
                    .response()

            println("#####")
            println(result)

            return "Success"
        }
    }
}

data class Template(val owner: String, val repo: String)

val templates = mapOf<String,Template>(
        "java" to Template("Spring-Boot-Framework", "Spring-Boot-Application-Template"),
        "typescript" to Template("carsonfarmer", "ts-template")
)

private fun clean(value: String) : String = value.replace(" ", "_")