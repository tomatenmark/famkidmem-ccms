/* userForms */

function prepareUserForm(username, password, frontendUrl, masterKey, linkType){
    const secret = CryptoJS.enc.Utf8.parse(password);
    const loginPepper = "H8KDQYZplb7FUZLoX4lHLg==";
    const loginSpice = CryptoJS.enc.Utf8.parse(loginPepper+username);
    const passwordKeySalt = CryptoJS.lib.WordArray.random(128 / 8);
    const passwordKey = CryptoJS.PBKDF2(secret, passwordKeySalt, {keySize: 128 / 32, iterations: 2500});
    const loginHash = CryptoJS.PBKDF2(secret, loginSpice, {keySize: 128 / 32, iterations: 1250});
    document.userForm.loginHash.value = CryptoJS.enc.Base64.stringify(loginHash);
    document.userForm.passwordKeySalt.value = CryptoJS.enc.Base64.stringify(passwordKeySalt);
    document.userForm.userKey.value = createUserKey(passwordKey, masterKey);
    document.userForm.style.display = 'block';
    createLink(username, password, frontendUrl, linkType);
}

function createLink(username, password, frontendUrl, linkType){
    const credentials = {
        username: username,
        password: password
    };
    const credentialString = JSON.stringify(credentials);
    const credentialWords = CryptoJS.enc.Utf8.parse(credentialString);
    const credentialsBase64 = CryptoJS.enc.Base64.stringify(credentialWords);
    document.userForm.link.value = `${frontendUrl}#${linkType}/${credentialsBase64}`;
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
