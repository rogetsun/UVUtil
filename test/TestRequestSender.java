import com.uv.utils.http.HttpRequestSender;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * Created by uv2sun on 16/5/19.
 */
public class TestRequestSender {
    public static void main(String[] args) throws IOException {
        String s = HttpRequestSender.post("http://localhost:8000", JSONObject.fromObject("{name:'litx'}"));
        System.out.println(s);
    }
}

