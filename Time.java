import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Time {
	
	
	
	
	
	public static String getDateFromTimestamp(long timestamp){
		DateFormat df=new SimpleDateFormat("dd/MM/yy HH:mm:ss:SS");
		df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		String data=df.format(new Date(timestamp));
		return data;
		
		
	}
	public static long getTimestampFromDate(String date){
		DateFormat df=new SimpleDateFormat("dd/MM/yy HH:mm:ss:SS");
		Date data;
		try {
			df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
			data = df.parse(date);
			long timestamp=data.getTime();
			return timestamp;
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("errore nel parsing della data, il formato deve essere dd/MM/yy HH:mm:ss:SS");
			return 0;
		}
		
		
		
	}
	
	
	
	
	

}
