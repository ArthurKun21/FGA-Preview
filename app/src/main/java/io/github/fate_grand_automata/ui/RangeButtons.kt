package io.github.fate_grand_automata.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun RangeButtons(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: Shape = ButtonDefaults.shape,
    valueRepresentation: (Int) -> String = { it.toString() }
) {
    var currentValue by remember(value) { mutableIntStateOf(value) }

    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        for (i in valueRange) {
            Button(
                onClick = {
                    currentValue = i.coerceIn(valueRange)
                    onValueChange(i.coerceIn(valueRange))
                },
                enabled = enabled,
                border = BorderStroke(
                    width = when(currentValue == i){
                        true -> 2.dp
                        false -> 1.dp
                    },
                    color = when(currentValue == i){
                        true -> MaterialTheme.colorScheme.onSurface
                        false -> MaterialTheme.colorScheme.onSecondary
                    },
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when(currentValue == i){
                        true -> MaterialTheme.colorScheme.secondary
                        false -> MaterialTheme.colorScheme.surface
                    },
                    contentColor = when(currentValue == i){
                        true -> MaterialTheme.colorScheme.onSecondary
                        false -> MaterialTheme.colorScheme.onSurface
                    }
                ),
                shape = shape,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(
                    valueRepresentation(i),
                    textAlign = TextAlign.Center,
                    modifier = Modifier,
                    style = textStyle
                )
            }
        }
    }
}