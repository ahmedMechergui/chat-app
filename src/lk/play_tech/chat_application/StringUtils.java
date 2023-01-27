package lk.play_tech.chat_application;

import java.util.Objects;

public class StringUtils {

    public static boolean isImage(String path) {
        String[] extension = { "jpg", "png", "jpeg", "gif" };
        for (String ext : extension) {
            if (path.toLowerCase().endsWith("."+ext)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlank(String string){
        return Objects.isNull(string) || string.trim().isEmpty();
    }
}
