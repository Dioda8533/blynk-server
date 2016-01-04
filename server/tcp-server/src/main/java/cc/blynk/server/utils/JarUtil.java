package cc.blynk.server.utils;

import cc.blynk.common.utils.ServerProperties;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class to work with jar file.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.12.15.
 */
public class JarUtil {

    /**
     * Unpacks all files from staticFolder of jar and puts them to current folder within staticFolder path.
     *
     * @param staticFolder - path to resources
     * @throws Exception
     */
    public static void unpackStaticFiles(String staticFolder) throws Exception {
        List<String> staticResources = find(staticFolder);

        for (String staticFile : staticResources) {
            try (InputStream is = JarUtil.class.getResourceAsStream("/" + staticFile)) {
                Path newStaticFile = ServerProperties.getFileInCurrentDir(staticFile);

                Files.deleteIfExists(newStaticFile);
                Files.createDirectories(newStaticFile);

                Files.copy(is, newStaticFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * Returns list of resources that were found in staticResourcesFolder
     *
     * @param staticResourcesFolder - resource folder
     * @return - absolute path to resources within staticResourcesFolder
     * @throws Exception
     */
    public static List<String> find(String staticResourcesFolder) throws Exception {
        CodeSource src = JarUtil.class.getProtectionDomain().getCodeSource();
        List<String> staticResources = new ArrayList<>();

        if (src != null) {
            URL jar = src.getLocation();
            try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                ZipEntry ze;

                while ((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if (entryName.startsWith(staticResourcesFolder) && isResource(entryName)) {
                        staticResources.add(entryName);
                    }
                }
            }
        }

        return staticResources;
    }

    private static boolean isResource(String filename) {
        return filename.endsWith(".js") ||
               filename.endsWith(".css") ||
               filename.endsWith(".html") ||
               filename.endsWith(".ico") ||
               filename.endsWith(".png");
    }

}
