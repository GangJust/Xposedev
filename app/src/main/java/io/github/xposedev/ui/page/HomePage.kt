package io.github.xposedev.ui.page

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.xpler.HookState
import io.github.xposedev.LocalShareResolver
import io.github.xposedev.R
import io.github.xposedev.db.entity.AppConfig
import kotlinx.coroutines.launch

@Composable
fun HomePage() {
    LazyColumn {
        item {
            StatusView(
                isEnabled = HookState.isEnabled,
            )
        }
        item {
            PortView()
        }
    }
}

@Composable
private fun StatusView(
    isEnabled: Boolean,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.error
            },
            contentColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(
            // horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            if (isEnabled) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "生效中",
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cancel),
                    contentDescription = "未生效",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            Text(
                text = if (isEnabled) {
                    "生效中"
                } else {
                    "未生效"
                },
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun PortView() {
    val context = LocalContext.current
    val shareResolver = LocalShareResolver.current
    val scope = rememberCoroutineScope()

    var portSettingDialog by remember { mutableStateOf(false) }
    var config by remember { mutableStateOf<AppConfig?>(null) }
    var portInput by remember { mutableStateOf("") }

    LaunchedEffect("init") {
        config = shareResolver?.queryConfig()?.firstOrNull()
        portInput = config?.port ?: "2345"
    }

    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "监听端口",
                modifier = Modifier.weight(1f),
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                onClick = {
                    portSettingDialog = true
                },
            ) {
                Text(
                    text = config?.port ?: "2345",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    if (portSettingDialog) {
        Dialog(
            onDismissRequest = {
                portSettingDialog = false
            },
        ) {
            Card(
                modifier = Modifier,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "端口设置",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TextField(
                        value = portInput,
                        onValueChange = {
                            portInput = it
                        },
                        placeholder = {
                            Text(text = "请输入端口号 0~65535")
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    ) {
                        Button(
                            onClick = {
                                if (portInput.isEmpty()) {
                                    Toast.makeText(context, "请输入端口号", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                scope.launch {
                                    val copy = config?.copy(port = portInput)
                                    val count = copy?.let { shareResolver?.updateConfigByEntity(it) } ?: 0

                                    if (count > 0) {
                                        config = copy
                                        portSettingDialog = false
                                        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show()
                                    }

                                    portInput = config?.port ?: "2345"
                                }
                            },
                        ) {
                            Text(text = "保存")
                        }
                    }
                }
            }
        }
    }
}