package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"

const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"
    private val emptyLineToSet = ""
    private val nonEmptyLineExternal = "Deep, dark fantasies..."

// для проверки Workflow выкидываем закомментированную простыню
    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        // Press home
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        // Wait for launcher
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)

        device.findObject(
            UiSelector().resourceId("android:id/title").instance(0)
        ).click()
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.findObject(By.res(packageName, "userInput")).text = textToSet
        device.findObject(By.res(packageName, "buttonChange")).click()

        val result = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(textToSet, result)

        Thread.sleep(5000)
    }

    @Test
    fun testInputEmptyLine() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val expected = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text
        // для упрощения проверки получаем сперва дефолтный текст из элемента
        device.findObject(By.res(MODEL_PACKAGE, "userInput")).text = emptyLineToSet
        device.findObject(By.res(MODEL_PACKAGE, "buttonChange")).click()

        val result = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text
        assertEquals(expected, result)
    }

    @Test
    fun testOpenInputTextInAnotherActivity() {

        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.findObject(By.res(MODEL_PACKAGE, "userInput")).text = nonEmptyLineExternal
        device.findObject(By.res(MODEL_PACKAGE, "buttonActivity")).click()
        // наконец-то происходит что-то интересное
        device.wait(Until.hasObject(By.res(MODEL_PACKAGE, "text")), TIMEOUT)
        //  дожидаемся появления нужного элемента с нашим текстом на втором экране
        val result = device.findObject(By.res(MODEL_PACKAGE, "text")).text
        assertEquals(nonEmptyLineExternal, result)
    }
}



