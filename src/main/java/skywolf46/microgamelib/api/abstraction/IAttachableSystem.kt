package skywolf46.microgamelib.api.abstraction

interface IAttachableSystem {
    open fun onAttach() {
        // No content for implementation
    }

    open fun onDetach() {
        // No content for implementation
    }

    fun bindUnregisterLambda(unit: () -> Unit)

    open fun checkWinner(): IGameResult? {
        return null
    }

    fun hasWinner() : Boolean
}