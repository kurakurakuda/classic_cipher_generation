package classic.cipher.generation.app

import classic.cipher.generation.app.constants.Alphabetic
import classic.cipher.generation.app.constants.CipherType
import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult
import groovy.json.*
import grails.plugin.redis.*

@Transactional
class KeyphraseService {

    def  dataService
    def  redisService

    private static final int REDIS_EXPIRE_TIME = 60
    private static final String REDIS_KEY_EN = "kp:e"
    private static final String REDIS_KEY_DE = "kp:d"

    KeyphraseRes encrypt(KeyphraseReq req) {
        log.info "Start encryption for ${req.properties} "

        if (redisService."$REDIS_KEY_EN:$req.text:$req.phrase" != null) {
            return new JsonSlurper().parseText(redisService."$REDIS_KEY_EN:$req.text:$req.phrase")
        }

        GroovyRowResult result = dataService.getKeyPhraseByPlain(req.text, req.phrase)
        if (result != null) {
            return setCache("$REDIS_KEY_EN:$req.text:$req.phrase", convertFromRowData(result))
        }
        KeyphraseRes res = new KeyphraseRes().tap {
            plain = req.text
            type = CipherType.KEY_PHRASE
            phrase = req.phrase
            crypto = ""
        }
        String key = generateKeyPhrase(req.phrase)
        for (i in 0..req.text.size()-1) {
            String s = req.text[i]
            int idx = Alphabetic.UPPER.indexOf(s.toUpperCase())
            String e = s.toUpperCase() == s ? key[idx] : key[idx].toLowerCase()
            res.crypto = res.crypto + e
        }
        dataService.insertKeyphraseData(res)
        setCache("$REDIS_KEY_EN:$req.text:$req.phrase", res)
        return res
    }

    KeyphraseRes decrypt(KeyphraseReq req) {
        log.info "Start decryption for ${req.properties} "

        if (redisService."$REDIS_KEY_DE:$req.text:$req.phrase" != null) {
            return new JsonSlurper().parseText(redisService."$REDIS_KEY_DE:$req.text:$req.phrase")
        }

        GroovyRowResult result = dataService.getKeyPhraseByEncrypt(req.text, req.phrase)
        if (result != null) {
            return setCache("$REDIS_KEY_DE:$req.text:$req.phrase", convertFromRowData(result))
        }
        KeyphraseRes res = new KeyphraseRes().tap {
            plain = ""
            type = CipherType.KEY_PHRASE
            phrase = req.phrase
            crypto = req.text
        }
        String key = generateKeyPhrase(req.phrase)
        for (i in 0..req.text.size()-1) {
            String s = req.text[i]
            int idx = key.indexOf(s.toUpperCase())
            String o = s.toUpperCase() == s ? Alphabetic.UPPER[idx] : Alphabetic.LOWER[idx]
            res.plain = res.plain + o
        }
        dataService.insertKeyphraseData(res)
        setCache("$REDIS_KEY_DE:$req.text:$req.phrase", res)
        return res
    }

    private String generateKeyPhrase(String phrase) {
        String originalKey = phrase.toUpperCase() + Alphabetic.UPPER
        String key = ""
        String used = ""
        for (i in 0..originalKey.size()-1) {
            if (used.indexOf(originalKey[i]) == -1) {
                key = key + originalKey[i]
            }
            used = used + originalKey[i]
        }
        return key
    }

    private KeyphraseRes setCache(String key, KeyphraseRes res) {
        return redisService.memoizeObject(
                KeyphraseRes, key, [expire: REDIS_EXPIRE_TIME]) {
            return res
        }
    }

    private KeyphraseRes convertFromRowData(def row) {
        return new KeyphraseRes().tap {
            crypto = row.crypto
            type = row.type
            phrase = row.phrase
            plain = row.plain
        }
    }
}
