package com.gmail.tofibashers.blacklist

import android.support.test.runner.AndroidJUnit4
import org.junit.runner.RunWith
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.support.test.InstrumentationRegistry
import android.arch.persistence.room.testing.MigrationTestHelper
import android.support.test.filters.MediumTest
import com.gmail.tofibashers.blacklist.data.db.BlacklistDatabase
import com.gmail.tofibashers.blacklist.data.db.Migrations
import org.junit.Rule
import org.junit.Test


/**
 * Created by TofiBashers on 29.04.2018.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class MigrationsTest {

    @JvmField
    @Rule
    var testHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            BlacklistDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    fun migrate1to2_validSchema() {
        testHelper.createDatabase(TEST_DB_NAME, 1)
        testHelper.runMigrationsAndValidate(TEST_DB_NAME, 2, false, Migrations.MIGRATION_1_2)
    }

    companion object {
        private val TEST_DB_NAME = "db_blacklist_test"
    }
}