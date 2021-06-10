package br.com.zupacademy.matheus.pix.remove

import br.com.zupacademy.matheus.KeymanagerRemoveGrpcServiceGrpc
import br.com.zupacademy.matheus.RemoveChavePixRequest
import br.com.zupacademy.matheus.RemoveChavePixResponse
import br.com.zupacademy.matheus.compartilhado.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChaveEndpoint(@Inject private val service: RemoveChaveService)
    : KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceImplBase() {

    override fun remove(request: RemoveChavePixRequest, responseObserver: StreamObserver<RemoveChavePixResponse>) {

        service.remove(clienteId = request.clienteId, pixId = request.pixId)

        responseObserver.onNext(RemoveChavePixResponse.newBuilder()
            .setClienteId(request.clienteId)
            .setPixId(request.pixId)
            .build())

        responseObserver.onCompleted()
    }
}