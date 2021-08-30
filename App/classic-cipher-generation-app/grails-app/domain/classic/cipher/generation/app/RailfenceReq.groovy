package classic.cipher.generation.app

class RailfenceReq {
    String text
    Integer count

    static constraints = {
        text nullable: false, blank: false, size: 1..100, matches: /^[a-zA-Z]*$/
        count nullable: false, blank: false, min:2, max:5
    }
}
