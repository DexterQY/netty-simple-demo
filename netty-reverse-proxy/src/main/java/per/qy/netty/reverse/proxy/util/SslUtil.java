package per.qy.netty.reverse.proxy.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class SslUtil {

    public static SSLEngine getClientSslEngine(String host, int port) throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);

        SSLEngine sslEngine = sc.createSSLEngine(host, port);
        sslEngine.setUseClientMode(true);
        sslEngine.setNeedClientAuth(false);
        return sslEngine;
    }
}
