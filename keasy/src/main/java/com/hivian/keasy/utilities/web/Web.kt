@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.hivian.keasy.utilities.web

import org.jsoup.Jsoup
import java.io.IOException

object Web {

    /**
     * Fetches a string of html code from [url]
     */
    @Throws(IOException::class)
    private fun fetchHtmlFrom(url : String) : String? = try {
        Jsoup.connect(url).ignoreContentType(true).get().html()
    } catch (exception : IOException) {
        null
    }

}