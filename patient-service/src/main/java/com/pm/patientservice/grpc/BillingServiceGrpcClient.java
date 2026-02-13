package com.pm.patientservice.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class BillingServiceGrpcClient {

	private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);

	private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

	public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String serverAddress,
			@Value("${billing.service.grpc.port:9001}") int serverPort) {
		log.info("Connecting to billing service GRPC service at {}:{}", serverAddress, serverPort);

		ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();
		blockingStub = BillingServiceGrpc.newBlockingStub(channel);
	}

	public BillingResponse createBillingAccount(String patientId, String name, String email) {
		BillingRequest billingRequest = BillingRequest.newBuilder().setPatientId(patientId).setName(name)
				.setEmail(email).build();
		BillingResponse billingResponse = blockingStub.createBillingAccount(billingRequest);
		log.info("Recieved response from billing service via GRPC: {}", billingResponse);
		return billingResponse;
	}
}
