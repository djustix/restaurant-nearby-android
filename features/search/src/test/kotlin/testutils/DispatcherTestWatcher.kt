package testutils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class DispatcherRule: TestWatcher() {
    val ioDispatcher = TestCoroutineDispatcher()

    override fun finished(description: Description?) {
        super.finished(description)

        ioDispatcher.cleanupTestCoroutines()
    }
}