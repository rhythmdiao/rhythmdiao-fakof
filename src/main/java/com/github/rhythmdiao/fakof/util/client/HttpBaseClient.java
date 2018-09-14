package com.github.rhythmdiao.fakof.util.client;

import com.google.common.base.Strings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author rhyth
 */
public abstract class HttpBaseClient implements Client {
    private String scheme;
    private String context;
    private CloseableHttpClient client;

    protected HttpBaseClient(String context) {
        this(null, context);
    }

    protected HttpBaseClient(String scheme, String context) {
        this.scheme = "https".equals(scheme) ? scheme : "http";
        this.context = context;
        this.client = ClientBuilder.INSTANCE.getClient();
    }

    protected void setURI(HttpRequestBase httpRequestBase, String path) {
        try {
            httpRequestBase.setURI(new URI(scheme + "://"
                    + ((context == null || "".equals(context)) ? path : context + path)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected void addHeader(HttpRequestBase httpRequestBase, HttpProperty httpProperty) {
        if (httpProperty.getHeaderMap() != null) {
            for (Map.Entry<String, String> header : httpProperty.getHeaderMap().entrySet()) {
                httpRequestBase.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    protected void addParameter(HttpPost httpPost, HttpProperty httpProperty) {
        if (!Strings.isNullOrEmpty(httpProperty.getBody())) {
            StringEntity entity = new StringEntity(httpProperty.getBody(), "UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            return;
        }
        if (httpProperty.getParameterMap() != null) {
            List<NameValuePair> parameters = new LinkedList<NameValuePair>();
            for (Map.Entry<String, String> parameter : httpProperty.getParameterMap().entrySet()) {
                parameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
            HttpEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(parameters, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            httpPost.setEntity(entity);
        }
    }

    protected String execute(HttpRequestBase request) {
        CloseableHttpResponse response;
        try {
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException("Error fetching data from " + request.getURI() + ", and status code is " + response.getStatusLine().getStatusCode());
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
