package com.pm.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/*
 * JwtValidation is being called from application.yml.
 * The filters: parameter will take anything which ends with GatewayFilterFactory, will take its prefix.
 */
@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

	private final WebClient webClient;

	public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
			@Value("${auth.service.url}") String authServiceUrl) {
		this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
	}

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {
			String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			/*
			 * Handle invalid tokens
			 */
			if (null == token || !token.startsWith("Bearer ")) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			return webClient.get().uri("/validate").header(HttpHeaders.AUTHORIZATION, token).retrieve()
					.toBodilessEntity().then(chain.filter(exchange));

		};
	}
}
