import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.msukanen.splintersector_server.model.Room
import kotlin.test.Test

class RoomTest {
    @Test
    fun `room jsons ok with encodeToString and serializer`() {
        val room = Room("Test Room", 1)
        val jsonString = Json.encodeToString(serializer(), room)
        println(jsonString)
    }

    @Test
    fun `room jsons ok with direct encodeToString`() {
        val room = Room("Test Room", 1)
        val jsonString = Json.encodeToString(room)
        println(jsonString)
    }
}
