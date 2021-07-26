package org.kimaita.vaccinationscheduler;

import org.json.JSONObject;

import java.io.InputStream;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json;

    // constructor
    public JSONParser() {

    }
    // function get json from url
    // by making HTTP POST or GET method
    /*public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

        // Making HTTP request
        try {
            // check for request method
            if(method.equals("POST")){

                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                CloseableHttpResponse httpResponse = httpclient.execute((ClassicHttpRequest) httpPost);
                org.apache.hc.core5.http.HttpEntity httpEntity = httpResponse.getEntity();

                is = httpEntity.getContent();
                EntityUtils.consume(httpEntity);

            }*//*else if(method.equals("GET")){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }*//*

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException exc) {
            exc.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, StandardCharsets.ISO_8859_1), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e);
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e);
        }

        return jObj;
    }*/
}
/*HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
   try {
     urlConnection.setDoOutput(true);
     urlConnection.setChunkedStreamingMode(0);

     OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
     writeStream(out);

     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
     readStream(in);
   } finally {
     urlConnection.disconnect();
   }*/
