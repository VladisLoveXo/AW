package app.awake.ui.trees

import android.util.Config
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.ConfigurationController
import app.awake.MainActivityReceiver
import app.awake.R
import app.awake.TaskType
import app.awake.TextFieldInfo
import app.awake.data.TreeStage
import app.awake.ui.buttons.CombinedButton
import app.awake.ui.controls.EstimatePicker
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.seconds


@Composable
fun TreeStageCreationView(
    treeId: UUID,
    stageIndex: Int,
    title: String,
    _name: String = "",
    _estimatedDays: Int = 7,
    _task: String = "",
    _taskType: TaskType = TaskType.daily,
    modifier: Modifier = Modifier,
    backAction: (()->Unit)? = null,
    addAction: ((TreeStage)->Unit)? = null,
    doneAction: ((TreeStage)->Unit)? = null,
) {
    var name by remember { mutableStateOf(_name) }
    var estimatedDays by remember { mutableStateOf(_estimatedDays) }
    var task by remember { mutableStateOf(_task) }
    var taskType by remember { mutableStateOf(_taskType) }

    var nameError by remember { mutableStateOf(false) }
    val nameScale: Float by animateFloatAsState(
        targetValue = if (nameError) 1.1f else 1f
    )
    val nameColor: Color by animateColorAsState(
        targetValue = if (nameError) Color.Red else Color.Black
    )

    var taskError by remember { mutableStateOf(false) }
    val taskScale: Float by animateFloatAsState(
        targetValue = if (taskError) 1.1f else 1f
    )
    val taskColor: Color by animateColorAsState(
        targetValue = if (taskError) Color.Red else Color.Black
    )

    fun checkFields(): Boolean {
        var res = true

        if (name.isEmpty() || task.isEmpty()) {
            CoroutineScope(Dispatchers.Default).launch {
                if (name.isEmpty()) {
                    nameError = true
                }

                if (task.isEmpty()) {
                    taskError = true
                }

                delay(0.2.seconds)

                nameError = false
                taskError = false
            }

            res = false
        }

        return res
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(state = rememberScrollState()),
    ) {
        CochinText(
            text = title,
            size = 36,
            weight = FontWeight.Bold,
            modifier = Modifier
                .padding(all = 18.dp)
        )
        
        CochinText(
            text = stringResource(id = R.string.tree_stage_title),
            size = 22,
            weight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = if (ConfigurationController.getInstance().isSmallScreen()) 0.dp else 18.dp)
        )

        CochinText(
            text = stringResource(id = R.string.tree_stage_title_info),
            modifier = Modifier
                .padding(top = 12.dp, start = 18.dp, end = 18.dp)
        )

        Column(
            modifier = Modifier
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                border = BorderStroke(width = Theme.Sizes.lineWidth.dpSizeValue.width, color = Color.Black),
                shape = RoundedCornerShape(size = 4.dp),
                modifier = Modifier
                    .scale(nameScale),
                onClick = {
                    MainActivityReceiver.getInstance().pushData(
                        data = TextFieldInfo(
                            text = name,
                            placeholder = R.string.enter_title
                        ) { result ->
                            name = result
                        }
                    )
                }
            ) {
                Crossfade(targetState = name, label = "stage_name") {
                    CochinText(
                        text = if (it.isEmpty()) stringResource(id = R.string.enter_title) else it,
                        color = nameColor,
                        modifier = Modifier
                            .padding(6.dp)
                    )
                }
            }

            EstimatePicker(
                estimatedDays = estimatedDays,
                modifier = Modifier
                    .padding(top = 18.dp)
            ) { newValue ->
                estimatedDays = newValue
            }
        }

        Divider(
            modifier = Modifier
                .padding(bottom = 18.dp, start = 46.dp, end = 46.dp)
        )

        CochinText(
            text = stringResource(id = R.string.tree_task),
            size = 22,
            weight = FontWeight.Bold
        )

        CochinText(
            text = stringResource(id = R.string.treeTask_info),
            modifier = Modifier
                .padding(top = 12.dp, start = 18.dp, end = 18.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                border = BorderStroke(width = Theme.Sizes.lineWidth.dpSizeValue.width, color = Color.Black),
                shape = RoundedCornerShape(size = 4.dp),
                modifier = Modifier
                    .scale(taskScale),
                onClick = {
                    MainActivityReceiver.getInstance().pushData(
                        data = TextFieldInfo(
                            text = task,
                            placeholder = R.string.enter_task
                        ) { result ->
                            task = result
                        }
                    )
                }
            ) {
                Crossfade(targetState = task, label = "stage_task") {
                    CochinText(
                        text = if (it.isEmpty()) stringResource(id = R.string.enter_task) else it,
                        color = taskColor,
                        modifier = Modifier
                            .padding(6.dp)
                    )
                }
            }
        }

        Divider(
            modifier = Modifier
                .padding(top = if (ConfigurationController.getInstance().isSmallScreen()) 0.dp else 18.dp, start = 46.dp, end = 46.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (backAction != null) {
                CombinedButton(
                    image = painterResource(id = R.drawable.arrowshape_backward),
                    string = stringResource(id = R.string.tree_stage_previous),
                ) {
                    backAction()
                }

                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(top = 18.dp, bottom = 18.dp)
                )
            }

            if (addAction != null) {
                CombinedButton(
                    image = painterResource(id = R.drawable.plus),
                    string = stringResource(id = R.string.tree_stage_add),
                ) {
                    if (checkFields()) {
                        addAction(
                            TreeStage(
                            id = UUID.randomUUID(),
                            treeId = treeId,
                            index = stageIndex,
                            name = name,
                            estimatedDays = estimatedDays,
                            task = task,
                            taskType = taskType.id
                            )
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(top = 18.dp, bottom = 18.dp)
                )
            }

            if (doneAction != null) {
                CombinedButton(
                    image = painterResource(id = R.drawable.checkmark),
                    string = stringResource(id = R.string.tree_stage_plant),
                ) {
                    if (checkFields()) {
                        doneAction(
                            TreeStage(
                                id = UUID.randomUUID(),
                                treeId = treeId,
                                index = stageIndex,
                                name = name,
                                estimatedDays = estimatedDays,
                                task = task,
                                taskType = taskType.id
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview36() {
    AwakeTheme {
        TreeStageCreationView(
            treeId = UUID.randomUUID(),
            stageIndex = 1,
            title = "Stage one:",
        )
    }
}