function decryptTitleAndDescription(masterKey, keyEncrypted, iv){
    let titleEncrypted = document.getElementById('title').value;
    let descriptionEncrypted = document.getElementById('description').value;
    let key = decryptKey(keyEncrypted, masterKey);
    let title = decryptToUtf8String(titleEncrypted, key, iv);
    let description = decryptToUtf8String(descriptionEncrypted, key, iv);
    document.getElementById('title').value = title;
    document.getElementById('description').value = description;
}