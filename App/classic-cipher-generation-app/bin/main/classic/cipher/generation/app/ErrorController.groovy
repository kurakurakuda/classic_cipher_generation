package classic.cipher.generation.app

class ErrorController {

    def index() {
        respond message: "Error"
    }

    def internalError() {
        def exception = request.exception.cause.cause
        ErrorMsg msg = new ErrorMsg(errorMessage: exception.message)
        response.status = 500
        respond error: msg
    }

    def badRequest() {
        def exception = request.exception.cause.cause
        ErrorMsg msg = new ErrorMsg(errorMessage: exception.message)
        response.status = 400
        respond error: msg
    }

}
