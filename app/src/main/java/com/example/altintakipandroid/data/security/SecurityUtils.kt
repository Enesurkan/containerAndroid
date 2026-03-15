package com.example.altintakipandroid.data.security

/**
 * Simple obfuscation for embedded strings (e.g. API key) to avoid plain-text inspection in the binary.
 * Same algorithm as iOS SecurityUtils (XOR with salt). Not a substitute for backend security.
 */
object SecurityUtils {
    private val SALT = byteArrayOf(
        0x54, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53,
        0x65, 0x63, 0x72, 0x65, 0x74, 0x53, 0x61, 0x6c, 0x74
    ) // "ThisIsASecretSalt"

    fun deobfuscate(bytes: ByteArray): String {
        val result = ByteArray(bytes.size)
        for (i in bytes.indices) {
            result[i] = (bytes[i].toInt() xor SALT[i % SALT.size].toInt()).toByte()
        }
        return String(result, Charsets.UTF_8)
    }

    fun obfuscate(string: String): ByteArray {
        val bytes = string.toByteArray(Charsets.UTF_8)
        val result = ByteArray(bytes.size)
        for (i in bytes.indices) {
            result[i] = (bytes[i].toInt() xor SALT[i % SALT.size].toInt()).toByte()
        }
        return result
    }
}
