import io.blocktracer.transport.util.EthNumberUtil;

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


        BigInteger hexValue =  new BigInteger( "0x0".substring(2),16 );
        BigDecimal value = new BigDecimal( hexValue,hexValue.compareTo(BigInteger.ZERO)==0 ? 0 : 18);
        String val = value.toPlainString();

        int endIndex=0;

        for(int i=val.length(); i >= 1; i--){
            System.out.println(val.substring(i-1,i));
             if(!val.substring(i-1,i).equals("0")){
                 endIndex=i;
                break;
            }
        }


        System.out.println( val.substring(0,endIndex));
        String result = EthNumberUtil.hexToGasNumber("0x1b1ae47763b79c7000");
        System.out.println( result);

    }
}
