
import static spark.Spark.*;

import com.twilio.Twilio;


import com.twilio.sdk.TwilioRestClient;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.sdk.resource.instance.Sms;
import com.twilio.type.PhoneNumber;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TwilioBackend extends com.twilio.base.Resource{

        public static void main(String[] args) {
            String SID = "ACca9625d5004aecd333d236abdf521852";
            String twilioToken = "c4e7a3fb6a522a073e18423a04ac181c";
            String twilioNumber = "+19713402317";


            final CountDownLatch latch = new CountDownLatch(1);
            Thread translatorService = new Thread(new TranslatorService(1000, latch));

            translatorService.start(); //separate thread will initialize CacheService


            try{
                latch.await();  //main thread is waiting on CountDownLatch to finish
                System.out.println("In the main method" + TranslatorService.translatedMsg);
                System.out.println("All services are up, Application is starting now");
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }


            Twilio.init(SID, twilioToken);
            Message message1 = Message.fetcher("SMc7a8efb3289f6b68a9475ddf5d347f17").fetch();
//            System.out.println(message1.getBody());

            ResourceSet<Message> messages = Message.reader().read();

            //STORE INFO
            ArrayList<String> messageStore = new ArrayList<>();
            String mTo;
            String mFrom;
            String receivedMessage;

            // Loop over messages and print out a property for each one.
            for (Message message : messages) {
                messageStore.add(message.getBody());
                mTo = message.getTo().toString();
                mFrom = message.getFrom().toString();
                if (mFrom.equals("+15039983176")){
                    receivedMessage = message.getBody();
                    System.out.println(message.getBody());
                    break;
                }

//                message.deleter(message.getSid());

            }

            for (String i : messageStore) {
                if (i.length() > 25){
//                    System.out.println(i.substring(38, i.length()));
//                    System.out.println(i.substring(38, i.length()));
                }
            }



//            ResourceSet<Message> messages = Message
//                    .reader()
//                    .setTo(new PhoneNumber("+15039983176"))
//                    .setFrom(new PhoneNumber(twilioNumber))
//                    .setDateSent(DateTime.parse("2016-01-01'T'09:28:00Z")).read();



            // Loop over messages and print out a property for each one.
//            for (Message message : messages) {
//                System.out.println(message.getBody());
//            }


            get("/", (req, res) -> "Hello, World!");

            TwilioRestClient client2 = new TwilioRestClient(SID, twilioToken);

            post("/sms", (req, res) -> {
                String body = req.queryParams("Body");
                System.out.println(body);
                String to = req.queryParams("To");
                String from = twilioNumber;

                Map<String, String> callParams = new HashMap<>();
                callParams.put("To", to);
                callParams.put("From", from);
                callParams.put("Body", body);
                Sms message = client2.getAccount().getSmsFactory().create(callParams);

                return message.getSid();
            });

            post("/smsBack", (req, res) -> {
                String body = req.queryParams("Body");
//                body.substring(38, body.length());
                String to = req.queryParams("To");
                String from = twilioNumber;

                System.out.println("unfiltered "  + body);

//                System.out.println(body.substring(15, body.length()));


                Map<String, String> callParams = new HashMap<>();
                callParams.put("To", to);
                callParams.put("From", from);
                callParams.put("Body", body);
                Sms message = client2.getAccount().getSmsFactory().create(callParams);

//                System.out.println("testing webhooks triggered");

//                return message.getSid();
                return body;
            });


        }




}
