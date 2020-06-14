package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.exception.EncryptionException;
import de.mherrmann.famkidmem.ccms.service.push.PushMessage;
import de.mherrmann.famkidmem.ccms.service.push.PushService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FfmpegService {

    String ffmpegCommandName = "ffmpeg"; //used for unit testing

    private final PushService pushService;
    private final CryptoUtil cryptoUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(FfmpegService.class);

    @Autowired
    public FfmpegService(PushService pushService, CryptoUtil cryptoUtil) {
        this.pushService = pushService;
        this.cryptoUtil = cryptoUtil;
    }

    public int encryptVideo(String name) throws EncryptionException {
        try {
            Process process = buildFfmpegProcess(name);
            return handleFfmpeg(process);
        } catch(EncryptionException | IOException ex){
            throw new EncryptionException("Exception during video encryption (ffmpeg): " + ex);
        }
    }

    private Process buildFfmpegProcess(String name) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String[] commands = {
                ffmpegCommandName,
                "-y",
                "-i",
                "./files/video.mp4",
                "-hls_time", "10",
                "-hls_flags", "periodic_rekey",
                "-hls_key_info_file", "./files/enc.keyinfo",
                "-hls_playlist_type", "vod",
                "-hls_segment_filename", "\"./files/"+name+".%d.ts\"",
                "./files/index.m3u8"};
        return runtime.exec(commands);
    }

    private int handleFfmpeg(Process process) throws IOException, EncryptionException {
        updateKeyFilesForFfmpeg();

        //ffmpeg outputs all to stderr ^^
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        State state = new State();
        while ((line = stdError.readLine()) != null) {
            handleFfmpegLine(line, state);
        }

        if(state.isErrorState()){
            LOGGER.error("Ffmpeg Error. See previous log entries for details");
            throw new EncryptionException("Ffmpeg Error. See log box for details");
        }

        LOGGER.info("Ffmpeg successfully encrypted the video.");

        return state.tsFiles;
    }

    private void handleFfmpegLine(String line, State state) throws IOException {
        String possibleBeginningsPattern = "^(ffmpeg|\\s|\\[|Input|Stream|Press|Output|video|frame|$).*$";
        if(line.matches("^\\s*Duration:\\s\\d+:\\d+:\\d+.*$")){
            state.tsFilesExpected = calculateTsFilesExpected(line);
        }
        if(line.matches("^.*Opening\\s'crypto:fileSequence.*$") || line.startsWith("video")){
            state.tsFiles++;
            updateKeyFilesForFfmpeg();
        }
        if(!line.matches(possibleBeginningsPattern) || line.contains("error")){
            state.errorState = true;
            handleError(line);
        }
        if(!line.isEmpty()){
            sendProgress(line, state);
        }
    }

    private void updateKeyFilesForFfmpeg() throws IOException {
        byte[] key = cryptoUtil.generateSecureRandomKeyParam();
        String iv = cryptoUtil.generateSecureRandomKeyParamHex();
        String keyBase64 = cryptoUtil.toBase64(key);
        BufferedWriter writer = new BufferedWriter(new FileWriter("./files/enc.keyinfo"));
        writer.write("data:;base64,"+keyBase64+"\n./files/enc.key\n"+iv);
        writer.close();
        FileOutputStream stream = new FileOutputStream("./files/enc.key");
        stream.write(key);
        stream.close();
    }

    private int calculateTsFilesExpected(String line){
        String pattern = "^\\s*Duration:\\s(\\d+):(\\d+):(\\d+).*$";
        int hours = Integer.valueOf(line.replaceFirst(pattern, "$1"));
        int minutes = Integer.valueOf(line.replaceFirst(pattern, "$2")) + hours*60;
        int seconds = Integer.valueOf(line.replaceFirst(pattern, "$3")) + minutes*60;
        int tsFiles = seconds / 10;
        if((seconds % 10) != 0){
            tsFiles++;
        }
        return tsFiles;
    }

    private void sendProgress(String line, State state){
        LOGGER.debug("Ffmpeg says: " + line);
        pushService.push(PushMessage.videoEncryptionProgress(line, state.frameLineBefore, state.getPercentage()));
        state.frameLineBefore = line.startsWith("frame");
    }

    private void handleError(String line){
        LOGGER.error("Ffmpeg Error: " + line);
        pushService.push(PushMessage.videoEncryptionError(line));
    }

    private class State {
        int tsFiles;
        int tsFilesExpected;
        boolean frameLineBefore;
        boolean errorState;

        int getPercentage() {
            if(tsFilesExpected == 0){
                return 0;
            }
            int percentage = 100 / tsFilesExpected * tsFiles;
            if (tsFiles >= tsFilesExpected) {
                percentage = 100;
            }
            return percentage;
        }

        boolean isErrorState() {
            return errorState || tsFiles < tsFilesExpected;
        }
    }

}
