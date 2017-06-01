
import static spark.Spark.*;

public class TwilioBackend {

        public static void main(String[] args) {
            get("/", (req, res) -> "Hello, World!");
        }
}
