package com.gmail.tofibashers.blacklist

import com.gmail.tofibashers.blacklist.entity.ActivityInterval
import com.gmail.tofibashers.blacklist.entity.ActivityTimeIntervalWithIgnoreSettings
import com.gmail.tofibashers.blacklist.entity.BlacklistContactPhoneNumberItem


/**
 * Created by TofiBashers on 24.01.2018.
 */

typealias TimeAndIgnoreSettingsByWeekdayId = HashMap<Int, ActivityTimeIntervalWithIgnoreSettings>
typealias PairOfBlacklistContactPhonesAndIntervals = Pair<List<BlacklistContactPhoneNumberItem>, List<List<ActivityInterval>>>