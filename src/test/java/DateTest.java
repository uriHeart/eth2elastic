import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTest {

    public static void main(String[] arg){
        String hexDate ="0x5b834b3f";
        ZoneId zoneId = ZoneId.of("Asia/Seoul" );//"UTC","Asia/Seoul"

        Long dateLong = new BigInteger(hexDate.substring(2),16).longValue();
        Instant instant = Instant.ofEpochSecond( dateLong );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
        LocalDateTime time = zdt.toLocalDateTime();


        BigInteger hexValue =  new BigInteger( "0xf3238b2cd0d8000".substring(2),16 );
        BigDecimal value = new BigDecimal( hexValue,hexValue.compareTo(BigInteger.ZERO)==0 ? 0 : 18);
        value.toPlainString();

    }
}
