
import static spark.Spark.*;

import com.twilio.Twilio;


import com.twilio.sdk.TwilioRestClient;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.sdk.resource.instance.Sms;
import com.twilio.type.PhoneNumber;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


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
            String SID = "ACc68a5633f3513f235b3ce7c5a2cedc6d";
            String twilioToken = "783279d8c69d116820838cce1304ebc9";
            String twilioNumber = "+19712385837";
            Conversation conversation = new Conversation();


            final CountDownLatch latch = new CountDownLatch(1);


            Twilio.init(SID, twilioToken);
//            System.out.println(message1.getBody());

            ResourceSet<Message> messages = Message.reader().read();

            //STORE INFO
            ArrayList<String> messageStore = new ArrayList<>();
            String mTo;
            String mFrom;
            String receivedMessage;

            // Loop over messages and print out a property for eac  h one.
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


            get("/", (req, res) -> "Hello, World!");

            TwilioRestClient client2 = new TwilioRestClient(SID, twilioToken);

            post("/sms", (req, res) -> {

                String body = req.queryParams("Body");
                System.out.println(body);
                String to = req.queryParams("To");
                String from = twilioNumber;

                Thread translatorService = new Thread(new TranslatorService(body, 1000, latch));

                translatorService.start(); //separate thread will initialize CacheService



                Map<String, String> callParams = new HashMap<>();
                callParams.put("To", to);
                callParams.put("From", from);
                callParams.put("Body", body);
                Sms message = client2.getAccount().getSmsFactory().create(callParams);

                return message.getSid();
            });

            post("/smsBack", (req, res) -> {
                if(conversation.getConvoList().size() > 1){
                    return null;
                }else{
                    String body = req.queryParams("Body");
//                body.substring(38, body.length());
                    String to = req.queryParams("To");
                    String from = twilioNumber;

                    System.out.println(body);

                    conversation.addToList(body);
                    System.out.println("substring " + body);
                    System.out.println("in conversation " +conversation.getConvoList().toString());


                    Map<String, String> callParams = new HashMap<>();
                    callParams.put("To", to);
                    callParams.put("From", from);
                    callParams.put("Body", body);
                    Sms message = client2.getAccount().getSmsFactory().create(callParams);

                }


                return "worked";
            });

            get("/receiveSMS", (req,res) ->{
                int length = conversation.getConvoList().size();
                String save = conversation.getConvoList().get(length -1 );

                System.out.println(save);
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(conversation);


                conversation.clear();


                return json;
            });


        }




}
