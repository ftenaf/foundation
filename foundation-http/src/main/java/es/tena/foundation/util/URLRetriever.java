package es.tena.foundation.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.codec.binary.Base64;

public class URLRetriever {
    
    public static void main(String[] args){
        getBase64("admin", "1q2w3e4R");
    }

    public void URLToDisk(String url, String file, boolean sslEnabled) throws Exception {
        URLToDisk(url, file, sslEnabled, "");
    }
    
    public static String getBase64(String user, String pass){
        byte[] encodedBytes = Base64.encodeBase64((user+":"+pass).getBytes());
        System.out.println("encodedBytes " + new String(encodedBytes));
        byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
        System.out.println("decodedBytes " + new String(decodedBytes));
        return new String(encodedBytes);
    }

    public void URLToDisk(String url, String file, boolean sslEnabled, String pass) throws Exception {
        byte[] arrayOfByte = new byte[4096];
        File localFile = new File(file);
        if (!sslEnabled) {
            setTrustAllCerts();
        }
//        url = "http://frvotels1.freiremar.loc:8080/otdsws/login?RFA=e7994c58%2Dc418%2D4774%2D91cc%2De900bfa9a08a%3Ahttp%3A%2F%2Ffropentext1%2Efreiremar%2Eloc%2Fotcs%2Fcs%2Eexe%3Ffunc%3Dll%26objId%3D978325%26objAction%3Ddownload";
        url = url.replace(" ", "%20");
        URL localURL = new URL(url);
        URLConnection localURLConnection = localURL.openConnection();
        if (pass.length() > 0) {
            localURLConnection.setRequestProperty("Authorization", "Basic " + pass);
        }
        InputStream localInputStream = localURLConnection.getInputStream();
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
        int i;
        while ((i = localInputStream.read(arrayOfByte)) != -1) {
            localFileOutputStream.write(arrayOfByte, 0, i);
        }
        localFileOutputStream.close();
        localInputStream.close();
    }

    private void setTrustAllCerts() throws Exception {
        TrustManager[] arrayOfTrustManager = {new URLRetriever$1(this)};
        try {
            SSLContext localSSLContext = SSLContext.getInstance("SSL");
            localSSLContext.init(null, arrayOfTrustManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(localSSLContext
                    .getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new URLRetriever$2(
                    this));
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

}

class URLRetriever$1 implements X509TrustManager {

    URLRetriever$1(URLRetriever paramURLRetriever) {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public void checkClientTrusted(
            X509Certificate[] paramArrayOfX509Certificate, String paramString) {
    }

    public void checkServerTrusted(
            X509Certificate[] paramArrayOfX509Certificate, String paramString) {
    }
}

class URLRetriever$2 implements HostnameVerifier {

    URLRetriever$2(URLRetriever paramURLRetriever) {
    }

    public boolean verify(String paramString, SSLSession paramSSLSession) {
        return true;
    }
}
