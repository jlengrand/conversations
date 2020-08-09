package nl.lengrand.conversations

import assistant.conversation.schema.HandlerRequest
import assistant.conversation.schema.HandlerResponse
import assistant.conversation.schema.Prompt
import assistant.conversation.schema.Simple
import org.springframework.web.bind.annotation.*

@RestController
class FulfillmentController(private val gitHubApi: GitHubService) {

    private final val createRepoAction = "create_github_repository"

    @PostMapping("/fulfillment")
    fun fulfillment(@RequestBody handlerRequest: HandlerRequest) : HandlerResponse {

        return when(handlerRequest.handler.name){
            createRepoAction -> gitHubApi.createNewRepository(handlerRequest)
            else -> {
                responseCreator(handlerRequest, "Sorry, we couldn't figure out what you wanted to do. Please try again later!")
            }
        }
    }
}

fun responseCreator(handlerRequest: HandlerRequest, response: String) : HandlerResponse {
    val simple = Simple()
    simple.text = response
    simple.speech = response

    val prompt = Prompt()
    prompt.firstSimple = simple

    val handlerResponse = HandlerResponse()
    handlerResponse.session = handlerRequest.session;
    handlerResponse.scene = handlerRequest.scene;
    handlerResponse.prompt = prompt

    return handlerResponse
}