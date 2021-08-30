package classic.cipher.generation.app

import classic.cipher.generation.app.exception.BadRequestException

class RailfenceController {

    def railfenceService

    static allowedMethods = [encrypt: "POST", decrypt: "POST"]

    def index() {
        respond message: "Railfence"
    }


    RailfenceRes encrypt(RailfenceReq req) {
        log.info "Get request for encryption. Request: ${req.properties} "
        if (!req.validate()) {
            def errors = req.errors.allErrors.collect {
                message(error: it)
            }
            throw new BadRequestException(message: errors.toString())
        }

        respond railfenceService.encrypt(req)
    }

    RailfenceRes decrypt(RailfenceReq req) {
        log.info "Get request for decryption. Request: ${req.properties} "
        if (!req.validate()) {
            def errors = req.errors.allErrors.collect {
                message(error: it)
            }
            throw new BadRequestException(message: errors.toString())
        }

        respond railfenceService.decrypt(req)
    }
}
