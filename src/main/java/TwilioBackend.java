
import static spark.Spark.*;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.Sms;

import java.util.HashMap;
import java.util.Map;

public class TwilioBackend {

        public static void main(String[] args) {
            String SID = "ACca9625d5004aecd333d236abdf521852";
            String token = "c4e7a3fb6a522a073e18423a04ac181c";
            String twilioNumber = "+19713402317";

            get("/", (req, res) -> "Hello, World!");

            TwilioRestClient client = new TwilioRestClient(SID, token);

            post("/sms", (req, res) -> {
                String body = req.queryParams("Body");
                String to = req.queryParams("To");
                String from = twilioNumber;

                Map<String, String> callParams = new HashMap<>();
                callParams.put("To", to);
                callParams.put("From", from);
                callParams.put("Body", body);
                Sms message = client.getAccount().getSmsFactory().create(callParams);

                return message.getSid();
            });


        }



}
