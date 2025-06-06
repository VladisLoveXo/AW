package app.awake.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import app.awake.TreeMode
import app.awake.TreeProgress
import java.util.Date
import java.util.UUID

@Entity
data class TreeStage(
    @PrimaryKey val id: UUID,

    @ColumnInfo(name = "index") var index: Int,
    @ColumnInfo(name = "treeId") val treeId: UUID,

    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "estimated_days") var estimatedDays: Int,
    @ColumnInfo(name = "task") var task: String,
    @ColumnInfo(name = "task_type") var taskType: Int,

    @ColumnInfo(name = "watered") var watered: Int = 0,
    @ColumnInfo(name = "last_water") var lastWater: Date? = null,
)
typealias TREE_STAGES = MutableList<TreeStage>

@Dao
interface TreeStageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg stages: TreeStage)
}

@Entity(tableName = "tree")
data class TreeData(
    @PrimaryKey var id: UUID,

    @ColumnInfo(name = "index") var index: Int,

    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "mode") var mode: Int = TreeMode.standard.id,
    @ColumnInfo(name = "planting_date") var plantingDate: Date,
    @ColumnInfo(name = "finish_date") var finishDate: Date? = null,
    @ColumnInfo(name = "selected_pack_id") var selectedPackId: String,

    @ColumnInfo(name = "progress") var progress: Int = TreeProgress.growing.id,
    @ColumnInfo(name = "last_stage_id") var lastStageId: UUID,
    @ColumnInfo(name = "current_tree_index") var currentTreeIndex: Int = 0,
)
typealias TREES = MutableList<TreeData>

@Dao
interface TreeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tree: TreeData)

    @Transaction
    @Query("SELECT * FROM tree")
    fun getAllTreesWithStages(): MutableList<TreeWithStages>

    @Delete
    fun delete(tree: TreeData)
}

@Entity
data class TreeWithStages(
    @Embedded val tree: TreeData,
    @Relation(
        parentColumn = "id",
        entityColumn = "treeId"
    )
    val stages: TREE_STAGES
)
typealias TREES_WITH_STAGES = MutableList<TreeWithStages>

fun getSortedStages(stages: TREE_STAGES): TREE_STAGES {
    return stages.sortedBy { it.index }.toMutableList()
}

fun isGrowing(tree: TreeData): Boolean {
    if (TreeProgress.fromInt(tree.progress) == TreeProgress.growing) {
        return true
    }

    return false
}

fun isWithered(tree: TreeData): Boolean {
    if (TreeProgress.fromInt(tree.progress) == TreeProgress.withered) {
        return true
    }

    return false
}

fun isGrownUp(tree: TreeData): Boolean {
    if (TreeProgress.fromInt(tree.progress) == TreeProgress.grownUp) {
        return true
    }

    return false
}