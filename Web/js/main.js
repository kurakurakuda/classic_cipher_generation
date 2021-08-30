const typeRadios = document.getElementsByName('type');
const kindRadios = document.getElementsByName('kind');
//const baseUrl = 'http://192.168.99.100:8080/'
const baseUrl = 'http://ccg-app-service:8080/'
const pathForRF = 'railfence/'
const pathForCS = 'caesarCipher/'
const pathForKP = 'keyphrase/'
const pathForEn = 'encrypt'
const pathForDe = 'decrypt'


async function execute(event) {
    let text = document.getElementById('text').value
    let key = document.getElementById('key').value
    let type = getType()
    let kind = getKind()

    let data = generateRequestBody(text, type, key)
    let endpoint = generateEndpoint(type, kind)
    let result = ''
    try {
        result = await call(endpoint, data)
    } catch (e) {
        console.log(e)
    }
    if (result && kind == 'ENCRYPT') {
        document.getElementById('result').innerHTML = result['crypto']
    } else if (result && kind == 'DECRYPT') {
        document.getElementById('result').innerHTML = result['plain']
    }
}

function getType() {
    for (var i = 0, length = typeRadios.length; i < length; i++) {
        if (typeRadios[i].checked) {
            return typeRadios[i].value;
        }
    }
}

function getKind() {
    for (var i = 0, length = kindRadios.length; i < length; i++) {
        if (kindRadios[i].checked) {
            return kindRadios[i].value;
        }
    }
}

function generateRequestBody(text, type, key) {
    if (type == 'RAIL_FENCE') {
        return {
            text: text,
            count: parseInt(key)
        };
    } else if (type == 'CAESAR') {
        return {
            text: text,
            shift: parseInt(key)
        };
    } else if (type == 'KEY_PHRASE') {
        return {
            text: text,
            phrase: key
        };
    } else {
        return null
    }
}

function generateEndpoint(type, kind) {
    var path = ''
    if (type == 'RAIL_FENCE' && kind == 'ENCRYPT') {
        path = pathForRF + pathForEn
    } else if (type == 'RAIL_FENCE' && kind == 'DECRYPT') {
        path = pathForRF + pathForDe
    } else if (type == 'CAESAR' && kind == 'ENCRYPT') {
        path = pathForCS + pathForEn
    } else if (type == 'CAESAR' && kind == 'DECRYPT') {
        path = pathForCS + pathForDe
    } else if (type == 'KEY_PHRASE' && kind == 'ENCRYPT') {
        path = pathForKP + pathForEn
    } else if (type == 'KEY_PHRASE' && kind == 'DECRYPT') {
        path = pathForKP + pathForDe
    } else {
        return null
    }
    return baseUrl + path
}

function call(endpoint, data) {
    console.log("bathUrl: " + baseUrl)
    console.log("endpoint: " + endpoint)
    return $.ajax({
        type:"post",                // method = "POST"
        url:endpoint,        // POST送信先のURL
        data:JSON.stringify(data),  // JSONデータ本体
        contentType: 'application/json', // リクエストの Content-Type
        dataType: "json",           // レスポンスをJSONとしてパースする
        mode: "cors",
        Accept: "application/json",
        success: function(json_data) {   // 200 OK時
            console.log(json_data)
            return json_data
        },
        error: function(json_data) {         // HTTPエラー時
            console.log(json_data)
            if (json_data && json_data['responseJSON']
                && json_data['responseJSON']['error']
                && json_data['responseJSON']['error']['errorMessage']) {
                alert("Execution failed. Please try again later: " + json_data['responseJSON']['error']['errorMessage']);
            } else {
                alert("Execution failed. Please try again later: " + json_data.toString());
            }

            return null
        }
    });
}