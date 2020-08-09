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

        if(projectType == null || projectName == null)
            return responseCreator(handlerRequest, "We are missing a valid project name or type. Please try again.")

        val template = templates[projectType]
        return if(template == null)
            responseCreator(handlerRequest, "Unrecognized project type. Valid options are ${templates.keys.joinToString(",")}")
        else {
            val result = GithubApi.createRepository(template, projectName)
            responseCreator(handlerRequest, result)
        }
    }
}

class GithubApi{

    companion object {
        fun createRepository(template: Template, repoName: String) : String{

            val result = Fuel.post("https://api.github.com/repos/${template.owner}/${template.repo}/generate")
                    .header("Accept", "application/vnd.github.baptiste-preview+json")
                    .header("Authorization", "token $token")
                    .jsonBody("{ \"name\" : \"${repoName}\" }")
                    .also { println(it) }
                    .response().second

            return when(result.statusCode){
                201 -> "Success! The repository with name ${unclean(repoName)} has been created!"
                401 -> "Error with authorization. Have you already signed up for Github buddy?"
                422 -> "Error when creating the repository. Do you already have a repository with the same name? "
                else -> "Unknown error! Please try again later"
            }
        }
    }
}

data class Template(val owner: String, val repo: String)

val templates = mapOf<String,Template>(
        "java" to Template("Spring-Boot-Framework", "Spring-Boot-Application-Template"),
        "typescript" to Template("carsonfarmer", "ts-template")
)

private fun clean(value: String) : String = value.replace(" ", "_")
private fun unclean(value: String) : String = value.replace("_", " ")