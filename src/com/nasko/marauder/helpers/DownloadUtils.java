package com.nasko.marauder.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadUtils {

    public static void downloadImage(String url, String path, String imageName) {

        File directoy = new File(path);
        if (!directoy.exists()) {
            directoy.mkdirs();
        }

        try (InputStream in = new URL(url).openStream()){
            Path filePath = Paths.get(directoy.getAbsolutePath(), imageName);
            Files.copy(in, filePath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
