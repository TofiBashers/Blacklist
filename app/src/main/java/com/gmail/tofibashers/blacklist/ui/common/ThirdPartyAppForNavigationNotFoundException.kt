package com.gmail.tofibashers.blacklist.ui.common


/**
 * Created by TofiBashers on 18.07.2018.
 */
class ThirdPartyAppForNavigationNotFoundException : Exception {

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)
}