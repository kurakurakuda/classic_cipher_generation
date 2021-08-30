package classic.cipher.generation.app

class KeyphraseReq {
    String text
    String phrase

    static constraints = {
        text nullable: false, blank: false, size: 1..100, matches: /^[a-zA-Z]*$/
        phrase nullable: false, blank: false, size: 1..10, matches: /^[a-zA-Z]*$/
    }
}
