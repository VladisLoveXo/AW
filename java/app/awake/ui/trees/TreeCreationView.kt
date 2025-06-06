package app.awake.ui.trees

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.awake.MainActivityReceiver
import app.awake.R
import app.awake.TREE_PACKS
import app.awake.TextFieldInfo
import app.awake.TreeMode
import app.awake.TreeStages
import app.awake.data.TreeData
import app.awake.data.TreeStage
import app.awake.data.TreeWithStages
import app.awake.ui.buttons.CombinedButton
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme
import app.awake.ui.trees.TreeCreationStages.FINAL_INFO
import app.awake.ui.trees.TreeCreationStages.MODE
import app.awake.ui.trees.TreeCreationStages.NAME
import app.awake.ui.trees.TreeCreationStages.STAGE1
import app.awake.ui.trees.TreeCreationStages.STAGE2
import app.awake.ui.trees.TreeCreationStages.STAGE3
import java.util.Date
import java.util.UUID

object TreeCreationStages {
    const val NAME = "name"
    const val MODE = "mode"
    const val STAGE1 = "stage_1"
    const val STAGE2 = "stage_2"
    const val STAGE3 = "stage_3"
    const val FINAL_INFO = "final_info"
}

@Composable
fun TreeCreationView(
    treeIndex: Int,
    treePacks: TREE_PACKS,
    defaultPackId: String,
    modifier: Modifier = Modifier,
    action: (TreeWithStages)->Unit
) {
    var selectedPackId by remember { mutableStateOf(defaultPackId) }

    var treeId by remember { mutableStateOf(UUID.randomUUID()) }

    var name by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf(TreeMode.standard.id) }

    var lastStage by remember { mutableStateOf<TreeStage?>(null) }
    var stages by remember { mutableStateOf(mutableListOf<TreeStage>()) }

    val navController = rememberNavController()

    fun addStage(stage: TreeStage) {
        stages.add(stage)
        lastStage = null
    }

    fun goToFinal() {
        navController.navigate(FINAL_INFO) // todo: animation 1 second
    }

    NavHost(
        navController = navController,
        startDestination = NAME,
        modifier = modifier
    ) {
        composable(NAME) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // ----- title ----- //
                CochinText(
                    text = stringResource(id = R.string.tree_creation),
                    size = 36,
                    weight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(all = 18.dp)
                )

                CochinText(
                    text = stringResource(id = R.string.tree_info),
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 24.dp)
                )

                // ----- select tree ----- //
                CochinText(
                    text = stringResource(id = R.string.select_tree),
                    size = 22,
                    weight = FontWeight.Bold
                )

                Crossfade(targetState = selectedPackId, label = "tree_pack_picker") {
                    TreePackPicker(
                        selectedPackId = it,
                        treePacks = treePacks,
                        modifier = Modifier
                            .padding(top = 18.dp, bottom = 18.dp)
                    ) {
                        selectedPackId = it
                    }
                }

                // ----- name ----- //
                CochinText(
                    text = stringResource(id = R.string.tree_name),
                    size = 22,
                    weight = FontWeight.Bold
                )

                CochinText(
                    text = stringResource(id = R.string.treeName_info),
                    modifier = Modifier
                        .padding(top = 12.dp, start = 18.dp, end = 18.dp)
                )

                TextButton(
                    border = BorderStroke(width = Theme.Sizes.lineWidth.dpSizeValue.width, color = Color.Black),
                    shape = RoundedCornerShape(size = 4.dp),
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(all = 18.dp),
                    onClick = {
                        MainActivityReceiver.getInstance().pushData(
                            data = TextFieldInfo(
                                text = name,
                                placeholder = R.string.enter_name
                            ) { result ->
                                name = result

                                navController.navigate(MODE)
                            }
                        )
                    }
                ) {
                    Crossfade(targetState = name, label = "tree_name") {
                        CochinText(
                            text = if (it.isEmpty()) stringResource(id = R.string.enter_name) else it,
                            modifier = Modifier
                                .padding(6.dp)
                        )
                    }
                }
            }
        }

        composable(MODE) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CochinText(
                    text = stringResource(id = R.string.tree_mode),
                    size = 36,
                    weight = FontWeight.Bold,
                    modifier = Modifier.padding(all = 18.dp)
                )

                CochinText(
                    text = stringResource(id = R.string.treeMode_info),
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp, top = 12.dp)
                )

                Crossfade(targetState = mode, label = "tree_mode") {
                    TreeModes(
                        selectedMode = it,
                        modifier = Modifier
                            .padding(
                                top = 38.dp,
                                bottom = 20.dp
                            )
                    )
                    { newMode ->
                        mode = newMode
                    }
                }

                CombinedButton(
                    image = painterResource(id = R.drawable.checkmark),
                    string = stringResource(id = R.string.select_mode)
                ) {
                    navController.navigate(STAGE1)
                }
            }
        }

        composable(STAGE1) {
            TreeStageCreationView(
                treeId = treeId,
                stageIndex = stages.count(),
                title = stringResource(id = TreeStages.one.string),
                _name = lastStage?.name ?: "",
                _estimatedDays = lastStage?.estimatedDays ?: 7,
                _task = lastStage?.task ?: "",
                addAction = { stage ->
                    addStage(stage)

                    navController.navigate(STAGE2)
                },
                doneAction = { stage ->
                    addStage(stage)
                    goToFinal()
                }
            )
        }

        composable(STAGE2) {
            TreeStageCreationView(
                treeId = treeId,
                stageIndex = stages.count(),
                title = stringResource(id = TreeStages.two.string),
                _name = lastStage?.name ?: "",
                _estimatedDays = lastStage?.estimatedDays ?: 7,
                _task = lastStage?.task ?: "",
                backAction = { ->
                    lastStage = stages.removeLast()

                    navController.navigate(STAGE1)
                },
                addAction = { stage ->
                    addStage(stage)

                    navController.navigate(STAGE3)
                },
                doneAction = { stage ->
                    addStage(stage)
                    goToFinal()
                }
            )
        }

        composable(STAGE3) {
            TreeStageCreationView(
                treeId = treeId,
                stageIndex = stages.count(),
                title = stringResource(id = TreeStages.three.string),
                _name = lastStage?.name ?: "",
                _estimatedDays = lastStage?.estimatedDays ?: 7,
                _task = lastStage?.task ?: "",
                backAction = { ->
                    lastStage = stages.removeLast()

                    navController.navigate(STAGE2)
                },
                doneAction = { stage ->
                    addStage(stage)
                    goToFinal()
                }
            )
        }

        composable(FINAL_INFO) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CochinText(
                    text = stringResource(id = R.string.tree_conclusion),
                    size = 46,
                    modifier = Modifier
                        .padding(18.dp)
                )

                CochinText(
                    text = stringResource(id = if (TreeMode.fromInt(mode) == TreeMode.standard) R.string.treeConclusion_info_standard else R.string.treeConclusion_info_intensive),
                    modifier = Modifier
                        .padding(18.dp)
                        .padding(start = 18.dp, end = 18.dp)
                )

                IconButton(onClick = {
                    val tree = TreeData(
                        id = treeId,
                        index = treeIndex,
                        name = name,
                        mode = mode,
                        plantingDate = Date(),
                        selectedPackId = selectedPackId,
                        lastStageId = stages[0].id
                    )

                    action(
                        TreeWithStages(
                            tree = tree,
                            stages = stages
                        )
                    )
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.checkmark),
                        contentDescription = "checkmark_on_final"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview32() {
    AwakeTheme {
        TreeCreationView(
            treeIndex = 0,
            treePacks = mutableListOf(),
            defaultPackId = ""
        ) {}
    }
}