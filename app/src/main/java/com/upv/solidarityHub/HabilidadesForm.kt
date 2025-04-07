package com.upv.solidarityHub

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upv.solidarityHub.persistence.factory.habilidad.HabilidadFactoryProvider
import com.upv.solidarityHub.persistence.model.Habilidad

@Composable
fun HabilidadesForm() {

    var selectedSkill by remember { mutableStateOf("") }
    var competencia by remember { mutableStateOf(0f) }
    var preferencia by remember { mutableStateOf(0f) }
    var skillList by remember { mutableStateOf(listOf<Habilidad>()) }

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Añadir habilidades",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                SkillDropdown(
                    selectedSkill = selectedSkill,
                    onSkillSelected = { newSkill ->
                        selectedSkill = newSkill
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text("Competencia: ${competencia.toInt()}")
                    Slider(
                        value = competencia,
                        onValueChange = { competencia = it },
                        valueRange = 0f..5f,
                        steps = 4
                    )
                }

                Column {
                    Text("Preferencia: ${preferencia.toInt()}")
                    Slider(
                        value = preferencia,
                        onValueChange = { preferencia = it },
                        valueRange = 0f..5f,
                        steps = 4
                    )
                }

                Button(
                    onClick = {
                        if (selectedSkill.isNotEmpty()) {
                            skillList = removeSkillIfExists(selectedSkill, skillList);
                            val factory = HabilidadFactoryProvider.getFactory(selectedSkill)
                            val habilidad = factory.createHabilidad(competencia.toInt(), preferencia.toInt())
                            skillList = skillList + habilidad
                        }
                    },
                    enabled = selectedSkill.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Añadir")
                }

                if (skillList.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray)
                            .padding(8.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Habilidad", fontWeight = FontWeight.Bold)
                            Text("Competencia", fontWeight = FontWeight.Bold)
                            Text("Preferencia", fontWeight = FontWeight.Bold)
                        }
                        Divider()
                        skillList.forEach { habilidad ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(habilidad.nombre)
                                Text(habilidad.competencia.toString())
                                Text(habilidad.preferencia.toString())
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = {
                        selectedSkill = ""
                        competencia = 0f
                        preferencia = 0f
                        skillList = emptyList()
                    }) {
                        Text("Cancelar")
                    }

                    Button(onClick = {
                        Toast.makeText(context, "Habilidades guardadas", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Finalizar")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDropdown(
    selectedSkill: String,
    onSkillSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val habilidades = listOf("Medicina", "Veterinaria", "Conducción")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedSkill,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = { Text("Selecciona una habilidad") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            habilidades.forEach { skill ->
                DropdownMenuItem(
                    text = { Text(skill, modifier = Modifier.fillMaxWidth()) },
                    onClick = {
                        onSkillSelected(skill)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun removeSkillIfExists(skillToRemove: String, skillList : List<Habilidad>): List<Habilidad> {
    return skillList.filterNot { it.nombre == skillToRemove }
}
