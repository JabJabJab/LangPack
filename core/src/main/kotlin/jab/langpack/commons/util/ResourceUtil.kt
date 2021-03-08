package jab.langpack.commons.util

import java.io.*
import java.net.URL

/**
 * The **ResourceUtil** class stores static-modified methods from Bukkit for global resource
 *   management.
 *
 * @author Bukkit team (Modified by Jab)
 */
object ResourceUtil {

    /**
     * (Modified method from Bukkit to write global files)
     *
     * @param path The path inside of the JAR file.
     * @param replace If set to true, write the resource to a file, even if one already exists.
     */
    fun saveResource(path: String, replace: Boolean = false) {

        if (path.isEmpty()) {
            throw RuntimeException("ResourcePath cannot be empty.")
        }

        var resourcePath2 = path
        resourcePath2 = resourcePath2.replace('\\', '/')
        val `in`: InputStream = getResource(resourcePath2)
            ?: return
        val outFile = File(resourcePath2)
        val lastIndex = resourcePath2.lastIndexOf('/')
        val outDir = File(resourcePath2.substring(0, if (lastIndex >= 0) lastIndex else 0))
        if (!outDir.exists()) {
            outDir.mkdirs()
        }

        try {
            if (!outFile.exists() || replace) {
                val out: OutputStream = FileOutputStream(outFile)
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                `in`.close()
            }
        } catch (ex: IOException) {
            System.err.println("Could not save ${outFile.name} to $outFile")
        }
    }

    /**
     * (Modified method from Bukkit to write global files)
     *
     * @param path The path inside of the JAR file.
     *
     * @return Returns the input stream for the resource in the JAR file. If the file isn't found, null is returned.
     */
    private fun getResource(path: String): InputStream? {
        return try {
            val url: URL = this::class.java.classLoader.getResource(path) ?: return null
            val connection = url.openConnection()
            connection.useCaches = false
            connection.getInputStream()
        } catch (ex: IOException) {
            null
        }
    }
}
