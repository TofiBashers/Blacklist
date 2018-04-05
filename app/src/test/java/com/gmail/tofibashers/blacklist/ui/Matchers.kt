package com.gmail.tofibashers.blacklist.ui

import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.junit.internal.ThrowableCauseMatcher
import kotlin.reflect.KClass


/**
 * Created by TofiBashers on 28.02.2018.
 */

fun <T: Throwable> errorTypeAndEqualsCause(errorType: KClass<out Throwable>, errorCause: T) : Matcher<Nothing> =
        allOf(isA(errorType.java), ThrowableCauseMatcher.hasCause(equalTo(errorCause)))