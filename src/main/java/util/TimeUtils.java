package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static void print(String msg, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSSS");    
        Date resultdate = new Date(time);
        UiUtils.showMessage(msg + sdf.format(resultdate));
    }
    
}
