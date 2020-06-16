package de.mherrmann.famkidmem.ccms.service;


import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.service.push.PushService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FfmpegServiceTest {

    @Autowired
    private FfmpegService ffmpegService;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private TestUtil testUtil;



    @Before
    public void setUp(){
        ffmpegService.ffmpegCommandName = getOsDummyFfmpegCommandName();
        testUtil.deleteFilesDirectory();
        testUtil.createFilesDirectory();
    }

    @After
    public void tearDown(){
        ffmpegService.ffmpegCommandName = "ffmpeg";
        testUtil.deleteFilesDirectory();
    }

    @Test
    public void shouldRunCheckMessages() throws Exception {
        Runnable runnable = () -> ffmpegService.encryptVideo("");
        Thread thread = new Thread(runnable);
        thread.start();

        Thread.sleep(1000);
        assertThat(PushService.getLastMessage().getDetails()).isEqualTo("Frame 80/240");
        assertThat(PushService.getLastMessage().getValue()).isEqualTo(33);
        assertThat(PushService.getLastMessage().isOverride()).isEqualTo(false);
        Thread.sleep(5000);
        assertThat(PushService.getLastMessage().getValue()).isEqualTo(67);
        assertThat(PushService.getLastMessage().getDetails()).isEqualTo("Frame 160/240");
        assertThat(PushService.getLastMessage().isOverride()).isEqualTo(true);
        Thread.sleep(5000);
        assertThat(PushService.getLastMessage().getValue()).isEqualTo(100);
        assertThat(PushService.getLastMessage().getDetails()).isEqualTo("Finished");
    }

    @Test
    public void shouldRunCheckKeyUpdate() throws Exception {
        generateKeyInfoFiles();

        Runnable runnable = () -> ffmpegService.encryptVideo("");
        Thread thread = new Thread(runnable);
        thread.start();

        Thread.sleep(1000);
        checkKeyInfoFiles();
        Thread.sleep(5000);
        checkKeyInfoFiles();
    }

    @Test
    public void shouldRunCheckTsFilesCount() {
        int tsFiles = ffmpegService.encryptVideo("");

        assertThat(tsFiles).isEqualTo(3);
    }

    @Test
    public void shouldRunCheckCall() throws Exception {
        String expectedCall = "-y -i ./files/video.mp4 -hls_time 10 -hls_flags periodic_rekey -hls_key_info_file " +
                "./files/enc.keyinfo -hls_playlist_type vod -hls_segment_filename \"./files/.%d.ts\" ./files/index.m3u8";

        Runnable runnable = () -> ffmpegService.encryptVideo("");
        Thread thread = new Thread(runnable);
        thread.start();

        Thread.sleep(1000);
        String call = Files.readAllLines(Paths.get("./files/call.txt")).get(0);
        assertThat(call).isEqualTo(expectedCall);

    }

    private String getOsDummyFfmpegCommandName() {
        String os = System.getProperty("os.name");
        boolean windows = os.contains("Windows") || os.contains("windows");
        return windows ? "ffmpegDummy.bat" : "./ffmpegDummy.sh";
    }

    private void generateKeyInfoFiles() throws IOException {
        new File("./files/enc.key").createNewFile();
        new File("./files/enc.keyinfo").createNewFile();
        new File("./files/enc.key.prev").createNewFile();
        new File("./files/enc.keyinfo.prev").createNewFile();
    }

    private void checkKeyInfoFiles() throws IOException {
        byte[] encKeyBytes = Files.readAllBytes(Paths.get("./files/enc.key"));
        byte[] encKeyInfoBytes = Files.readAllBytes(Paths.get("./files/enc.keyinfo"));
        byte[] encKeyBytesPrev = Files.readAllBytes(Paths.get("./files/enc.key.prev"));
        byte[] encKeyInfoBytesPrev = Files.readAllBytes(Paths.get("./files/enc.keyinfo.prev"));

        assertThat(cryptoUtil.toBase64(encKeyBytes)).isNotEqualTo(cryptoUtil.toBase64(encKeyBytesPrev));
        assertThat(cryptoUtil.toBase64(encKeyInfoBytes)).isNotEqualTo(cryptoUtil.toBase64(encKeyInfoBytesPrev));

        FileOutputStream stream = new FileOutputStream("./files/enc.key.prev");
        stream.write(encKeyBytes);
        stream.close();
        stream = new FileOutputStream("./files/enc.keyinfo.prev");
        stream.write(encKeyInfoBytes);
        stream.close();
    }

}
