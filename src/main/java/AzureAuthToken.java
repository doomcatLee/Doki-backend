import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.net.ssl.HttpsURLConnection;

//Client to call Cognitive Services Azure Authentication Token service in order to get an access token.
public class AzureAuthToken {

    //Name of header used to pass the subscription key to the token service
    public static final String OcpApimSubscriptionKeyHeader = "f93282fa1d78473a82b3fc0b2b3b9980";

    //when to refresh the token
    public static final int TokenCacheDurationMins = 8;

    /// URL of the token service
    private String  _serviceUrl= "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";

    /// Gets the subscription key.
    private String _subscriptionKey;

    //Cache the value of the last valid token obtained from the token service.
    private String _storedTokenValue = "";

    // When the last valid token was obtained.
    private Instant  _storedTokenTime = Instant.MIN;


    public AzureAuthToken(String subscriptionKey)
    {
        this._subscriptionKey = subscriptionKey;
    }

    public String getAccessToken()
    {
        if (  this._storedTokenTime.until(Instant.now(), ChronoUnit.MINUTES) < TokenCacheDurationMins)
        {
            return this._storedTokenValue;
        }
        try
        {
            String charset = StandardCharsets.UTF_8.name();
            URL url = new URL(this._serviceUrl);

            HttpsURLConnection  connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty(OcpApimSubscriptionKeyHeader, this._subscriptionKey);
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.close();

            int responseCode = connection.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK)
            {
                // OK
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset)))
                {
                    StringBuffer res = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        res.append(line);
                    }

                    this._storedTokenValue = res.toString();
                    this._storedTokenTime = Instant.now();
                    return this._storedTokenValue;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}