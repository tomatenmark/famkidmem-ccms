/* userForms */

const loginPepper = "ee/ZUOqS8i30+TKZ5mU9dA==";

function prepareUserForm(username, password, frontendUrl, masterKey, linkType){
    const secret = CryptoJS.enc.Utf8.parse(password);
    const loginSpice = CryptoJS.enc.Utf8.parse(loginPepper+username);
    const passwordKeySaltBase64 = document.userForm.passwordKeySalt.value;
    const passwordKeySalt =  CryptoJS.enc.Base64.parse(passwordKeySaltBase64);
    const passwordKey = CryptoJS.PBKDF2(secret, passwordKeySalt, {keySize: 128 / 32, iterations: 2500});
    const loginHash = CryptoJS.PBKDF2(secret, loginSpice, {keySize: 128 / 32, iterations: 1250});
    document.userForm.loginHash.value = CryptoJS.enc.Base64.stringify(loginHash);
    document.userForm.userKey.value = createUserKey(passwordKey, masterKey);
    document.userForm.style.display = 'block';
    createLink(username, password, frontendUrl, linkType);
}

function createLink(username, password, frontendUrl, linkType){
    if(frontendUrl.substr(frontendUrl.length-1, 1) === '/'){
        frontendUrl = frontendUrl.substr(0, frontendUrl.length-1);
    }
    const data = {
        username: username,
        password: password,
        userKey: document.userForm.userKey.value
    };
    const dataString = JSON.stringify(data);
    const dataWords = CryptoJS.enc.Utf8.parse(dataString);
    const dataBase64 = CryptoJS.enc.Base64.stringify(dataWords);
    document.userForm.link.value = `${frontendUrl}/${linkType}.html#${linkType}/${dataBase64}`;
}

function createUserKey(passwordKey, masterKeyEncoded){
    const masterKey = CryptoJS.enc.Base64.parse(masterKeyEncoded);
    const encryptedMasterKey = CryptoJS.AES.encrypt(masterKey, passwordKey,{mode:CryptoJS.mode.ECB, padding: CryptoJS.pad.NoPadding});
    return CryptoJS.enc.Base64.stringify(encryptedMasterKey.ciphertext);
}

function decryptFromBase64String(ciphertextBase64, keyBase64, ivBase64){
    let ciphertext = CryptoJS.enc.Base64.parse(ciphertextBase64);
    let key = CryptoJS.enc.Base64.parse(keyBase64);
    let iv = CryptoJS.enc.Base64.parse(ivBase64);
    return CryptoJS.AES.decrypt({ciphertext:ciphertext}, key, {iv:iv});
}

function decryptToUtf8String(ciphertextBase64, keyBase64, ivBase64){
    let plaintext = decryptFromBase64String(ciphertextBase64, keyBase64, ivBase64);
    return CryptoJS.enc.Utf8.stringify(plaintext);
}

function decryptToBase64String(ciphertextBase64, keyBase64, ivBase64){
    let plaintext = decryptFromBase64String(ciphertextBase64, keyBase64, ivBase64);
    return CryptoJS.enc.Base64.stringify(plaintext);
}

function decryptKey(encryptedKeyBase64, masterKeyBase64){
    let encryptedKey = CryptoJS.enc.Base64.parse(encryptedKeyBase64);
    let masterKey = CryptoJS.enc.Base64.parse(masterKeyBase64);
    let key = CryptoJS.AES.decrypt({ciphertext:encryptedKey}, masterKey, {mode:CryptoJS.mode.ECB, padding: CryptoJS.pad.NoPadding});
    return CryptoJS.enc.Base64.stringify(key);
}

function checkSilvester(){
    let month = parseInt(document.getElementById('month').value);
    let day = parseInt(document.getElementById('day').value);
    let maybeSilvester = (month === 12 && day === 31) || (month === 1 && day === 1);
    let checked = document.getElementById('silvester').checked;
    document.getElementById('silvesterToggle').style.display = maybeSilvester ? 'inline' : 'none';
    document.getElementById('silvester').checked = checked && maybeSilvester;
}