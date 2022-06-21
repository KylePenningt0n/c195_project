package Database;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DBtime {
    /**
     * getTimeStamp method
     * gets timestamp value based off localDateTime now of UTC timezone
     * @return
     */
    public static java.sql.Timestamp getTimeStamp(){
        ZoneId zoneId = ZoneId.of("UTC");
        LocalDateTime localDateTime = LocalDateTime.now(zoneId);
        java.sql.Timestamp timestamp = Timestamp.valueOf(localDateTime);
        return timestamp;
    }
}
