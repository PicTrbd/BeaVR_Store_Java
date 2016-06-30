package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Thomas on 05/12/2015.
 */
public class JSONParser {

    public String url;

    public JSONParser(String _url) {
        url = _url;
    }

    // Execute a post request
    public Object postRequest(List<NameValuePair> urlParameters, Object object) {
        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));

            HttpResponse response = client.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            // Tricky because we need to send the exact type of the wanted response
            return mapper.readValue(result, object.getClass());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return (null);
    }

    // Execute a get request
    public Object getRequest(List<NameValuePair> urlParameters, Object object) {
        try {

            // Creation of the formatted url GET like (?a=b&c=d)
            URIBuilder builder = new URIBuilder(url);
            for (NameValuePair params : urlParameters)
                builder.addParameter(params.getName(), params.getValue());

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(builder.build());

            HttpResponse response = client.execute(get);
            String result = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            // Tricky because we need to send the exact type of the wanted response
            return mapper.readValue(result, object.getClass());

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return (null);
    }

    // Execute a get request like /xxx/1
    public Object getRequestFromID(int id, Object object) {
        try {

            // Creation of the formatted url GET like (?a=b&c=d)
            URIBuilder builder = new URIBuilder(url + Integer.toString(id));

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(builder.build());

            HttpResponse response = client.execute(get);
            String result = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            // Tricky because we need to send the exact type of the wanted response
            return mapper.readValue(result, object.getClass());

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return (null);
    }

    // Execute a put request
    public Object putRequest(List<NameValuePair> urlParameters, Object object) {
        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpPut put = new HttpPut(url);
            put.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));

            HttpResponse response = client.execute(put);
            String result = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            // Tricky because we need to send the exact type of the wanted response
            return mapper.readValue(result, object.getClass());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return (null);
    }

}