package classic.cipher.generation.app

import classic.cipher.generation.app.exception.BadRequestException

class KeyphraseController {

    def keyphraseService

    static allowedMethods = [encrypt: "POST", decrypt: "POST"]

    def index() {
        respond message: "Keyphrase"
    }

    KeyphraseRes encrypt(KeyphraseReq req) {
        log.info "Get request for encryption. Request: ${req.properties} "
        if (!req.validate()) {
            def errors = req.errors.allErrors.collect {
                message(error: it)
            }
            throw new BadRequestException(message: errors.toString())
        }

        respond keyphraseService.encrypt(req)
    }

    KeyphraseRes decrypt(KeyphraseReq req) {
        log.info "Get request for decryption. Request: ${req.properties} "
        if (!req.validate()) {
            def errors = req.errors.allErrors.collect {
                message(error: it)
            }
            throw new BadRequestException(message: errors.toString())
        }

        respond keyphraseService.decrypt(req)
    }
}
