package classic.cipher.generation.app

import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import groovy.sql.GroovyRowResult
import javax.sql.DataSource

@Transactional
class DataService {

    Sql sql
    DataSource dataSource

    private  static  final String queryToInsertCryptography =
            "INSERT INTO cryptography(plain, crypto, type) VALUES (?, ?, ?);"
    private  static  final String queryToInsertRailFence =
            "INSERT INTO rail_count(id, count) SELECT MAX(id), ? FROM cryptography;"
    private  static  final String queryToInsertCaesar =
            "INSERT INTO shift_count(id, shift) SELECT MAX(id), ? FROM cryptography;"
    private  static  final String queryToInsertKeyPhrase =
            "INSERT INTO key_phrase(id, phrase) SELECT MAX(id), ? FROM cryptography;"
    private static final String queryToGetRailfenceByCrypto =
            "SELECT * FROM cryptography JOIN rail_count ON cryptography.id = rail_count.id WHERE cryptography.crypto = ? AND rail_count.count = ?"
    private static final String queryToGetRailfenceByPlain =
            "SELECT * FROM cryptography JOIN rail_count ON cryptography.id = rail_count.id WHERE cryptography.plain = ? AND rail_count.count = ?"
    private static final String queryToGetCaesarByCrypto =
            "SELECT * FROM cryptography JOIN shift_count ON cryptography.id = shift_count.id WHERE cryptography.crypto = ? AND shift_count.shift = ?"
    private static final String queryToGetCaesarByPlain =
            "SELECT * FROM cryptography JOIN shift_count ON cryptography.id = shift_count.id WHERE cryptography.plain = ? AND shift_count.shift = ?"
    private static final String queryToGetKeyPhraseByCrypto =
            "SELECT * FROM cryptography JOIN key_phrase ON cryptography.id = key_phrase.id WHERE cryptography.crypto = ? AND key_phrase.phrase = ?"
    private static final String queryToGetKeyPhraseByPlain =
            "SELECT * FROM cryptography JOIN key_phrase ON cryptography.id = key_phrase.id WHERE cryptography.plain = ? AND key_phrase.phrase = ?"

    void setDataSource(dataSource) {
        sql = new Sql(dataSource)
    }

    Integer insertRailfenceData(RailfenceRes data) {
        int res1 = sql.executeUpdate(queryToInsertCryptography, [data.plain, data.crypto, data.type])
        log.info "insertion into {cryptography} is success: [plian: " + data.plain + ", crypto: " +  data.crypto + ", type: " + data.type + "]"
        int res2 = sql.executeUpdate(queryToInsertRailFence, [data.count])
        log.info "insertion into {rail_count} is success: [count: " +  data.count + "]"
        return res1 * res2
    }

    Integer insertCaesarData(CaesarRes data) {
        int res1 = sql.executeUpdate(queryToInsertCryptography, [data.plain, data.crypto, data.type])
        log.info "insertion into {cryptography} is success: [plian: " + data.plain + ", crypto: " +  data.crypto + ", type: " + data.type + "]"
        int res2 = sql.executeUpdate(queryToInsertCaesar, [data.shift])
        log.info "insertion into {shift_count} is success: [shift: " +  data.shift + "]"
        return res1 * res2
    }

    Integer insertKeyphraseData(KeyphraseRes data) {
        int res1 = sql.executeUpdate(queryToInsertCryptography, [data.plain, data.crypto, data.type])
        log.info "insertion into {cryptography} is success: [plian: " + data.plain + ", crypto: " +  data.crypto + ", type: " + data.type + "]"
        int res2 = sql.executeUpdate(queryToInsertKeyPhrase, [data.phrase])
        log.info "insertion into {key_phrase} is success: [phrase: " +  data.phrase + "]"
        return res1 * res2
    }

    GroovyRowResult getRailfenceByEncrypt(String crypto, int count) {
        def res = sql.rows(queryToGetRailfenceByCrypto, [crypto, count])
        log.info "get Railfence Data query by encrypt: " + crypto + ", count: " + count
        return res[0]
    }

    GroovyRowResult getRailfenceByPlain(String plain, int count) {
        def res = sql.rows(queryToGetRailfenceByPlain, [plain, count])
        log.info "get Railfence Data query by plain: " + plain + ", count: " + count
        return res[0]
    }

    GroovyRowResult getCaesarByEncrypt(String crypto, int shift) {
        def res = sql.rows(queryToGetCaesarByCrypto, [crypto, shift])
        log.info "get Caesar Data query by encrypt: " + crypto + ", shift: " + shift
        return res[0]
    }

    GroovyRowResult getCaesarByPlain(String plain, int shift) {
        def res = sql.rows(queryToGetCaesarByPlain, [plain, shift])
        log.info "get Caesar Data query by plain: " + plain + ", shift: " + shift
        return res[0]
    }

    GroovyRowResult getKeyPhraseByEncrypt(String crypto, String phrase) {
        def res = sql.rows(queryToGetKeyPhraseByCrypto, [crypto, phrase])
        log.info "get KeyPhrase Data query by encrypt: " + crypto + ", phrase: " + phrase
        return res[0]
    }

    GroovyRowResult getKeyPhraseByPlain(String plain, String phrase) {
        def res = sql.rows(queryToGetKeyPhraseByPlain, [plain, phrase])
        log.info "get KeyPhrase Data query by plain: " + plain + ", phrase: " + phrase
        return res[0]
    }
}
