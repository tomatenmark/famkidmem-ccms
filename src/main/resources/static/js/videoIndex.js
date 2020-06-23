function buildIndex(){
    let videos = JSON.parse(document.getElementById('videos').getAttribute('data-raw'));
    for(let i = 0; i < videos.length; i++){
        let video = videos[i];
        buildIndexItem(video);
    }
}

function buildIndexItem(video){
    
}