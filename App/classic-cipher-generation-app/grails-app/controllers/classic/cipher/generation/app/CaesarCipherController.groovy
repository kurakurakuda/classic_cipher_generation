package classic.cipher.generation.app

import classic.cipher.generation.app.exception.BadRequestException

class CaesarCipherController {

    def caesarCipherService

    static allowedMethods = [encrypt: "POST", decrypt: "POST"]

    def index() {
        respond message: "Caesar Cipher"
    }

    CaesarRes encrypt(CaesarReq req) {
        log.info "Get request for encryption. Request: ${req.properties} "
        if (!req.validate()) {
            def errors = req.errors.allErrors.collect {
                message(error: it)
            }
            throw new BadRequestException(message: errors.toString())
        }
        respond caesarCipherService.encrypt(req)
    }

    CaesarRes decrypt(CaesarReq req) {
        log.info "Get request for decryption. Request: ${req.properties} "
        if (!req.validate()) {
            def errors = req.errors.allErrors.collect {
                message(error: it)
            }
            throw new BadRequestException(message: errors.toString())
        }
        respond caesarCipherService.decrypt(req)
    }
}
