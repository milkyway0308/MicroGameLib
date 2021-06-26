package skywolf46.microgamelib.test

import org.bukkit.event.block.BlockBreakEvent
import skywolf46.microgamelib.annotations.InGameListener

class TestListenerObject {
    @InGameListener
    fun BlockBreakEvent.hello() {
        println("Hello, this is Main stage")
    }
}