package app.awake

import android.content.Context
import android.media.AsyncPlayer
import androidx.core.os.ConfigurationCompat
import app.awake.data.TreeStage
import app.awake.data.TreeWithStages
import app.awake.data.getSortedStages
import app.awake.data.isGrowing
import app.awake.data.isGrownUp
import app.awake.data.isWithered
import app.awake.model.DataViewModel
import app.awake.player.MusicPlayer
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

enum class Localization { // todo: fix bug with first open with russian language (also default messages)
    eng,
    rus;

    companion object {
        fun current(context: Context): Localization {
            var res = eng

            val configuration = context.resources.configuration
            var locale = ConfigurationCompat.getLocales(configuration).get(0).toString()

            if (locale == "ru_RU") {
                res = rus
            }

            return res
        }

        fun getLocalizedFilename(context: Context, filename: String, locale: Localization = Localization.current(context)): String {
            var result = filename

            when (locale) {
                eng -> result += "_eng"
                rus -> result += "_rus"
            }

            return result
        }
    }
}

enum class Month {
    january {
        override val index: Int = 0
        override val fullName: Int = R.string.january
    },

    february {
        override val index: Int = 1
        override val fullName: Int = R.string.february
    },

    march {
        override val index: Int = 2
        override val fullName: Int = R.string.march
    },

    april {
        override val index: Int = 3
        override val fullName: Int = R.string.april
    },

    may {
        override val index: Int = 4
        override val fullName: Int = R.string.may
    },

    june {
        override val index: Int = 5
        override val fullName: Int = R.string.june
    },

    july {
        override val index: Int = 6
        override val fullName: Int = R.string.july
    },

    august {
        override val index: Int = 7
        override val fullName: Int = R.string.august
    },

    september {
        override val index: Int = 8
        override val fullName: Int = R.string.september
    },

    october {
        override val index: Int = 9
        override val fullName: Int = R.string.october
    },

    november {
        override val index: Int = 10
        override val fullName: Int = R.string.november
    },

    december {
        override val index: Int = 11
        override val fullName: Int = R.string.december
    };

    abstract val index: Int
    abstract val fullName: Int

    companion object {
        fun fromInt(value: Int) = Month.values().first { it.index == value }
    }
}

enum class Day {
    mon {
        override val index: Int = 0
        override val character: Int
            get() = R.string.monday_c

        override val fullName: Int
            get() = R.string.monday
    },

    tue {
        override val index: Int = 1
        override val character: Int
            get() = R.string.tuesday_c

        override val fullName: Int
            get() = R.string.tuesday
    },

    wed {
        override val index: Int = 2
        override val character: Int
            get() = R.string.wednesday_c

        override val fullName: Int
            get() = R.string.wednesday
    },

    thu {
        override val index: Int = 3
        override val character: Int
            get() = R.string.thursday_c

        override val fullName: Int
            get() = R.string.thursday
    },

    fri {
        override val index: Int = 4
        override val character: Int
            get() = R.string.friday_c

        override val fullName: Int
            get() = R.string.friday
    },

    sat {
        override val index: Int = 5
        override val character: Int
            get() = R.string.saturday_c

        override val fullName: Int
            get() = R.string.saturday
    },

    sun {
        override val index: Int = 6
        override val character: Int
            get() = R.string.sunday_c

        override val fullName: Int
            get() = R.string.sunday
    };

    abstract val index: Int
    abstract val character: Int
    abstract val fullName: Int

    companion object {
        fun fromInt(value: Int) = Day.values().first { it.index == value }

        fun fromCalendar(calendar: Calendar): Day {
            var day = Day.mon

            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY     -> day = Day.mon
                Calendar.TUESDAY    -> day = Day.tue
                Calendar.WEDNESDAY  -> day = Day.wed
                Calendar.THURSDAY   -> day = Day.thu
                Calendar.FRIDAY     -> day = Day.fri
                Calendar.SATURDAY   -> day = Day.sat
                Calendar.SUNDAY     -> day = Day.sun
            }

            return day
        }
    }
}
typealias DAYS = MutableSet<Day>

data class TextInfo (var title: String, var text: String)

class ModeInfo (var name: Int, var info: Int, var image: Int)

enum class Mode {
    standard {
        override val id = 0
        override val info =
            ModeInfo(name = R.string.mode_standard, info = R.string.standardMode_info, image = R.drawable.standard_mode)
    },

    intensive {
        override val id = 1
        override val info =
            ModeInfo(name = R.string.mode_intensive, info = R.string.intensiveMode_info, image = R.drawable.intensive_mode)
    };

    abstract val id: Int
    abstract val info: ModeInfo

    companion object {
        fun fromInt(value: Int) = Mode.values().first { it.id == value }
    }
}

class Sound (var id: UUID = UUID.randomUUID(), var name: String, var resourceId: Int, var image: Int)
typealias SOUNDS = MutableList<Sound>

class ButtonInfo(var type: Type = Type.classic, var image: Int, var selected: Boolean = false, var action: () -> Unit) {
    enum class Type {
        classic,
        roundedAndMoving,
    }
}

typealias BUTTONS_INFO = MutableList<ButtonInfo>

data class DailyText (var text: String, var ps: String? = null)
typealias DAILY_TEXTS = MutableList<DailyText>
data class DailyTexts(val texts: DAILY_TEXTS)

class TextFieldInfo(var text: String, var placeholder: Int = R.string.text_placeholder, var action: (String)->Unit)

class QuestionInfo(var question: String = "", var action: (Boolean) -> Unit)

data class SoundItem(var name: String, var path: String, var image: String, var selected: Boolean)
typealias SOUND_ITEMS = MutableList<SoundItem>
typealias ACTIVE_SOUNDS = MutableSet<SoundItem>

data class MusicItem(var name: String, var about: String, var image: String, var sounds: SOUND_ITEMS)
typealias MUSIC_ITEMS = MutableList<MusicItem>

data class MusicTool(var item: MusicItem, var player: MusicPlayer) // var downloader
typealias MUSIC_TOOLS = MutableList<MusicTool>

data class Musics(val musics: MutableList<String>)


enum class EstimateType {
    weeks,
    months;
}


enum class TaskType {
    daily {
        override val id: Int = 0
    },

    weekly {
        override val id: Int = 1
    };

    abstract val id: Int
}


data class TreePackItem(
    var blooming: String,
    var withered: String,

    var bloomingPreview: String,
    var witheredPreview: String,
)

typealias TREE_PACK_ITEMS = MutableList<TreePackItem>

data class TreePack(
    var name: String,
    var id: String,
    var preview: String,
    var trees: TREE_PACK_ITEMS
)

typealias TREE_PACKS = MutableList<TreePack>

data class Packs(var packs: MutableList<String>)

enum class TreeMode {
    standard {
        override val id = 0
        override val info =
            ModeInfo(name = R.string.mode_standard, info = R.string.standardTreeMode_info, image = R.drawable.standard_tree_mode)
    },

    intensive {
        override val id = 1
        override val info =
            ModeInfo(name = R.string.mode_intensive, info = R.string.intensiveTreeMode_info, image = R.drawable.intensive_tree_mode)
    };

    abstract val id: Int
    abstract val info: ModeInfo

    companion object {
        fun fromInt(value: Int) = TreeMode.values().first { it.id == value }
    }
}

enum class TreeStages {
    one {
        override val id: Int = 1
        override val string: Int = R.string.tree_stage_one
    },

    two {
        override val id: Int = 2
        override val string: Int = R.string.tree_stage_two
    },

    three {
        override val id: Int = 3
        override val string: Int = R.string.tree_stage_three
    };

    abstract val id: Int
    abstract val string: Int

    companion object {
        fun fromInt(value: Int) = TreeStages.values().first { it.id == value }
    }
}

enum class TreeProgress {
    growing {
        override val id = 0
    },

    grownUp {
        override val id = 1
    },

    withered {
        override val id = 2
    };

    abstract val id: Int

    companion object {
        fun fromInt(value: Int) = TreeProgress.values().first { it.id == value }
    }
}

class ViewTree(var treeId: UUID) {
    var treeName: String = ""
    var treePreview: String = ""

    var currentStageId: UUID? = null
    var currentStageName: String = ""
    var currentTree: String = ""
    var task: String = ""

    var showProgress: Boolean = false
    var stageDaysProgress: Int = 0
    var watered: Int = 0
    var showBucket: Boolean = true

    var previousTree: String = ""

    fun init(context: Context, tree: TreeWithStages, treePack: TreePack, keepProgress: Boolean = false, dataViewModel: DataViewModel)
    {
        treeId = tree.tree.id

        treeName = tree.tree.name
        treePreview = treePack.preview

        val mode = TreeMode.fromInt(tree.tree.mode)

        if (isGrowing(tree.tree))
        {
            val today = Calendar.getInstance()
            today.get(Calendar.DAY_OF_MONTH)

            // for tests
            //let today = Date().addingTimeInterval((3600) * 1)
            //let today = Date().addingTimeInterval((86400) * 1)

            val plantingCalendar = Calendar.getInstance()
            plantingCalendar.time = tree.tree.plantingDate

            if ((plantingCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))
                && (plantingCalendar.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY))
                && (plantingCalendar.get(Calendar.MINUTE) == today.get(Calendar.MINUTE))
                && ((plantingCalendar.get(Calendar.SECOND) + 2) > today.get(Calendar.SECOND)))
            {
                previousTree = "none"
            }

            // ----- find stage ----- //
            var currentStage: TreeStage? = null

            val datesDistanceDays = getDistanceInDays(today, plantingCalendar)
            var daysSummary = 0

            for (stage in getSortedStages(tree.stages))
            {
                if (datesDistanceDays > (daysSummary + stage.estimatedDays) && stage.index == tree.stages.count() - 1)
                {
                    // last stage
                    currentStage = stage
                }
                else if (datesDistanceDays > (daysSummary + stage.estimatedDays))
                {
                    daysSummary += stage.estimatedDays
                    continue
                }
                else
                {
                    currentStage = stage
                    break
                }
            }

            var progress = TreeProgress.fromInt(tree.tree.progress)
            val progressFactor = if (mode == TreeMode.standard) 0.7f else 1f

            // ----- check stage ----- //
            if (currentStage != null)
            {
                val stagesCount = tree.stages.count() //TreeStages.allCases.count
                val treesInTheStage = treePack.trees.count() / stagesCount

                val currentDayOfTheStage = datesDistanceDays - daysSummary
                var daysRemaining = currentStage.estimatedDays - currentDayOfTheStage

                if (currentStage.index > 0)
                {
                    daysRemaining += 1
                }

                if (tree.tree.lastStageId != currentStage.id)
                {
                    var lastStage: TreeStage? = null

                    for (stage in getSortedStages(tree.stages))
                    {
                        if (stage.id == tree.tree.lastStageId)
                        {
                            lastStage = stage
                            break
                        }
                    }

                    if (lastStage != null)
                    {
                        if (lastStage.watered < (lastStage.estimatedDays.toFloat() * progressFactor).toInt())
                        {
                            progress = TreeProgress.withered

                            // TODO: need transition
                        }
                        else
                        {
                            if (keepProgress)
                            {
                                if (tree.tree.lastStageId != currentStage.id)
                                {
                                    tree.tree.lastStageId = currentStage.id
                                }
                            }

                            if (tree.tree.currentTreeIndex < treePack.trees.count())
                            {
                                previousTree = treePack.trees[tree.tree.currentTreeIndex].blooming
                            }
                        }
                    }
                }
                else
                {
                    if ((currentStage.watered + daysRemaining) < (currentStage.estimatedDays.toFloat() * progressFactor).toInt())
                    {
                        progress = TreeProgress.withered
                    }
                }

                if (progress != TreeProgress.withered)
                {
                    // ----- check grown up ----- //
                    if (currentStage.index == (stagesCount - 1))
                    {
                        if (datesDistanceDays > (daysSummary + currentStage.estimatedDays))
                        {
                            if (currentStage.watered < (currentStage.estimatedDays.toFloat() * progressFactor).toInt())
                            {
                                progress = TreeProgress.withered

                                // TODO: need transition
                            } else {
                                progress = TreeProgress.grownUp

                                if (tree.tree.currentTreeIndex < treePack.trees.count())
                                {
                                    previousTree = treePack.trees[tree.tree.currentTreeIndex].blooming
                                }
                            }
                        }
                    }

                    if (progress == TreeProgress.growing)
                    {
                        currentStageId = currentStage.id
                        currentStageName = currentStage.name
                        task = currentStage.task

                        if (stagesCount == 1)
                        {
                            currentStageName = "($daysRemaining" + context.resources.getString(R.string.tree_remaining) + "): $currentStageName"
                        }
                        else
                        {
                            val stageNumber = context.resources.getString(TreeStages.fromInt(currentStage.index + 1).string)
                            currentStageName = "$stageNumber ($daysRemaining" + context.resources.getString(R.string.tree_remaining) + "): $currentStageName"
                        }

                        // ----- find tree ----- //
                        var treeIndex = 0

                        val maxTreeIndexInCurrentStage = ((treesInTheStage * (currentStage.index + 1)) - 1)
                        val estimatedTreeIndex = (max(0, currentDayOfTheStage - 1) / (currentStage.estimatedDays / treesInTheStage)) + (treesInTheStage * currentStage.index)

                        treeIndex = min(treePack.trees.count() - 2, min(maxTreeIndexInCurrentStage, estimatedTreeIndex.toInt()))

                        //if (currentStage.index != stagesCount - 1)
                        //{
                        //    if  currentDayOfTheStage < (currentStage.estimatedDays / 2)
                        //    {
                        //        // first tree
                        //        treeIndex = treesInTheStage * currentStage.index
                        //    }
                        //    else
                        //    {
                        //        // second tree
                        //        treeIndex = (treesInTheStage * currentStage.index) + 1
                        //    }
                        //}
                        //else
                        //{
                        //    treeIndex = treesInTheStage * currentStage.index
                        //}

                        if (treeIndex < treePack.trees.count())
                        {
                            currentTree = treePack.trees[treeIndex].blooming
                            treePreview = treePack.trees[treeIndex].bloomingPreview

                            if (keepProgress)
                            {
                                if (tree.tree.currentTreeIndex != treeIndex)
                                {
                                    tree.tree.currentTreeIndex = treeIndex
                                }
                            }
                        }

                        // ----- find progress ----- //
                        watered = currentStage.watered
                        stageDaysProgress = datesDistanceDays.toInt() - daysSummary

                        if (currentStage.lastWater != null)
                        {
                            //print("TREE DAY  today: \(today.day), start: \(startOfTheDay.day)\n")
                            //print("TREE HOUR today: \(today.hour), start: \(startOfTheDay.hour)\n")

                            val waterCalendar = Calendar.getInstance()
                            waterCalendar.time = currentStage.lastWater!!

                            if (today.get(Calendar.DAY_OF_MONTH) == waterCalendar.get(Calendar.DAY_OF_MONTH))
                            { //} && today.hour <= startOfTheDay.hour {
                                showBucket = false
                            }
                        }
                    }
                }

                if (keepProgress)
                {
                    if (tree.tree.progress != progress.id)
                    {
                        tree.tree.progress = progress.id

                        if (isWithered(tree.tree) || isGrownUp(tree.tree))
                        {
                            tree.tree.finishDate = Date()
                        }
                    }

                    dataViewModel.updateTree(tree.tree)
                }
            }
            else
            {
                print("ViewTree creation error")
            }
        }

        if (isWithered(tree.tree))
        {
            showBucket = false
            currentStageName = context.resources.getString(R.string.tree_withered)

            if (tree.tree.currentTreeIndex < treePack.trees.count())
            {
                currentTree = treePack.trees[tree.tree.currentTreeIndex].withered
                treePreview = treePack.trees[tree.tree.currentTreeIndex].witheredPreview
            }
        }
        else if (isGrownUp(tree.tree))
        {
            showBucket = false
            currentStageName = context.resources.getString(R.string.tree_grownUp)

            if (treePack.trees.last() != null)
            {
                currentTree = treePack.trees.last().blooming
            }
        }
        else if (currentStageName.isEmpty() || (isGrowing(tree.tree) && !previousTree.isEmpty() && !keepProgress))
        {
            currentStageName = context.resources.getString(R.string.tree_checkTree)

            if (tree.tree.currentTreeIndex < treePack.trees.count())
            {
                treePreview = treePack.trees[tree.tree.currentTreeIndex].bloomingPreview
            }
        }
        else if (isGrowing(tree.tree))
        {
            if (mode != TreeMode.intensive)
            {
                showProgress = true
            }
        }

        if (tree.tree.finishDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = tree.tree.finishDate!!

            val month = Month.fromInt(calendar.get(Calendar.MONTH))

            currentStageName += "${calendar.get(Calendar.DAY_OF_MONTH)} ${context.getString(month.fullName)} ${calendar.get(Calendar.YEAR)}"
        }
    }
}



