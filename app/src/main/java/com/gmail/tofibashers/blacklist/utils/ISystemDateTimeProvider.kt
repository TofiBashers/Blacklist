package com.gmail.tofibashers.blacklist.utils

import org.joda.time.DateTime


/**
 * Created by TofiBashers on 19.03.2018.
 */
interface ISystemDateTimeProvider {

    fun now() : DateTime
}