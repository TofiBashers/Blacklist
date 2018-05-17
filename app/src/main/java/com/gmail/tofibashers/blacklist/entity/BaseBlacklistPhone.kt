package com.gmail.tofibashers.blacklist.entity


/**
 * Created by TofiBashers on 10.05.2018.
 */
abstract class BaseBlacklistPhone(open val number: String,
                                  open val isCallsBlocked: Boolean,
                                  open val isSmsBlocked: Boolean)