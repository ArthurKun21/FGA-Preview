package io.github.fate_grand_automata.ui.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.fate_grand_automata.R
import io.github.fate_grand_automata.prefs.core.PrefsCore
import io.github.fate_grand_automata.ui.RangeButtons
import io.github.fate_grand_automata.ui.prefs.remember

@Composable
fun appendLauncher(
    prefsCore: PrefsCore,
    modifier: Modifier = Modifier
): ScriptLauncherResponseBuilder {

    val emptyServant by prefsCore.emptyEnhance.remember()

    val appendOneLocked by prefsCore.append.appendOneLocked.remember()
    val appendTwoLocked by prefsCore.append.appendTwoLocked.remember()
    val appendThreeLocked by prefsCore.append.appendThreeLocked.remember()

    var shouldUnlockAppend1 by remember { mutableStateOf(false) }
    var shouldUnlockAppend2 by remember { mutableStateOf(false) }
    var shouldUnlockAppend3 by remember { mutableStateOf(false) }

    var upgradeAppend1 by remember { mutableIntStateOf(0) }
    var upgradeAppend2 by remember { mutableIntStateOf(0) }
    var upgradeAppend3 by remember { mutableIntStateOf(0) }

    var upgradeAll by remember { mutableIntStateOf(0) }
    var shouldUpgradeAll by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = shouldUpgradeAll, block = {
        if (appendOneLocked) {
            shouldUnlockAppend1 = shouldUpgradeAll == true
        }
        if (appendTwoLocked) {
            shouldUnlockAppend2 = shouldUpgradeAll == true
        }
        if (appendThreeLocked) {
            shouldUnlockAppend3 = shouldUpgradeAll == true
        }
    })
    LaunchedEffect(key1 = upgradeAll, block = {
        if (!appendOneLocked || shouldUnlockAppend1) {
            upgradeAppend1 = upgradeAll
        }
        if (!appendTwoLocked || shouldUnlockAppend2) {
            upgradeAppend2 = upgradeAll
        }
        if (!appendThreeLocked || shouldUnlockAppend3) {
            upgradeAppend3 = upgradeAll
        }
    })

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 5.dp)
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
            ) {
                Text(
                    text = stringResource(id = R.string.append),
                    style = MaterialTheme.typography.headlineSmall
                )
                HorizontalDivider()
            }
        }

        if (emptyServant) {
            item {
                Text(
                    text = stringResource(id = R.string.empty_servant),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.note),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    text = stringResource(R.string.append_note),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { shouldUpgradeAll = !shouldUpgradeAll }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = shouldUpgradeAll,
                            onCheckedChange = {
                                shouldUpgradeAll = it
                            },
                        )
                        Text(
                            text = stringResource(id = R.string.append_upgrade_all),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (shouldUpgradeAll) {
                                true -> MaterialTheme.colorScheme.onBackground
                                false -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            },
                            textDecoration = TextDecoration.Underline
                        )
                        TextButton(
                            onClick = { upgradeAll = 0 },
                        ) {
                            Text(text = stringResource(id = R.string.reset).uppercase())
                        }
                    }
                    RangeButtons(
                        value = upgradeAll,
                        onValueChange = { upgradeAll = it },
                        valueRange = 0..9,
                        enabled = shouldUpgradeAll,
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RectangleShape,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            item {
                AppendItem(
                    name = stringResource(id = R.string.append_number, 1),
                    isLocked = appendOneLocked,
                    shouldUnlock = shouldUnlockAppend1,
                    onShouldUnlockChange = { shouldUnlockAppend1 = it },
                    upgradeLevel = upgradeAppend1,
                    onUpgradeLevelChange = { upgradeAppend1 = it }
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            item {
                AppendItem(
                    name = stringResource(id = R.string.append_number, 2),
                    isLocked = appendTwoLocked,
                    shouldUnlock = shouldUnlockAppend2,
                    onShouldUnlockChange = { shouldUnlockAppend2 = it },
                    upgradeLevel = upgradeAppend2,
                    onUpgradeLevelChange = { upgradeAppend2 = it }
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            item {
                AppendItem(
                    name = stringResource(id = R.string.append_number, 3),
                    isLocked = appendThreeLocked,
                    shouldUnlock = shouldUnlockAppend3,
                    onShouldUnlockChange = { shouldUnlockAppend3 = it },
                    upgradeLevel = upgradeAppend3,
                    onUpgradeLevelChange = { upgradeAppend3 = it }
                )
            }
        }
    }


    return ScriptLauncherResponseBuilder(
        canBuild = {
            !emptyServant
        },
        build = {
            ScriptLauncherResponse.Append(
                shouldUnlockAppend1 = shouldUnlockAppend1,
                shouldUnlockAppend2 = shouldUnlockAppend2,
                shouldUnlockAppend3 = shouldUnlockAppend3,
                upgradeAppend1 = upgradeAppend1,
                upgradeAppend2 = upgradeAppend2,
                upgradeAppend3 = upgradeAppend3
            )
        }
    )
}

@Composable
private fun AppendItem(
    modifier: Modifier = Modifier,
    name: String,
    isLocked: Boolean,
    shouldUnlock: Boolean,
    onShouldUnlockChange: (Boolean) -> Unit,
    upgradeLevel: Int,
    onUpgradeLevelChange: (Int) -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = isLocked,
                onClick = {
                    onShouldUnlockChange(!shouldUnlock)
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = name.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
            )
            if (isLocked) {
                Text(
                    text = stringResource(id = R.string.should_unlock_append),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Checkbox(
                    checked = shouldUnlock,
                    onCheckedChange = onShouldUnlockChange
                )
            }
            TextButton(
                onClick = { onUpgradeLevelChange(0) },
                enabled = (!isLocked || shouldUnlock) && upgradeLevel != 0,
            ) {
                Text(text = stringResource(id = R.string.reset).uppercase())
            }
        }
        RangeButtons(
            value = (upgradeLevel),
            onValueChange = { onUpgradeLevelChange(it) },
            valueRange = 0..9,
            enabled = !isLocked || shouldUnlock,
            shape = RectangleShape,
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
    }
}