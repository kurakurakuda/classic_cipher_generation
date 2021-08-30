package classic.cipher.generation.app

class CaesarReq {
    String text
    Integer shift

    static constraints = {
        text nullable: false, blank: false, size: 1..100, matches: /^[a-zA-Z]*$/
        shift nullable: false, blank: false, min:1, max:25
    }
}
