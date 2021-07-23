package skywolf46.microgamelib.data

import skywolf46.asyncdataloader.core.abstraction.enums.LoadState
import skywolf46.asyncdataloader.core.abstraction.loader.AbstractDataLoader
import skywolf46.asyncdataloader.core.abstraction.loader.AbstractDataSnapshot
import skywolf46.asyncdataloader.file.impl.BukkitYamlBasedSnapshot
import skywolf46.asyncdataloader.file.impl.loadBukkitYaml
import skywolf46.extrautility.util.callEvent
import skywolf46.extrautility.util.schedule
import skywolf46.microgamelib.MicroGameLib
import skywolf46.microgamelib.enums.InjectScope
import skywolf46.microgamelib.api.events.gameEvent.*
import skywolf46.microgamelib.inject.util.ConditionScheduler
import skywolf46.microgamelib.storage.InjectReference
import skywolf46.microgamelib.storage.InjectorClassManagerStorage
import java.io.File

class GameInstanceObject : AbstractDataLoader<GameInstanceObject> {

    var gameStagePointer = 0
    lateinit var config: ConfigurationStructure

    val instanceName: String
    val gameData: GameInstanceData

    var projectArgument: InjectReference? = null
    var stageArgument: InjectReference? = null
    var currentStage: Any? = null
    var conditionScheduler: ConditionScheduler = ConditionScheduler()

    constructor(stageName: String, gameData: GameInstanceData) {
        this.instanceName = stageName
        this.gameData = gameData
        init()
    }

    constructor(stageName: String, gameData: GameInstanceData, config: ConfigurationStructure) {
        this.instanceName = stageName
        this.gameData = gameData
        this.config = config
        loaded.set(LoadState.EMPTY_LOAD)
        init()
    }

    private fun init() {
        if (!loaded.get().isLoadedState) {
            loadBukkitYaml(File(MicroGameLib.inst.dataFolder, "Stages/$instanceName")) {
                config = gameData.gameConfiguration!!.loadFromSection(this)
            }
        }
    }


    fun toStage(name: String) {
        gameStagePointer = gameData.findStageIndex(name)
    }


    override fun snapshot(): AbstractDataSnapshot {
        return object : BukkitYamlBasedSnapshot(File(MicroGameLib.inst.dataFolder, "Stages/$instanceName")) {
            override fun trigger(isFinalizing: Boolean) {
                yaml {
                    this["Game"] = gameData.gameName
                    this["Stage"] = instanceName
                    config.saveToSection(this.createSection("Data"))
                }
            }
        }
    }

    fun getGameStage() = gameStagePointer

    fun getGameStageObject() = gameData.getStage(gameStagePointer)

    fun start() {
        gameStagePointer = -1
        projectArgument = InjectReference().apply {
            addProxy(InjectorClassManagerStorage.globalVariable)
            addArgument(config.constructToInstance())
            addArgument(this@GameInstanceObject)
            addArgument(GameInstanceWatcher())
            addArgument(conditionScheduler)
        }
        InjectorClassManagerStorage.of(InjectScope.GAME)
            .applyReferences(InjectorClassManagerStorage.globalVariable, projectArgument!!, this)
        GameStartedEvent(this).callEvent()
        nextStage()
    }

    fun nextStage() {
        schedule { enqueueNextStage() }
    }

    private fun enqueueNextStage() {
        StageChangedEvent(this).callEvent()
        if (++gameStagePointer >= gameData.gameStages.size) {
            conditionScheduler.endScope(InjectScope.GAME)
            reset()
            return
        }
        val cachedArgument = stageArgument
        stageArgument = InjectReference()
        if (cachedArgument != null) {
            projectArgument!!.removeProxy(cachedArgument)
            cachedArgument.unregisterAllListener()
            cachedArgument.extractTo(cachedArgument)
        }

        InjectorClassManagerStorage.of(InjectScope.STAGE).applyReferences(projectArgument, stageArgument!!, this)
        currentStage = getGameStageObject().constructStage(stageArgument!!)
        stageArgument!!.registerAllListeners(currentStage!!, stageArgument!!, this)
        StageAfterChangedEvent(this).callEvent()
    }


    fun reset() {
        GameEndedEvent(this).callEvent()
        conditionScheduler.endScope(InjectScope.STAGE)
        conditionScheduler.endScope(InjectScope.GAME)
        gameStagePointer = 0
        stageArgument!!.unregisterAllListener()
        projectArgument!!.unregisterAllListener()
        projectArgument = null
        stageArgument = null
        println("Stop stage")
        GameAfterEndedEvent(this).callEvent()
        if (gameData.alwaysStarted) {
            GameRestartEvent(this).callEvent()
            start()
        }
    }

}