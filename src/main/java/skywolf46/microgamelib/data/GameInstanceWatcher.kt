package skywolf46.microgamelib.data

class GameInstanceWatcher {
    private val watched = mutableListOf<Class<*>>()
    fun watchInitialized(cls: Class<*>) {
        watched += cls
    }

    fun isInitialized(cls: Class<*>): Boolean {
        return cls in watched
    }
}