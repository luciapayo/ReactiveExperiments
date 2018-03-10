package com.traumtraum.adventapp2.base.extensions

/**
 * Created by Lucia on 10/02/2018
 */

/**
 * Returns the object if the predicate returns true.
 * Returns null if the predicate returns false.
 */
inline fun <T> T.filterSingular(predicate: (T) -> Boolean): T? {
    return if (predicate.invoke(this)) this else null
}
