package co.analisys.clases.security;

public final class AuthContext {
    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setToken(String token) {
        TOKEN.set(token);
    }

    public static String getToken() {
        return TOKEN.get();
    }

    public static void clear() {
        TOKEN.remove();
    }
}
