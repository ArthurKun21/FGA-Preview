package io.github.fate_grand_automata.ui.launcher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.fate_grand_automata.R
import io.github.fate_grand_automata.prefs.core.PrefsCore
import io.github.fate_grand_automata.ui.Stepper
import io.github.fate_grand_automata.ui.prefs.remember

@Composable
fun appendLauncher(
    prefsCore: PrefsCore,
    modifier: Modifier = Modifier
): ScriptLauncherResponseBuilder {

    val isAppend1Locked by prefsCore.append.isAppend1Locked.remember()
    val isAppend2Locked by prefsCore.append.isAppend2Locked.remember()
    val isAppend3Locked by prefsCore.append.isAppend3Locked.remember()

    var shouldUnlockAppend1 by remember { mutableStateOf(false) }
    var shouldUnlockAppend2 by remember { mutableStateOf(false) }
    var shouldUnlockAppend3 by remember { mutableStateOf(false) }

    var upgradeAppend1 by remember { mutableIntStateOf(0) }
    var upgradeAppend2 by remember { mutableIntStateOf(0) }
    var upgradeAppend3 by remember { mutableIntStateOf(0) }

    var upgradeAll by remember { mutableIntStateOf(0) }
    var shouldUpgradeAll by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = shouldUpgradeAll, block = {
        if (isAppend1Locked) {
            shouldUnlockAppend1 = shouldUpgradeAll == true
        }
        if (isAppend2Locked) {
            shouldUnlockAppend2 = shouldUpgradeAll == true
        }
        if (isAppend3Locked) {
            shouldUnlockAppend3 = shouldUpgradeAll == true
        }
    })
    LaunchedEffect(key1 = upgradeAll, block ={
        if (!isAppend1Locked || shouldUnlockAppend1) {
            upgradeAppend1 = upgradeAll
        }
        if (!isAppend2Locked || shouldUnlockAppend2) {
            upgradeAppend2 = upgradeAll
        }
        if (!isAppend3Locked || shouldUnlockAppend3) {
            upgradeAppend3 = upgradeAll
        }
    })

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 5.dp)
    ) {
        Text(
            text = stringResource(id = R.string.append),
            style = MaterialTheme.typography.headlineSmall
        )
        Divider()

        Text(
            text = stringResource(R.string.note),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.append_note),
            style = MaterialTheme.typography.bodyMedium
        )

        Divider()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { shouldUpgradeAll = !shouldUpgradeAll }
        ) {
            Text(
                stringResource(R.string.append_upgrade_all_question),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Switch(
                checked = shouldUpgradeAll,
                onCheckedChange = { shouldUpgradeAll = it }
            )
        }

        Box(
            modifier = Modifier.align(Alignment.End)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = { upgradeAll = 0 },
                    enabled = shouldUpgradeAll,
                    modifier = Modifier.alignByBaseline()
                ) {
                    Text(text = stringResource(id = R.string.reset).uppercase())
                }
                Stepper(
                    modifier = Modifier.alignByBaseline(),
                    value = upgradeAll,
                    onValueChange = { upgradeAll = it },
                    valueRange = 0..9,
                    enabled = shouldUpgradeAll,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AppendItem(
                modifier = Modifier.weight(1f),
                name = stringResource(id = R.string.append_1),
                isLocked = isAppend1Locked,
                shouldUnlock = shouldUnlockAppend1,
                onShouldUnlockChange = { shouldUnlockAppend1 = it },
                upgradeLevel = upgradeAppend1,
                onUpgradeLevelChange = { upgradeAppend1 = it }
            )
            AppendItem(
                modifier = Modifier.weight(1f),
                name = stringResource(id = R.string.append_2),
                isLocked = isAppend2Locked,
                shouldUnlock = shouldUnlockAppend2,
                onShouldUnlockChange = { shouldUnlockAppend2 = it },
                upgradeLevel = upgradeAppend2,
                onUpgradeLevelChange = { upgradeAppend2 = it }
            )
            AppendItem(
                modifier = Modifier.weight(1f),
                name = stringResource(id = R.string.append_3),
                isLocked = isAppend3Locked,
                shouldUnlock = shouldUnlockAppend3,
                onShouldUnlockChange = { shouldUnlockAppend3 = it },
                upgradeLevel = upgradeAppend3,
                onUpgradeLevelChange = { upgradeAppend3 = it }
            )
        }
    }


    return ScriptLauncherResponseBuilder(
        canBuild = { true },
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
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                enabled = isLocked,
                onClick = {
                    onShouldUnlockChange(!shouldUnlock)
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = name.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
            )
        }
        if (isLocked) {
            item {
                Text(
                    text = stringResource(id = R.string.should_unlock_append),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item {
                Checkbox(
                    checked = shouldUnlock,
                    onCheckedChange = onShouldUnlockChange
                )
            }
        }
        item {
            Stepper(
                value = (upgradeLevel),
                onValueChange = { onUpgradeLevelChange(it) },
                valueRange = 0..9,
                enabled = !isLocked || shouldUnlock
            )
        }
        item {
            TextButton(
                onClick = { onUpgradeLevelChange(0)  },
                enabled = (!isLocked || shouldUnlock) && upgradeLevel != 0,
            ) {
                Text(text = stringResource(id = R.string.reset).uppercase())
            }
        }
    }
}