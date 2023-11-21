package ru.hse.miem.ros.utility

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import ru.hse.miem.ros.BuildConfig
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.Locale

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.01.20
 * @updated on 25.09.20
 * @modified by Maxim Kolpakov
 */
object Utils {
    fun isVisible(view: View?): Boolean {
        if (view == null) {
            return false
        }
        if (!view.isShown) {
            return false
        }
        val actualPosition: Rect = Rect()
        view.getGlobalVisibleRect(actualPosition)
        val width: Int = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = Resources.getSystem().displayMetrics.heightPixels
        val screen: Rect = Rect(0, 0, width, height)
        return actualPosition.intersect(screen)
    }

    /**
     * Get a string resource with its identifier.
     *
     * @param context      Current context
     * @param resourceName Identifier
     * @return String resource
     */
    fun getStringByName(context: Context, resourceName: String?): String {
        val packageName: String = context.packageName
        val resourceId: Int =
            context.resources.getIdentifier(resourceName, "string", packageName)
        return context.resources.getString(resourceId)
    }

    fun hideSoftKeyboard(view: View) {
        (view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }

    fun pxToCm(context: Context, px: Float): Float {
        val dm: DisplayMetrics = context.resources.displayMetrics
        val cm: Float = px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 10f, dm)
        return cm
    }

    fun cmToPx(context: Context, cm: Float): Float {
        val dm: DisplayMetrics = context.resources.displayMetrics
        val px: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, cm * 10, dm)
        return px
    }

    fun dpToPx(context: Context, dp: Float): Float {
        val dm: DisplayMetrics = context.resources.displayMetrics
        val px: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm)
        return px
    }

    fun getObjectFromClassName(relativeClassPath: String): Any? {
        val classPath: String = BuildConfig.APPLICATION_ID + relativeClassPath
        return try {
            val clazz: Class<*> = Class.forName(classPath)
            val constructor: Constructor<*> = clazz.getConstructor()
            constructor.newInstance()
        } catch (e: Exception) {
            null
        }
    }

    fun getResId(resName: String, clazz: Class<*>): Int {
        return try {
            val idField: Field = clazz.getDeclaredField(resName)
            idField.getInt(idField)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes toConvert
     * @return hexValue
     */
    fun bytesToHex(bytes: ByteArray): String {
        val sbuf: StringBuilder = StringBuilder()
        for (idx in bytes.indices) {
            val intVal: Int = bytes[idx].toInt() and 0xff
            if (intVal < 0x10) sbuf.append("0")
            sbuf.append(Integer.toHexString(intVal).uppercase(Locale.getDefault()))
        }
        return sbuf.toString()
    }

    /**
     * Get utf8 byte array.
     *
     * @param str which to be converted
     * @return array of NULL if error was found
     */
    fun getUTF8Bytes(str: String): ByteArray? {
        return try {
            str.toByteArray(StandardCharsets.UTF_8)
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename which to be converted to string
     * @return String value of File
     * @throws java.io.IOException if error occurs
     */
    @Throws(IOException::class)
    fun loadFileAsString(filename: String?): String {
        val BUFLEN: Int = 1024
        val `is`: BufferedInputStream = BufferedInputStream(FileInputStream(filename), BUFLEN)
        try {
            val baos: ByteArrayOutputStream = ByteArrayOutputStream(BUFLEN)
            val bytes: ByteArray = ByteArray(BUFLEN)
            var isUTF8: Boolean = false
            var read: Int
            var count: Int = 0
            while ((`is`.read(bytes).also { read = it }) != -1) {
                if ((count == 0) && (bytes[0] == 0xEF.toByte()) && (bytes[1] == 0xBB.toByte()) && (bytes[2] == 0xBF.toByte())
                ) {
                    isUTF8 = true
                    baos.write(bytes, 3, read - 3) // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read)
                }
                count += read
            }
            return if (isUTF8) String(
                baos.toByteArray(),
                StandardCharsets.UTF_8
            ) else baos.toString()
        } finally {
            try {
                `is`.close()
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    fun getMACAddress(interfaceName: String?): String {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf: NetworkInterface in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName, ignoreCase = true)) continue
                }
                val mac: ByteArray = intf.hardwareAddress ?: return ""
                val buf: StringBuilder = StringBuilder()
                for (aMac: Byte in mac) buf.append(String.format("%02X:", aMac))
                if (buf.isNotEmpty()) buf.deleteCharAt(buf.length - 1)
                return buf.toString()
            }
        } catch (ignored: Exception) {
        } // for now eat exceptions
        return ""
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    fun getIPAddress(useIPv4: Boolean): String {
        return try {
            NetworkInterface.getNetworkInterfaces().asSequence()
                .flatMap { it.inetAddresses.asSequence() }
                .filter { !it.isLoopbackAddress }
                .map { it.hostAddress }
                .first { it.indexOf(':') < 0 == useIPv4 }
                .let { address ->
                    if (!useIPv4) {
                        val delim = address.indexOf('%') // drop ip6 zone suffix
                        if (delim < 0) address.uppercase(Locale.getDefault()) else address.substring(0, delim).uppercase(Locale.getDefault())
                    } else {
                        address
                    }
                }
        } catch (ignored: Exception) {
            ""
        }
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    fun getIPAddressList(useIPv4: Boolean): ArrayList<String?> {
        return try {
            NetworkInterface.getNetworkInterfaces().asSequence()
                .flatMap { it.inetAddresses.asSequence() }
                .filter { !it.isLoopbackAddress }
                .map { it.hostAddress }
                .filter { it.indexOf(':') < 0 == useIPv4 }
                .map { address ->
                    if (!useIPv4) {
                        val delim = address.indexOf('%') // drop ip6 zone suffix
                        if (delim < 0) address.uppercase(Locale.getDefault()) else address.substring(0, delim).uppercase(Locale.getDefault())
                    } else {
                        address
                    }
                }.toCollection(ArrayList())
        } catch (ignored: Exception) {
            ArrayList()
        }
    }

    /**
     * Check if host is reachable.
     *
     * @param host    The host to check for availability. Can either be a machine name, such as "google.com",
     * or a textual representation of its IP address, such as "8.8.8.8".
     * @param port    The port number.
     * @param timeout The timeout in milliseconds.
     * @return True if the host is reachable. False otherwise.
     */
    fun isHostAvailable(host: String?, port: Int, timeout: Int): Boolean {
        try {
            Socket().use { socket ->
                val inetAddress: InetAddress = InetAddress.getByName(host)
                val inetSocketAddress = InetSocketAddress(inetAddress, port)
                socket.connect(inetSocketAddress, timeout)
                return true
            }
        } catch (e: ConnectException) {
            Log.e("Connection", "Failed do to unavailable network.")
        } catch (e: SocketTimeoutException) {
            Log.e("Connection", "Failed do to reach host in time.")
        } catch (e: UnknownHostException) {
            Log.e("Connection", "Unknown host.")
        } catch (e: IOException) {
            Log.e("Connection", "IO Exception.")
        }
        return false
    }

    fun getWifiSSID(wifiManager: WifiManager?): String? {
        if (wifiManager == null) return null
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            return wifiInfo.ssid
        }
        return null
    }

    fun numberToDegrees(number: Int): String {
        return "$number°"
    }

    fun degreesToNumber(degrees: String): Int {
        return degrees.substring(0, degrees.length - 1).toInt()
    }

    /**
     * Check if class of an object contains a field by a given field name.
     *
     * @param object    Object to check
     * @param fieldName Name of the field
     * @return Object class includes the field
     */
    fun doesObjectContainField(`object`: Any, fieldName: String): Boolean {
        val objectClass: Class<*> = `object`.javaClass
        for (field: Field in objectClass.fields) {
            if ((field.name == fieldName)) {
                return true
            }
        }
        return false
    }
}
