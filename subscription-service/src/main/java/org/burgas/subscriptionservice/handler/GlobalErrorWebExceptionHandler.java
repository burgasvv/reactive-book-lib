package org.burgas.subscriptionservice.handler;

import lombok.Getter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;

@Getter
@Component
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final ObjectProvider<ViewResolver> viewResolvers;
    private final CodecConfigurer codecConfigurer;

    public GlobalErrorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties.Resources resources,
            ApplicationContext applicationContext,
            ObjectProvider<ViewResolver> viewResolvers,
            CodecConfigurer codecConfigurer
    ) {
        super(errorAttributes, resources, applicationContext);
        this.viewResolvers = viewResolvers;
        this.codecConfigurer = codecConfigurer;
        super.setMessageReaders(codecConfigurer.getReaders());
        super.setMessageWriters(codecConfigurer.getWriters());
        super.setViewResolvers(viewResolvers.stream().toList());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), request ->  {
                    Map<String, Object> attributes = errorAttributes.getErrorAttributes(
                            request, ErrorAttributeOptions.of(MESSAGE, EXCEPTION, ERROR, STACK_TRACE));

                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .body(BodyInserters.fromValue(attributes));
                }
        );
    }
}
