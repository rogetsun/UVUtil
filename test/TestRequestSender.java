import com.uv.utils.MD5;
import com.uv.utils.http.HttpRequestSender;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * Created by uv2sun on 16/5/19.
 */
public class TestRequestSender {
    public static void main(String[] args) throws IOException {
        //带cookie测试session,以登录为例子
        JSONObject cookie = new JSONObject();
        String s = HttpRequestSender.post("http://localhost:8080/credit/to_login", JSONObject.fromObject("{login_no:'lipeng', login_password:'" + MD5.string2md5("lipeng") + "'}"), cookie);
        JSONObject ret = JSONObject.fromObject(s);
        if (ret.getInt("ret_code") != 0) {
            System.out.println(ret);
            return;
        }
        s = HttpRequestSender.get("http://127.0.0.1:8080/credit/users", null, cookie);
        System.out.println(s);
        System.out.println(cookie);
    }
}

