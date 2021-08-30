package classic.cipher.generation.app

import classic.cipher.generation.app.constants.CipherType
import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult
import groovy.json.*
import grails.plugin.redis.*

@Transactional
class RailfenceService {

    def  dataService
    def  redisService

    private static final int REDIS_EXPIRE_TIME = 60
    private static final String REDIS_KEY_EN = "rf:e"
    private static final String REDIS_KEY_DE = "rf:d"

    RailfenceRes encrypt(RailfenceReq req) {
        log.info "Start encryption for ${req.properties} "

        if (redisService."$REDIS_KEY_EN:$req.text:$req.count" != null) {
            return new JsonSlurper().parseText(redisService."$REDIS_KEY_EN:$req.text:$req.count")
        }

        GroovyRowResult result = dataService.getRailfenceByPlain(req.text, req.count)
        if (result != null) {
            return setCache("$REDIS_KEY_EN:$req.text:$req.count", convertFromRowData(result))
        }
        RailfenceRes res = new RailfenceRes().tap {
            plain = req.text
            type = CipherType.RAIL_FENCE
            count = req.count
        }
        List rails = []
        for (i in 0.. req.count-1) {rails << ""}
        for (i in 0..req.text.length()-1) {
            rails[i%req.count] =  rails[i%req.count] + req.text[i]
        }
        res.crypto = rails.join("")
        dataService.insertRailfenceData(res)
        setCache("$REDIS_KEY_EN:$req.text:$req.count", res)
        return res
    }

    RailfenceRes decrypt(RailfenceReq req) {
        log.info "Start decryption for ${req.properties} "

        if (redisService."$REDIS_KEY_DE:$req.text:$req.count" != null) {
            return new JsonSlurper().parseText(redisService."$REDIS_KEY_DE:$req.text:$req.count")
        }

        GroovyRowResult result = dataService.getRailfenceByEncrypt(req.text, req.count)
        if (result != null) {
            return setCache("$REDIS_KEY_DE:$req.text:$req.count", convertFromRowData(result))
        }

        RailfenceRes res = new RailfenceRes().tap {
            crypto = req.text
            type = CipherType.RAIL_FENCE
            count = req.count
            plain = ""
        }
        int mod = req.text.length() % req.count
        int railsCount = req.text.length() / req.count

        List rails = []
        int s = 0
        for (i in 0..req.count-1) {
            int len = i < mod ? railsCount+1: railsCount
            rails << req.text.substring(s, s+len)
            s = s + len
        }
        for (i in 0..railsCount) {
            rails.each {
                if (i < it.length()) res.plain = res.plain + it[i]
            }
        }
        dataService.insertRailfenceData(res)
        setCache("$REDIS_KEY_DE:$req.text:$req.count", res)
        return res
    }

    private RailfenceRes setCache(String key, RailfenceRes res) {
        return redisService.memoizeObject(
                RailfenceRes, key, [expire: REDIS_EXPIRE_TIME]) {
            return res
        }
    }

    private RailfenceRes convertFromRowData(def row) {
        return new RailfenceRes().tap {
            crypto = row.crypto
            type = row.type
            count = row.count
            plain = row.plain
        }
    }
}
