package classic.cipher.generation.app

import classic.cipher.generation.app.exception.BadRequestException

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "500"(controller:"error", action: "badRequest", exception: BadRequestException )
        "500"(controller:"error", action: "internalError")
    }
}
