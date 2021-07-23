package skywolf46.microgamelib.api.systems

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import skywolf46.extrautility.events.interaction.PlayerRightClickEvent
import skywolf46.extrautility.util.amount
import skywolf46.microgamelib.annotations.InGameListener
import skywolf46.microgamelib.annotations.Inject
import skywolf46.microgamelib.api.abstraction.AbstractAttachableSystem
import skywolf46.microgamelib.api.abstraction.IAttachableSystem
import skywolf46.microgamelib.api.abstraction.IGadget
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.inject.util.DynamicEventHandle

class MicroGadgetLobby : AbstractAttachableSystem() {
    @Inject
    lateinit var dynamicListener: DynamicEventHandle

    private val gadgets = mutableListOf<IGadget>()

    fun registerGadget(item: ItemStack, gadget: IGadget) {
        gadgets += gadget
        dynamicListener.bindListenerTo(InjectScope.STAGE, gadget)
    }

    override fun onAttach() {
        for (x in party.getAllPlayers()) {
            gadgets.forEachIndexed { index, gadget ->
                x.inventory.setItem(index, gadget.getGadgetItem())
            }
        }
    }

    override fun onDetach() {
        for (x in party.getAllPlayers()) {
            x.inventory.clear()
        }
    }

    override fun hasWinner(): Boolean {
        return false
    }
}