package Global;

public class User {
    private static String username;
    private static String userId;
    private static String directory;

    public static String getDirectory() {
        return directory;
    }

    public static void setDirectory(String directory) {
        User.directory = directory;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String usernamet) {
        username = usernamet;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userIdt) {
        userId = userIdt;
    }
    
}
