import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Guest on 6/21/17.
 */
public class TranslatorService implements Runnable{
    private final int timeToStart;
    private final CountDownLatch latch;
    private String uri ="https://api.microsofttranslator.com/v2/http.svc/Translate?appid=Bearer%20eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzY29wZSI6Imh0dHBzOi8vYXBpLm1pY3Jvc29mdHRyYW5zbGF0b3IuY29tLyIsInN1YnNjcmlwdGlvbi1pZCI6IjQxMTk3YTFkZTUyNzQ4M2VhOTNjOTU4NTMyMDc2MjVhIiwicHJvZHVjdC1pZCI6IlRleHRUcmFuc2xhdG9yLkYwIiwiY29nbml0aXZlLXNlcnZpY2VzLWVuZHBvaW50IjoiaHR0cHM6Ly9hcGkuY29nbml0aXZlLm1pY3Jvc29mdC5jb20vaW50ZXJuYWwvdjEuMC8iLCJhenVyZS1yZXNvdXJjZS1pZCI6Ii9zdWJzY3JpcHRpb25zLzNlZmE1YmFlLTNhOGMtNGNlZC1hYmMyLWRhZmM4NDEyMTEyNy9yZXNvdXJjZUdyb3Vwcy9kb29tY2F0L3Byb3ZpZGVycy9NaWNyb3NvZnQuQ29nbml0aXZlU2VydmljZXMvYWNjb3VudHMvRG9raSIsImlzcyI6InVybjptcy5jb2duaXRpdmVzZXJ2aWNlcyIsImF1ZCI6InVybjptcy5taWNyb3NvZnR0cmFuc2xhdG9yIiwiZXhwIjoxNDk4MDc4NTMzfQ.UAJpQJTPzda4gTk7Tj3qrfZmzgwqtSmZxjTxD56oHLA&text=I%20am%20going%20to%20be%20working%20with%20a%20professor%20today.%20We%20will%20leave%20in%205%20hours.&to=ko";
    public static String translatedMsg = "";

    public TranslatorService(int time, CountDownLatch latch){
        timeToStart = time;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeToStart);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String i;
            System.out.println("Output from Server .... \n");
            while ((i = br.readLine()) != null) {
                translatedMsg = i;
//                System.out.println(translatedMsg);
            }

            conn.disconnect();


        } catch (InterruptedException ex) {
            Logger.getLogger(TranslatorService.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (MalformedURLException e) {

        e.printStackTrace();
    } catch (IOException e) {

        e.printStackTrace();
    }
        System.out.println( "TranslatorService "  + "is Up");
        latch.countDown(); //reduce count of CountDownLatch by 1

    }

}
