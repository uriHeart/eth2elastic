package io.blocktracer.transport.util;


import org.elasticsearch.search.DocValueFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class EthDateUtil {

    private static final String YYYY_MM_DD_HH_24 =  "yyyy-MM-dd HH:mm:ss";

    public static String hexToDateString(@NotNull String hexDate,@NotNull String timeZone){
        ZoneId zoneId = ZoneId.of(timeZone );//"UTC","Asia/Seoul"

        Long dateLong = new BigInteger(hexDate.substring(2),16).longValue();
        Instant instant = Instant.ofEpochSecond( dateLong );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );

        LocalDateTime time = zdt.toLocalDateTime();

        DateTimeFormatter format = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_24);

         return format.format(zdt);
    }


    public static LocalDateTime hexToDate(@NotNull String hexDate,@NotNull String timeZone){
        ZoneId zoneId = ZoneId.of(timeZone );//"UTC","Asia/Seoul"

        Long dateLong = new BigInteger(hexDate.substring(2),16).longValue();
        Instant instant = Instant.ofEpochSecond( dateLong );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
        LocalDateTime time = zdt.toLocalDateTime();

        return time;
    }

    public static HashMap<String,Object> hexToDateAll(@NotNull String hexDate, @NotNull String timeZone){
        ZoneId zoneId = ZoneId.of(timeZone );//"UTC","Asia/Seoul"

        HashMap<String,Object> result = new HashMap<String,Object>();
        Long dateLong = new BigInteger(hexDate.substring(2),16).longValue();
        result.put("long",dateLong);
        Instant instant = Instant.ofEpochSecond( dateLong );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
        LocalDateTime time = zdt.toLocalDateTime();
       result.put("date", time);

        DateTimeFormatter format = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_24);
        result.put("string",format.format(zdt));

        //org.joda.time.format.DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        //result.put("date", DateTime.parse(format.format(zdt),fmt));


        return result;
    }
}
