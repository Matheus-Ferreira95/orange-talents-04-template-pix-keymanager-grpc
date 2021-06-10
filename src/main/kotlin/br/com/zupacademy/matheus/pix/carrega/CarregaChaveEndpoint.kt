package br.com.zupacademy.matheus.pix.carrega

import br.com.zupacademy.matheus.CarregaChavePixRequest
import br.com.zupacademy.matheus.CarregaChavePixResponse
import br.com.zupacademy.matheus.KeymanagerCarregaGrpcServiceGrpc
import br.com.zupacademy.matheus.compartilhado.grpc.ErrorHandler
import br.com.zupacademy.matheus.integration.bcb.BancoCentralClient
import br.com.zupacademy.matheus.pix.ChavePixRepository
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler // 1
@Singleton
class CarregaChaveEndpoint(
    @Inject private val repository: ChavePixRepository, // 1
    @Inject private val bcbClient: BancoCentralClient, // 1
    @Inject private val validator: Validator,
) : KeymanagerCarregaGrpcServiceGrpc.KeymanagerCarregaGrpcServiceImplBase() { // 1

    // 9
    override fun carrega(
        request: CarregaChavePixRequest, // 1
        responseObserver: StreamObserver<CarregaChavePixResponse>, // 1
    ) {

        val filtro = request.toModel(validator) // 2
        val chaveInfo = filtro.filtra(repository = repository, bcbClient = bcbClient)

        responseObserver.onNext(CarregaChavePixResponseConverter().convert(chaveInfo)) // 1
        responseObserver.onCompleted()
    }
}