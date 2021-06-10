package br.com.zupacademy.matheus.pix.registra

import br.com.zupacademy.matheus.pix.TipoChave
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest {

    @Nested
    inner class ALEATORIA { // facilita a visualização na execução dos testes
        @Test
        internal fun `deve ser valido quando chave aleatoria for nula ou vazia`() {
            with(TipoChave.ALEATORIA) {
                assertTrue(valida(null))
                assertTrue(valida(""))
            }
        }

        @Test
        internal fun `nao deve ser valido quando chave aleatoria possuir um valor`() {
            with(TipoChave.ALEATORIA) {
                assertFalse(valida("qualquer coisa"))
            }
        }
    }

    @Nested
    inner class CPF {
        @Test
        internal fun `deve retornar true quando cpf for valido`() {
            with(TipoChave.CPF) {
                assertTrue(valida("72670126049"))
            }
        }

        @Test
        internal fun `deve retornar false quando cpf nao for valido`() {
            with(TipoChave.CPF) {
                assertFalse(valida("726707654219"))
            }
        }

        @Test
        internal fun `deve retornar false quando cpf nao for informado`() {
            with(TipoChave.CPF) {
                assertFalse(valida(""))
                assertFalse(valida(null))
            }
        }
    }

    @Nested
    inner class CELULAR {

        @Test
        internal fun `deve retornar true quando celular for um numero valido`() {
            with(TipoChave.CELULAR) {
                assertTrue(valida("+5511987654321"))
            }
        }

        @Test
        internal fun `nao deve ser valido quando celular for um numero invalido`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida("11987654321"))
                assertFalse(valida("+55a11987654321"))
            }
        }

        @Test
        internal fun `nao deve ser valido quando celular nao for informado`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida(""))
                assertFalse(valida(null))
            }
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        internal fun `deve ser valido quando email for endereco valido`() {
            with(TipoChave.EMAIL) {
                assertTrue(valida("rponte@gmail.com"))
            }
        }

        @Test
        internal fun `nao deve ser valido quando email estiver em um formato invalido`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida("rponte.gmail.com"))
            }
        }

        @Test
        internal fun `nao deve ser valido quando email nao for informado`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida(""))
                assertFalse(valida(null))
            }
        }
    }

}