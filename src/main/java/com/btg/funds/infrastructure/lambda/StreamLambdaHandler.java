package com.btg.funds.infrastructure.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.btg.funds.BtgFundsApplication;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Entry point de la función Lambda.
 * Usa aws-serverless-java-container para adaptar el ciclo de vida
 * de Spring Boot al modelo request/response de Lambda + API Gateway.
 */
@Slf4j
public class StreamLambdaHandler implements RequestStreamHandler {

    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            log.info("[LAMBDA] Inicializando contexto de Spring Boot...");
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(BtgFundsApplication.class);
            log.info("[LAMBDA] Spring Boot inicializado correctamente.");
        } catch (ContainerInitializationException e) {
            log.error("[LAMBDA] Error al inicializar Spring Boot: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                log.error("[LAMBDA] Causa raíz: {}", e.getCause().getMessage(), e.getCause());
            }
            throw new RuntimeException("No se pudo inicializar la aplicación Spring Boot en Lambda", e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
