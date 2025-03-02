import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")
        viewModel.sendMyMessage("TestMessage")
        val actual = viewModel.messages.value.single()
        assertThat(actual, equalTo(message))
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        val scope = CoroutineScope(testDispatcher)
        val jobs = mutableListOf<Job>()
        repeat(messagesToSend.size) { index ->
            val job = scope.launch {
                viewModel.sendMyMessage(messagesToSend[index].text)
            }
            jobs.add(job)
        }
        jobs.joinAll()

        val actualAmount = viewModel.messages.value.size
        Assert.assertEquals(actualAmount, messagesToSend.size)

        repeat(messagesToSend.size) { index ->
            val actualValue = viewModel.messages.value[index]
            Assert.assertEquals(actualValue, messagesToSend[index])
        }
    }
}