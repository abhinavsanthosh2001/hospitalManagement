package com.hospital.billing.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class Billing extends BillingServiceGrpc.BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(Billing.class);

    @Override
    public void createBillingAccount(BillingRequest request, StreamObserver<BillingResponse> responseObserver) {

        log.info("Received request for creating billing account: {}", request.toString());
        // Business logic
        BillingResponse billingResponse = BillingResponse.newBuilder().setAccountId("1234567890").setStatus("Success").build();
        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();


    }

}
