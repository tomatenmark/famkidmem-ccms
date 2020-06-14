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
    const encryptedMasterKey = CryptoJS.AES.encrypt(masterKey, passwordKey,{mode: CryptoJS.mode.ECB});
    return CryptoJS.enc.Base64.stringify(encryptedMasterKey.ciphertext);
}



/* videoForms */

function addTsFilenamesToVideoRemoveForm(){
    const m3u8Base64 = document.getElementById('m3u8').getAttribute('data-m3u8');
    //TODO: decrypt m3u8, find ts filenames and add them to videoRemoveForm
    //example for inputField for second ts file: <input type="hidden" name="tsFilename[1]" value="{FILENAME}">
}

function startAddVideo(){
    document.getElementById('fileProcessingErrors').innerText = '';
    document.getElementById("progress").style.display = 'block';
    uploadFile("thumbnailFile", "upload thumbnail", "/video/upload-thumbnail");
}

function handlePush(message){
    let messageObject = JSON.parse(message.body);
    switch(messageObject.message){
        case 'thumbnailUploadComplete':
            uploadFile("videoFile", "upload video", "/video/upload-video");
            break;
        case 'videoUploadComplete':
            startEncryption();
            break;
        case 'finishedWithThumbnail':
            startShowVideoEncryption();
            break;
        case 'videoEncryptionProgress':
            showProgress(messageObject);
            break;
        case 'videoEncryptionError':
            showVideoEncryptionError(messageObject);
            break;
        case 'finishedWithVideo':
            console.log("video encryption completed")
            break;
    }
}

function startEncryption() {
    document.getElementById('step').innerText = 'encrypt thumbnail';
    document.getElementById('progressBar').value = '';
    encrypt();
}

function startShowVideoEncryption() {
    document.getElementById('step').innerText = 'encrypt video';
    document.getElementById('progressBar').value = '';
    document.getElementById('ffmpegProgress').innerHTML = '<div>starting...</div>';
}

function showProgress(messageObject){
    document.getElementById('progressBar').value = messageObject.value;
    if(messageObject.override){
        let entries = document.getElementById('ffmpegProgress').children.length;
        document.getElementById('ffmpegProgress').children[entries-1].innerText = messageObject.details;
    } else {
        document.getElementById('ffmpegProgress').innerHTML += '<div>' + messageObject.details + '</div>';
    }
}

function showVideoEncryptionError(messageObject){
    document.getElementById('progressBar').value = 0;
    document.getElementById('ffmpegProgress').innerHTML += '<div class="error">' + messageObject.details + '</div>';
}

function showUploadError(message){
    document.getElementById('fileProcessingErrors').innerText = message;
    document.getElementById('progress').style.display = 'none';
}

function encrypt(){
    let client = new XMLHttpRequest();
    client.addEventListener("error", function(e) {
        showUploadError("error during encryption");
        console.error(e.message);
    });
    client.open("POST", "/video/encrypt");
    client.send();
}

function uploadFile(fileInputId, step, target) {
    let progress = document.getElementById("progressBar");
    progress.value = 0;

    let fileList = document.getElementById(fileInputId).files;
    let file = fileList[0];
    if(!file){
        showUploadError("No file selected. Could not do: " + step);
        return;
    }

    document.getElementById("step").innerText = step;

    let formData = new FormData();
    let client = new XMLHttpRequest();

    formData.append("file", file);

    client.addEventListener("error", function(e) {
        console.error(e.message);
        showUploadError("Could not do: " + step);
    });

    client.addEventListener("load", function(e) {
        progress.value = progress.max;
    });

    client.addEventListener("abort", function(e) {
        console.error(e.message);
        showUploadError("Aborted upload");
    });

    client.upload.addEventListener("progress", function(e) {
        let p = Math.round(100 / e.total * e.loaded);
        progress.value = p;
    });

    client.open("POST", target);
    client.send(formData);
}