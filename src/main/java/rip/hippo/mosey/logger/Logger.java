package rip.hippo.mosey.logger;

/**
 * @author Hippo
 * @version 1.0.0, 7/10/20
 * @since 1.0.0
 *
 * A tiny logger adapter to avoid the java-scala-interop issue
 */
public enum Logger {
    ;

    public static void info(String message) {
        org.tinylog.Logger.info(message);
    }

    public static void warn(String message) {
        org.tinylog.Logger.warn(message);
    }

    public static void error(Throwable e, String message) {
        org.tinylog.Logger.error(e, message);
    }
}
