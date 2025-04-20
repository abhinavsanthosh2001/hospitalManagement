package com.hospital.patient.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillingServiceGrpcClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub billingServiceBlockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.port:9001}") int serverPort
    ) {
        log.info("Connecting to billing service at {}:{}", serverAddress, serverPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();
        billingServiceBlockingStub = BillingServiceGrpc.newBlockingStub(channel);
        
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email){
        BillingRequest request = BillingRequest
                .newBuilder()
                .setEmail(email)
                .setName(name)
                .setPatientId(patientId)
                .build();

        BillingResponse billingResponse = billingServiceBlockingStub.createBillingAccount(request);
        log.info("Billing account created: {}", billingResponse);
        return billingResponse;


    }
}
