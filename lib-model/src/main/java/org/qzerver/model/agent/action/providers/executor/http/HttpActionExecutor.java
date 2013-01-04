package org.qzerver.model.agent.action.providers.executor.http;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.executor.http.threads.HttpOutputThread;
import org.qzerver.model.agent.action.providers.executor.http.threads.HttpTimeoutThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpActionExecutor.class);

    private static final String JMX_PARAM_NODE = "\\$\\{nodeAddress\\}";

    private static final String JMX_PARAM_EXECUTION = "\\$\\{executionId\\}";

    private Validator beanValidator;

    private long maxCaptureSize;

    @Override
    public HttpActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        Preconditions.checkNotNull(actionDefinition, "Action definition is not set");
        Preconditions.checkNotNull(nodeAddress, "Node address is not set");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        HttpActionDefinition definition = (HttpActionDefinition) actionDefinition;

        LOGGER.debug("HTTP query will be executed on node [{}]", nodeAddress);

        String effectiveUrl = definition.getUrl();
        effectiveUrl = effectiveUrl.replaceAll(JMX_PARAM_NODE, nodeAddress);
        String scheduleExecutionIdText = Long.toString(scheduleExecutionId);
        effectiveUrl = effectiveUrl.replaceAll(JMX_PARAM_EXECUTION, scheduleExecutionIdText);

        HttpClient httpClient = composeHttpClient(definition);

        try {
            return processHttpClient(httpClient, effectiveUrl, definition);
        } catch (Exception e) {
            LOGGER.warn("Fail to process HTTP request");
            return produceExceptionalResult(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private HttpClient composeHttpClient(HttpActionDefinition definition) {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, definition.getConnectionTimeoutMs());
        HttpConnectionParams.setSoTimeout(httpParameters, 0);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

        if (definition.getPlainAuthUsername() != null) {
            Credentials credentials = new UsernamePasswordCredentials(
                definition.getPlainAuthUsername(), definition.getPlainAuthPassword());
            httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        }

        return httpClient;
    }

    private HttpActionResult produceExceptionalResult(Exception e) {
        HttpActionOutput output = new HttpActionOutput();
        output.setStatus(HttpActionOutputStatus.IDLE);
        output.setData(null);

        HttpActionResult result = new HttpActionResult();
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());
        result.setStatus(HttpActionResultStatus.EXCEPTION);
        result.setSucceed(false);
        result.setStatusCode(0);
        result.setOutput(output);

        return result;
    }

    private HttpActionResult processHttpClient(HttpClient httpClient, String effectiveUrl,
        HttpActionDefinition definition) throws Exception
    {
        HttpUriRequest request;

        switch (definition.getMethod()) {
            case GET:
                request = composeGetRequest(effectiveUrl, definition);
                break;
            case POST:
                request = composePostRequest(effectiveUrl, definition);
                break;
            default:
                throw new IllegalArgumentException("Unknown method: " + definition.getMethod());
        }

        HttpResponse response = httpClient.execute(request);

        return processHttpResponse(request, response, definition);
    }

    private HttpActionResult processHttpResponse(HttpUriRequest request, HttpResponse response,
        HttpActionDefinition definition) throws Exception
    {
        HttpActionOutput output = new HttpActionOutput();
        output.setStatus(HttpActionOutputStatus.CAPTURED);

        HttpActionResult result = new HttpActionResult();
        result.setStatus(HttpActionResultStatus.QUERIED);
        result.setOutput(output);

        int statusCode = response.getStatusLine().getStatusCode();
        result.setStatusCode(statusCode);
        String reason = response.getStatusLine().getReasonPhrase();
        result.setReason(reason);
        boolean succeed = statusCode == definition.getExpectedStatusCode();
        result.setSucceed(succeed);

        HttpEntity entity = response.getEntity();

        HttpOutputThread outputThread = new HttpOutputThread(request, entity, maxCaptureSize,
            definition.isSkipOutput());
        outputThread.start();

        if (definition.getTimeoutMs() > 0) {
            HttpTimeoutThread timeoutThread = new HttpTimeoutThread(outputThread, definition.getTimeoutMs());
            timeoutThread.start();
        }

        outputThread.join();

        result.setOutput(outputThread.composeActionOutput());

        return result;
    }

    private HttpUriRequest composeGetRequest(String effectiveUrl, HttpActionDefinition definition) {
        HttpGet request = new HttpGet(effectiveUrl);

        if (MapUtils.isNotEmpty(definition.getHeaders())) {
            for (Map.Entry<String, String> entry : definition.getHeaders().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return request;
    }

    private HttpUriRequest composePostRequest(String effectiveUrl, HttpActionDefinition definition) {
        HttpPost request = new HttpPost(effectiveUrl);

        if (MapUtils.isNotEmpty(definition.getHeaders())) {
            for (Map.Entry<String, String> entry : definition.getHeaders().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (MapUtils.isNotEmpty(definition.getPostParams())) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : definition.getPostParams().entrySet()) {
                NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                nameValuePairs.add(nameValuePair);
            }

            HttpEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8);
            request.setEntity(encodedFormEntity);
        }

        return request;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }

    @Required
    public void setMaxCaptureSize(long maxCaptureSize) {
        this.maxCaptureSize = maxCaptureSize;
    }
}
