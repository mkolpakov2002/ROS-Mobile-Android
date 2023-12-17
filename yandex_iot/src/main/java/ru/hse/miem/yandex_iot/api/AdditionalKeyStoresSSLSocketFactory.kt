package ru.hse.miem.yandex_smart_home.api

import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


/**
 * Allows you to trust certificates from additional KeyStores in addition to
 * the default KeyStore
 */
open class AdditionalKeyStoresSSLSocketFactory(clientKeyStore: KeyStore?, serverKeyStore: KeyStore?) :
    SSLSocketFactory() {
    private var sslContext = SSLContext.getInstance("TLS")

    init {
        if (clientKeyStore != null) {
            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmf.init(clientKeyStore, "".toCharArray())
            sslContext.init(
                kmf.keyManagers,
                arrayOf<TrustManager>(ClientKeyStoresTrustManager(clientKeyStore, serverKeyStore)),
                SecureRandom()
            )
        } else {
            sslContext.init(
                null,
                arrayOf<TrustManager>(ClientKeyStoresTrustManager(serverKeyStore)),
                null
            )
        }
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return sslContext.socketFactory.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return sslContext.socketFactory.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(
        socket: Socket?,
        host: String?,
        port: Int,
        autoClose: Boolean
    ): Socket {
        return sslContext.socketFactory.createSocket(socket, host, port, autoClose)
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return sslContext.socketFactory.createSocket()
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(s: String, i: Int): Socket {
        return sslContext.socketFactory.createSocket(s, i)
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(s: String, i: Int, inetAddress: InetAddress, i1: Int): Socket {
        return sslContext.socketFactory.createSocket(s, i, inetAddress, i1)
    }

    @Throws(IOException::class)
    override fun createSocket(inetAddress: InetAddress, i: Int): Socket {
        return sslContext.socketFactory.createSocket(inetAddress, i)
    }

    @Throws(IOException::class)
    override fun createSocket(
        inetAddress: InetAddress,
        i: Int,
        inetAddress1: InetAddress,
        i1: Int
    ): Socket {
        return sslContext.socketFactory.createSocket(inetAddress, i, inetAddress1, i1)
    }

    /**
     * Based on http://download.oracle.com/javase/1.5.0/docs/guide/security/jsse/JSSERefGuide.html#X509TrustManager
     */
    class ClientKeyStoresTrustManager(vararg additionalKeyStores: KeyStore?) : X509TrustManager {
        private val x509TrustManagers = mutableListOf<X509TrustManager>()

        init {
            val factories = additionalKeyStores.map { keyStore ->
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                    init(keyStore)
                }
            }.toMutableList().apply {
                add(TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                    init(null as KeyStore?)
                })
            }

            factories.flatMap { it.trustManagers.toList() }
                .filterIsInstance<X509TrustManager>()
                .let { managers ->
                    check(managers.isNotEmpty()) { "Couldn't find any X509TrustManagers" }
                    x509TrustManagers.addAll(managers)
                }
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            x509TrustManagers.firstOrNull { tm ->
                runCatching { tm.checkClientTrusted(chain, authType) }.isSuccess
            } ?: throw CertificateException()
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            x509TrustManagers.firstOrNull { tm ->
                runCatching { tm.checkServerTrusted(chain, authType) }.isSuccess
            } ?: throw CertificateException()
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return x509TrustManagers.flatMap { it.acceptedIssuers.toList() }.toTypedArray()
        }
    }
}