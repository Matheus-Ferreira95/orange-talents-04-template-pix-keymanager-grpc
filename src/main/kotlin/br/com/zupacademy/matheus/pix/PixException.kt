package br.com.zupacademy.matheus.pix

class ChavePixExistenteException(message: String): RuntimeException(message)

class ChavePixNaoEncontradaException(message: String) : RuntimeException(message)