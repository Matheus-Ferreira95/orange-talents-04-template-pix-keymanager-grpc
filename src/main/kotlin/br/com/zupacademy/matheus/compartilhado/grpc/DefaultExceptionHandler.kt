package br.com.zupacademy.matheus.compartilhado.grpc

import br.com.zupacademy.matheus.compartilhado.grpc.ExceptionHandler.StatusWithDetails
import br.com.zupacademy.matheus.pix.ChavePixExistenteException
import br.com.zupacademy.matheus.pix.ChavePixNaoEncontradaException
import io.grpc.Status
import javax.validation.ConstraintViolationException

class DefaultExceptionHandler : ExceptionHandler<Exception> {

    override fun handle(e: Exception): StatusWithDetails {
        val status = when (e) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
            is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is ChavePixExistenteException -> Status.ALREADY_EXISTS.withDescription(e.message)
            is ChavePixNaoEncontradaException -> Status.NOT_FOUND.withDescription(e.message)

            else -> Status.UNKNOWN.withDescription(e.message)
        }
        return StatusWithDetails(status.withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return true
    }
}