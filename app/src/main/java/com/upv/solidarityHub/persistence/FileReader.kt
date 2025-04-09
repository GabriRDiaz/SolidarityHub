package com.upv.solidarityHub.persistence

import android.content.res.AssetManager
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.security.AccessController.getContext


class FileReader {

    companion object Reader {
        public val numMunicipios: Int = 9380
        public fun readMunicipiosToArray(input: InputStream) : Array<String?> {
            var res = arrayOfNulls<String>(numMunicipios)
            var linecount = 0
            input.bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    //Log.d("DEBUG","Processing line: $line")
                    res[linecount] = line
                    linecount++
                }
            }
            return res
        }
    }
}