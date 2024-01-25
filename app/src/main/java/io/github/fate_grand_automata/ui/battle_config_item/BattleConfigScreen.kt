package io.github.fate_grand_automata.ui.battle_config_item

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.fate_grand_automata.R
import io.github.fate_grand_automata.prefs.core.BattleConfigCore
import io.github.fate_grand_automata.scripts.models.CardPriorityPerWave
import io.github.fate_grand_automata.scripts.models.CardScore
import io.github.fate_grand_automata.ui.HeadingButton
import io.github.fate_grand_automata.ui.VerticalDivider
import io.github.fate_grand_automata.ui.card_priority.getColorRes
import io.github.fate_grand_automata.ui.dialog.FgaDialog
import io.github.fate_grand_automata.ui.icon
import io.github.fate_grand_automata.ui.pref_support.SupportViewModel
import io.github.fate_grand_automata.ui.prefs.EditTextPreference
import io.github.fate_grand_automata.ui.prefs.Preference
import io.github.fate_grand_automata.util.toSp

@Composable
fun BattleConfigScreen(
    vm: BattleConfigScreenViewModel = viewModel(),
    supportVm: SupportViewModel = viewModel(),
    windowSizeClass: WindowSizeClass,
    navigate: (BattleConfigDestination) -> Unit
) {
    val context = LocalContext.current

    val battleConfigExport = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        vm.export(context, uri)
    }

    BattleConfigContent(
        config = vm.battleConfigCore,
        friendEntries = supportVm.friends,
        windowSizeClass = windowSizeClass,
        onExport = { battleConfigExport.launch("${vm.battleConfig.name}.fga") },
        onCopy = {
            val id = vm.createCopyAndReturnId(context)
            navigate(BattleConfigDestination.Other(id))
        },
        onDelete = {
            vm.delete()
            navigate(BattleConfigDestination.Back)
        },
        navigate = navigate
    )
}

sealed class BattleConfigDestination {
    data object SkillMaker : BattleConfigDestination()
    data object CardPriority : BattleConfigDestination()
    data object Spam : BattleConfigDestination()
    data object PreferredSupport : BattleConfigDestination()
    data object Back : BattleConfigDestination()

    data object Material : BattleConfigDestination()
    class Other(val id: String) : BattleConfigDestination()
}

@Composable
private fun BattleConfigContent(
    config: BattleConfigCore,
    windowSizeClass: WindowSizeClass,
    friendEntries: Map<String, String>,
    onExport: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    navigate: (BattleConfigDestination) -> Unit,
    vm: BattleConfigScreenViewModel = viewModel()
) {
    val isMobileVertical by remember {
        mutableStateOf(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.battle_config_edit).uppercase(),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    if (!isMobileVertical) {
                        BattleConfigActionButtons(
                            onDelete = onDelete,
                            onExport = onExport,
                            onCopy = onCopy
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navigate(BattleConfigDestination.Back) }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isMobileVertical) {
                BattleConfigActionButtons(
                    onDelete = onDelete,
                    onExport = onExport,
                    onCopy = onCopy
                )
            }
            Divider()

            LazyVerticalStaggeredGrid(
                modifier = Modifier.weight(1f),
                columns = when (isMobileVertical) {
                    true -> StaggeredGridCells.Fixed(1)
                    false -> StaggeredGridCells.Fixed(2)
                },
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column {
                            config.name.EditTextPreference(
                                title = stringResource(R.string.p_battle_config_name),
                                validate = { it.isNotBlank() },
                                singleLine = true
                            )

                            Divider()

                            config.notes.EditTextPreference(
                                title = stringResource(R.string.p_battle_config_notes)
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        SkillCommandGroup(
                            config = config,
                            vm = vm,
                            openSkillMaker = { navigate(BattleConfigDestination.SkillMaker) }
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                            ) {
                                MaterialDisplay(
                                    modifier = Modifier.weight(1f),
                                    config = config,
                                    onNavigate = { navigate(BattleConfigDestination.Material) }
                                )
                            }

                            Divider(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(IntrinsicSize.Min)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .clickable(onClick = { navigate(BattleConfigDestination.Spam) }),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        stringResource(R.string.p_spam_spam).uppercase(),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier
                                            .padding(16.dp, 5.dp)
                                    )
                                }
                                VerticalDivider()
                                ServerSelection(
                                    modifier = Modifier.weight(1f),
                                    config = config
                                )
                                VerticalDivider()
                                PartySelection(
                                    modifier = Modifier.weight(1f),
                                    config = config
                                )
                            }

                            Divider(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(IntrinsicSize.Min)
                            ) {
                                RaidDelay(
                                    modifier = Modifier.weight(1f),
                                    config = config
                                )
                                VerticalDivider()
                                StoryIntro(
                                    modifier = Modifier.weight(1f),
                                    config = config
                                )
                            }

                            Divider(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                            )

                            val cardPriority by vm.cardPriority.collectAsState(null)

                            cardPriority?.let {
                                Preference(
                                    title = { Text(stringResource(R.string.p_battle_config_card_priority)) },
                                    summary = { CardPrioritySummary(it) },
                                    onClick = { navigate(BattleConfigDestination.CardPriority) }
                                )
                            }
                        }
                    }
                }

                item {
                    val maxSkillText by vm.maxSkillText.collectAsState("")

                    SupportGroup(
                        config = config.support,
                        goToPreferred = { navigate(BattleConfigDestination.PreferredSupport) },
                        maxSkillText = maxSkillText
                    )
                }

                item {
                    ShuffleCardsGroup(config)
                }
            }
        }
    }
}

@Composable
private fun BattleConfigActionButtons(
    onDelete: () -> Unit,
    onExport: () -> Unit,
    onCopy: () -> Unit
) {
    val deleteConfirmDialog = FgaDialog()
    deleteConfirmDialog.build {
        title(stringResource(R.string.battle_config_item_delete_confirm_title))
        message(stringResource(R.string.battle_config_item_delete_confirm_message))

        buttons(
            onSubmit = onDelete,
            okLabel = stringResource(R.string.battle_config_item_delete_confirm_ok)
        )
    }

    Row(
        modifier = Modifier.padding(
            start = 8.dp,
            end = 4.dp,
            bottom = 8.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeadingButton(
            text = stringResource(R.string.battle_config_item_export),
            onClick = onExport,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        HeadingButton(
            text = stringResource(R.string.battle_config_item_copy),
            icon = icon(Icons.Default.ContentCopy),
            onClick = onCopy,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        HeadingButton(
            text = stringResource(R.string.battle_config_item_delete),
            isDanger = true,
            icon = icon(Icons.Default.Delete),
            onClick = { deleteConfirmDialog.show() },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }

}

private val CardScore.color: Color
    @Composable get() {
        return colorResource(getColorRes())
    }

@Composable
private fun CardPrioritySummary(cardPriority: CardPriorityPerWave) {
    Column(
        modifier = Modifier
            .padding(vertical = 5.dp)
    ) {
        cardPriority.forEachIndexed { wave, priorities ->
            Row(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "W${wave + 1}: ",
                    modifier = Modifier
                        .padding(end = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                Card {
                    val priorityString = buildAnnotatedString {
                        priorities.forEachIndexed { index, it ->
                            if (index != 0) {
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                                    )
                                ) {
                                    append(",")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        letterSpacing = 4.dp.toSp()
                                    )
                                ) {
                                    append(" ")
                                }
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                                    color = it.color,
                                    shadow = Shadow(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        offset = Offset(1f, 1f),
                                        blurRadius = 0f
                                    )
                                )
                            ) {
                                append(it.toString())
                            }
                        }
                    }
                    Text(
                        text = priorityString,
                        modifier = Modifier.padding(horizontal = 5.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}