package classic.cipher.generation.app

import classic.cipher.generation.app.constants.Alphabetic
import classic.cipher.generation.app.constants.CipherType
import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult
import groovy.json.*
import grails.plugin.redis.*

@Transactional
class CaesarCipherService {

    def  dataService
    def  redisService

    private static final int REDIS_EXPIRE_TIME = 60
    private static final String REDIS_KEY_EN = "cs:e"
    private static final String REDIS_KEY_DE = "cs:d"

    CaesarRes encrypt(CaesarReq req) {
        log.info "Start encryption for ${req.properties} "

        if (redisService."$REDIS_KEY_EN:$req.text:$req.shift" != null) {
            return new JsonSlurper().parseText(redisService."$REDIS_KEY_EN:$req.text:$req.shift")
        }

        GroovyRowResult result = dataService.getCaesarByPlain(req.text, req.shift)
        if (result != null) {
            return setCache("$REDIS_KEY_EN:$req.text:$req.shift", convertFromRowData(result))
        }
        CaesarRes res = new CaesarRes().tap {
            plain = req.text
            crypto = ""
            type = CipherType.CAESAR
            shift = req.shift
        }
        for (i in 0..req.text.size()-1) {
            String s = req.text[i]
            int idx = (Alphabetic.UPPER.indexOf(s.toUpperCase()) + req.shift) % 26
            String shifted = s.toUpperCase() == s ? Alphabetic.UPPER[idx] : Alphabetic.LOWER[idx]
            res.crypto = res.crypto + shifted
        }
        dataService.insertCaesarData(res)
        setCache("$REDIS_KEY_EN:$req.text:$req.shift", res)
        return res
    }

    CaesarRes decrypt(CaesarReq req) {
        log.info "Start decryption for ${req.properties} "

        if (redisService."$REDIS_KEY_DE:$req.text:$req.shift" != null) {
            return new JsonSlurper().parseText(redisService."$REDIS_KEY_DE:$req.text:$req.shift")
        }

        GroovyRowResult result = dataService.getCaesarByEncrypt(req.text, req.shift)
        if (result != null) {
            return setCache("$REDIS_KEY_DE:$req.text:$req.shift", convertFromRowData(result))
        }
        CaesarRes res = new CaesarRes().tap {
            plain = ""
            crypto = req.text
            type = CipherType.CAESAR
            shift = req.shift
        }
        for (i in 0..req.text.size()-1) {
            String s = req.text[i]
            int idx = (Alphabetic.UPPER.indexOf(s.toUpperCase()) + 26 - req.shift) % 26
            String shifted = s.toUpperCase() == s ? Alphabetic.UPPER[idx] : Alphabetic.LOWER[idx]
            res.plain = res.plain + shifted
        }
        dataService.insertCaesarData(res)
        setCache("$REDIS_KEY_DE:$req.text:$req.shift", res)
        return res
    }

    private CaesarRes setCache(String key, CaesarRes res) {
        return redisService.memoizeObject(
                CaesarRes, key, [expire: REDIS_EXPIRE_TIME]) {
            return res
        }
    }

    private CaesarRes convertFromRowData(def row) {
        return new CaesarRes().tap {
            crypto = row.crypto
            type = row.type
            shift = row.shift
            plain = row.plain
        }
    }
}
