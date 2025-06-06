package app.awake.model

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.awake.ConfigurationController
import app.awake.DailyText
import app.awake.DailyTexts
import app.awake.ENTER_COUNTER
import app.awake.Localization
import app.awake.MUSIC_ITEMS
import app.awake.MUSIC_TOOLS
import app.awake.Mode
import app.awake.MusicItem
import app.awake.MusicTool
import app.awake.Musics
import app.awake.NotificationsController
import app.awake.Packs
import app.awake.R
import app.awake.ReadJSONFromAssets
import app.awake.RemoteNotificationsController
import app.awake.TREE_PACKS
import app.awake.TreePack
import app.awake.data.AppDatabase
import app.awake.data.MESSAGES
import app.awake.data.MainData
import app.awake.data.MainDataDao
import app.awake.data.MessageDao
import app.awake.data.MessageData
import app.awake.data.NOTIFICATIONS
import app.awake.data.NotificationDao
import app.awake.data.NotificationData
import app.awake.data.SettingsDao
import app.awake.data.SettingsData
import app.awake.data.TREES_WITH_STAGES
import app.awake.data.TreeDao
import app.awake.data.TreeData
import app.awake.data.TreeStageDao
import app.awake.data.TreeWithStages
import app.awake.data.TreesSettingsDao
import app.awake.data.TreesSettingsData
import app.awake.data.isGrowing
import app.awake.data.isGrownUp
import app.awake.data.isWithered
import app.awake.dataStore
import app.awake.getHoursInterval
import app.awake.player.MusicPlayer
import com.google.gson.Gson
import java.util.UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.max




@HiltViewModel // todo: refactor to simple viewModel
class DataViewModel @Inject constructor() : ViewModel() {
    private var defaultMessages: MESSAGES = mutableListOf()

    var enterCount: Int = 0

    var isSplashMain = MutableLiveData(true)
    var isShowSplash = MutableLiveData(true)

    private var isLoading = false

    var dailyTexts = DailyTexts(mutableListOf())
        private set
    //var musics: MUSIC_ITEMS = mutableListOf()
    var musics: MUSIC_TOOLS = mutableListOf()
        private set
    var treePacks: TREE_PACKS = mutableListOf()
        private set

    fun defaultTreePackId(): String {
        if (treePacks.count() > 0) {
            return treePacks[0].id
        }

        return "none"
    }

    fun findPackById(id: String): TreePack? {
        for (pack in treePacks) {
            if (pack.id == id) {
                return pack
            }
        }

        return null
    }

    // Returns -1 if musics not playing
    fun findPlayingIndex(): Int {
        var res = -1

        musics.forEachIndexed { index, music ->
            if (music.player.isPlaying.value!!) {
                res = index
            }
        }

        return res
    }

    fun maxNotifications(): Int {
        var maxNotificationsCount: Int = 6

        val settings = getSettings()

        val hInterval = getHoursInterval(start = settings.startOfTheDay, end = settings.endOfTheDay)

        val mode = Mode.fromInt(settings.mode)
        when (mode) {
            Mode.standard -> maxNotificationsCount = min(6, hInterval / 2)
            Mode.intensive -> maxNotificationsCount = min(12, (hInterval * 60) / 30)
        }

        maxNotificationsCount = max(1, maxNotificationsCount)

        return maxNotificationsCount
    }

    // main data
    private var mainData: MutableList<MainData> = mutableListOf()

    fun dailyText(): DailyText {
        if (mainData.count() > 0) {
            if (mainData[0].dailyTextIndex >= dailyTexts.texts.count()) {
                updateDailyText()
            }

            if (dailyTexts.texts.count() > 0) {
                return dailyTexts.texts[max(0, mainData[0].dailyTextIndex)]
            }
        }

        return DailyText("")
    }

    // messages
    var messages = MutableLiveData<MESSAGES>(mutableListOf())
    var activeMessages = MutableLiveData<MESSAGES>(mutableListOf())

    // trees
    var trees = MutableLiveData<TREES_WITH_STAGES>(mutableListOf())

    fun findTreeById(id: UUID): TreeWithStages? {
        for (tree in trees.value!!) {
            if (tree.tree.id == id) {
                return tree
            }
        }

        return null
    }

    // data
    var appDatabase: AppDatabase? = null

    private var mainDataDao: MainDataDao? = null
    private var messageDao: MessageDao? = null
    private var treeDao: TreeDao? = null
    private var treeStageDao: TreeStageDao? = null
    private var settingsDao: SettingsDao? = null
    private var treesSettingsDao: TreesSettingsDao? = null

    private var notificationDao: NotificationDao? = null

    // ui
    var isShowTreesPreview = MutableLiveData<Boolean>(true) // todo
    var isShowMusicsPreview = MutableLiveData<Boolean>(true)


    // ---------- loading ---------- //
    private fun loadDailyTexts(context: Context) {
        val filename = Localization.getLocalizedFilename(context, "dailyTexts") + ".json"

        val jsonString = ReadJSONFromAssets(context, filename)
        dailyTexts = Gson().fromJson(jsonString, DailyTexts::class.java)
    }

    private fun loadMusics(context: Context) {
        var jsonString = ReadJSONFromAssets(context, "musics/musics.json")
        var musicsData = Gson().fromJson(jsonString, Musics::class.java)

        for (item in musicsData.musics) {
            val filename = Localization.getLocalizedFilename(context, "musics/$item/info") + ".json"

            jsonString = ReadJSONFromAssets(context, filename)
            val musicItem = Gson().fromJson(jsonString, MusicItem::class.java)

            musics.add(MusicTool(item = musicItem, player = MusicPlayer()))
        }
    }

    private fun loadTreePacks(context: Context)
    {
        var jsonString = ReadJSONFromAssets(context, "tree_packs/treePacks.json")
        var packsData = Gson().fromJson(jsonString, Packs::class.java)

        for (item in packsData.packs) {
            val filename = "tree_packs/$item/info.json"

            jsonString = ReadJSONFromAssets(context, filename)
            val treePack = Gson().fromJson(jsonString, TreePack::class.java)
            treePacks.add(treePack)
        }
    }

    private var isInitialized = false

    // ---------- init ---------- //
    fun initialize(context: Context) {
        if (!isInitialized) {

            // setup configuration controller
            ConfigurationController.getInstance().initConfiguration(context)

            // create notification channels
            NotificationsController.getInstance().createNotificationChannels(context)

            // default messages
            defaultMessages = mutableListOf(
                    MessageData(id = UUID.fromString("1fa7c640-56b4-11ee-8c99-0242ac120002"), index = 0, text = context.resources.getString(R.string.message_default_1)),
                    MessageData(id = UUID.fromString("306772b4-56b4-11ee-8c99-0242ac120002"), index = 1, text = context.resources.getString(R.string.message_default_2)),
                    MessageData(id = UUID.fromString("35df1df0-56b4-11ee-8c99-0242ac120002"), index = 2, text = context.resources.getString(R.string.message_default_3)),
                    MessageData(id = UUID.fromString("3a90bdb8-56b4-11ee-8c99-0242ac120002"), index = 3, text = context.resources.getString(R.string.message_default_4)),
                    MessageData(id = UUID.fromString("e88357be-56dc-11ee-8c99-0242ac120002"), index = 4, text = context.resources.getString(R.string.message_default_5)),
                )

            // enter count
            runBlocking {
                enterCount = context.dataStore.data.first()[app.awake.ENTER_COUNTER] ?: 0
            }

            // data base
            appDatabase = AppDatabase.getInstance(context)

            mainDataDao = appDatabase!!.mainDataDao()
            messageDao = appDatabase!!.messageDao()
            treeDao = appDatabase!!.treeDao()
            treeStageDao = appDatabase!!.treeStageDao()
            settingsDao = appDatabase!!.settingsDao()
            treesSettingsDao = appDatabase!!.treesSettingsDao()

            notificationDao = appDatabase!!.notificationDao()

            // loading data
            loadDailyTexts(context)
            loadMusics(context)
            loadTreePacks(context)

            // init
            isLoading = true

            loadData()
            loadSettings()
            loadTreesSettings()

            updateMessages()
            updateTrees()

            isLoading = false

            isInitialized = true
        }
    }

    // ---------- settings ---------- //
    private fun loadSettings() {
        val settings = settingsDao!!.getAll()

        if (settings.count() == 0) {
            val newSettings = SettingsData()
            settingsDao!!.insert(newSettings)
        }
    }

    fun setSettings(settings: SettingsData) {
        settingsDao!!.insert(settings)
    }

    fun getSettings(): SettingsData {
        val settings = settingsDao!!.getAll()

        if (settings.count() > 0) {
            return settings[0]
        }

        return SettingsData() // failed
    }

    // ---------- trees settings ---------- //
    private fun loadTreesSettings() {
        val treeSettings = treesSettingsDao!!.getAll()

        if (treeSettings.count() == 0) {
            val newTreeSettings = TreesSettingsData()
            treesSettingsDao!!.insert(newTreeSettings)
        }
    }

    fun setTreesSettings(settings: TreesSettingsData) {
        treesSettingsDao!!.insert(settings)
    }

    fun getTreesSettings(): TreesSettingsData {
        val treeSettings = treesSettingsDao!!.getAll()

        if (treeSettings.count() > 0) {
            return treeSettings[0]
        }

        return TreesSettingsData() // failed
    }

    // ---------- data ---------- //
    private fun loadData() {
        val data = mainDataDao!!.getAll()

        if (data.count() > 0) {
            mainData = data

            if (mainData[0].lastDay != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                updateDailyText()
            }
        } else {
            val newData = MainData(
                dailyTextIndex = generateDailyTextIndex(),
                lastDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            mainDataDao?.insert(newData)
            mainData = mainDataDao!!.getAll()
        }
    }

    private fun generateDailyTextIndex(): Int {
        val  oldIndex = if (mainData.count() > 0) mainData[0].dailyTextIndex else -1;
        var newIndex = oldIndex

        while (newIndex == oldIndex) {
            newIndex = (0..dailyTexts.texts.count()).random()
        }

        return newIndex
    }

    private fun updateDailyText() {
        if (mainData.count() > 0) {
            mainData[0].dailyTextIndex = generateDailyTextIndex()
            mainData[0].lastDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            mainDataDao?.insert(mainData[0])
        }
    }

    // ---------- messages ---------- //
    private fun updateMessages() {
        var dataMessages = messageDao!!.getAll()
        messages.value = dataMessages.sortedBy { it.index }.reversed().toMutableList()
        activeMessages.value = getActiveMessages().reversed().toMutableList()

        if (!isLoading) {
            RemoteNotificationsController.getInstance().notifyAboutMessagesChanged()
        }

        /*viewModelScope.launch(Dispatchers.IO) {
            var dataMessages = messagesDao!!.getAll()

            viewModelScope.launch(Dispatchers.Main) {
                _messages.value = dataMessages
            }
        }*/
    }

    fun addMessage(message: MessageData) {
        messageDao!!.insert(message)
        updateMessages()
    }

    fun deleteMessage(message: MessageData) {
        messageDao!!.delete(message)
        updateMessages()
    }

    fun addDefaultMessages() {
        isLoading = true

        for (message in defaultMessages) {
            addMessage(message)
        }

        isLoading = false
    }

    fun getNextMessageIndex(): Int {
        var index = 0

        for (message in messages.value!!) {
            val nextIndex = message.index + 1

            if (nextIndex > index) {
                index = nextIndex
            }
        }

        return max(index, messages.value!!.count())
    }

    private fun getActiveMessages(): MESSAGES {
        var res: MESSAGES = mutableListOf()

        for (message in messages.value!!) {
            if (message.isSelected) {
                res.add(message)
            }
        }

        return res.sortedBy { it.index }.toMutableList()
    }

    // ---------- trees ---------- //
    private fun updateTrees() {
        var treesData = treeDao!!.getAllTreesWithStages()
        trees.value = treesData.sortedBy { it.tree.index }.reversed().toMutableList()

        if (!isLoading) {
            RemoteNotificationsController.getInstance().notifyAboutTreesSettingsChanged()
        }
    }

    fun getGrowingTrees(): TREES_WITH_STAGES {
        var growingTrees: TREES_WITH_STAGES = mutableListOf()

        for (tree in trees.value!!) {
            if (isGrowing(tree.tree)) {
                growingTrees.add(tree)
            }
        }

        return growingTrees.sortedBy { it.tree.index }.reversed().toMutableList()
    }

    fun getGrownTrees(): TREES_WITH_STAGES {
        var grownTrees: TREES_WITH_STAGES = mutableListOf()

        for (tree in trees.value!!) {
            if (isGrownUp(tree.tree)) {
                grownTrees.add(tree)
            }
        }

        return grownTrees.sortedBy { it.tree.index }.reversed().toMutableList()
    }

    fun getWitheredTrees(): TREES_WITH_STAGES {
        var witheredTrees: TREES_WITH_STAGES = mutableListOf()

        for (tree in trees.value!!) {
            if (isWithered(tree.tree)) {
                witheredTrees.add(tree)
            }
        }

        return witheredTrees.sortedBy { it.tree.index }.reversed().toMutableList()
    }

    fun addTree(tree: TreeWithStages) {
        treeDao!!.insert(tree.tree)

        for (stage in tree.stages) {
            treeStageDao!!.insert(stage)
        }

        updateTrees()
    }

    fun updateTree(tree: TreeData) {
        treeDao!!.insert(tree)

        updateTrees()
    }

    fun deleteTree(tree: TreeWithStages) {
        treeDao!!.delete(tree.tree) // todo: it's delete stages too?

        updateTrees()
    }

    fun getNextTreeIndex(): Int {
        var index = 0

        for (tree in trees.value!!) {
            val nextIndex = tree.tree.index + 1

            if (nextIndex > index) {
                index = nextIndex
            }
        }

        return max(index, trees.value!!.count())
    }

    fun waterTree(id: UUID, stageId: UUID) {
        val tree = findTreeById(id)

        if (tree != null) {
            for (stage in tree.stages) {
                if (stage.id == stageId) {
                    stage.watered += 1
                    stage.lastWater = Date()

                    treeStageDao!!.insert(stage)

                    break
                }
            }
        }
    }

    fun isHaveGrowingTrees(): Boolean {
        for (tree in trees.value!!) {
            if (isGrowing(tree.tree)) {
                return true
            }
        }

        return false
    }

    // ---------- notifications ---------- //
    fun getAllNotifications(): NOTIFICATIONS
    {
        return notificationDao!!.getAll()
    }

    fun addNotification(notification: NotificationData) {
        notificationDao!!.insert(notification)
    }

    fun deleteNotification(notification: NotificationData) {
        notificationDao!!.delete(notification)
    }

    fun deleteAllNotifications()
    {
        var allNotifications = getAllNotifications()

        for (notification in allNotifications) { // todo: maybe getAll() here?
            deleteNotification(notification)
        }
    }

    fun deleteNotificationsById(deleteNotificationsId: MutableList<Int>) {
        var allNotifications = getAllNotifications()

        for (id in deleteNotificationsId)
        {
            for (notification in allNotifications)
            {
                if (id == notification.id)
                {
                    deleteNotification(notification)
                    break
                }
            }
        }
    }

    fun getNextNotificationId(): Int {
        var allNotifications = getAllNotifications()

        var id = 0

        for (notification in allNotifications) {
            val nextId = notification.id + 1

            if (nextId > id) {
                id = nextId
            }
        }

        return max(id, allNotifications.count())
    }
}