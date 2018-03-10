package data;

/**
 Class to be able to test timestamp related features. Inject this instead of using System.currentTimeMillis()
 */
public class TimestampProvider {

    public TimestampProvider() {}

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
