function addTsFilenamesToVideoRemoveForm(){
    let m3u8Encrypted = document.getElementById('m3u8').getAttribute('data-m38u');
    let keyEncrypted = document.getElementById('m3u8Key').getAttribute('data-m3u8Key');
    let iv = document.getElementById('m3u8Iv').getAttribute('data-m3u8Iv');
    let masterKey = document.getElementById('masterKey').getAttribute('data-masterKey');
    let key = decryptKey(keyEncrypted, masterKey);
    let m3u8 = decryptToUtf8String(m3u8Encrypted, key, iv);
    document.videoRemoveForm.tsFilesName.value = getTsFilesName(m3u8);
    document.videoRemoveForm.tsFilesCount.value = getTsFilesCount(m3u8);
}

function decryptTitle(encryptedTitle, keyEncrypted, iv, masterKey) {
    let key = decryptKey(keyEncrypted, masterKey);
    document.getElementById('title').innerText = decryptToUtf8String(encryptedTitle, key, iv);
}

function getTsFilesCount(m3u8){
    console.log(m3u8);
    let lines = m3u8.split('\n');
    let tsFiles = 0;
    for(let i = 0; i < lines.length; i++){
        if(lines[i].indexOf('.ts') >= 0){
            tsFiles++;
        }
    }
    return tsFiles;
}

function getTsFilesName(m3u8){
    console.log(m3u8);
    let lines = m3u8.split('\n');
    let tsFiles = 0;
    for(let i = 0; i < lines.length; i++){
        if(lines[i].indexOf('.ts') >= 0){
            return lines[i].replace(/^([^.]+)\.\d+\.ts$/, '$1.%d.ts');
        }
    }
    return tsFiles;
}