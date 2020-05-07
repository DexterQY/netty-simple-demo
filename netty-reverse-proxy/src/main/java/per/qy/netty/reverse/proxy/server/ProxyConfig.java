package per.qy.netty.reverse.proxy.server;

public class ProxyConfig {

    private int port;
    private String proxyPath;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProxyPath() {
        return proxyPath;
    }

    public void setProxyPath(String proxyPath) {
        this.proxyPath = proxyPath;
    }

    @Override
    public String toString() {
        return "ProxyConfig{" +
                "port=" + port +
                ", proxyPath='" + proxyPath + '\'' +
                '}';
    }
}
