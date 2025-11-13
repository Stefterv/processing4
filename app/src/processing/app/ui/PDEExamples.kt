package processing.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import processing.app.Base
import processing.app.Mode
import processing.app.Platform
import processing.app.api.Sketch
import processing.app.ui.PDEExamples.Companion.examples
import processing.app.ui.theme.PDEComposeWindow
import processing.app.ui.theme.PDESwingWindow
import processing.app.ui.theme.PDETheme
import java.awt.Dimension
import java.io.File
import javax.swing.SwingUtilities


class PDEExamples {
    companion object {
        @Composable
        fun examples(mode: Mode) {
            val sketches = remember { mutableStateListOf<Sketch.Companion.Folder>() }

            var searchQuery by remember { mutableStateOf("") }

            val scope = rememberCoroutineScope()

            LaunchedEffect(mode) {
                val foundSketches =
                    processing.app.api.Mode.findExampleSketches(
                        mode = mode,
                        sketchbookFolder = null,
                        scope = scope
                    )
                sketches.clear()
                sketches.addAll(foundSketches)
            }
            val alphabeticalSketches by derivedStateOf {
                fun sortFolder(folder: Sketch.Companion.Folder): Sketch.Companion.Folder {
                    val sortedSketches = folder.sketches.sortedBy { it.name }
                    val sortedChildren = folder.children.map { child ->
                        sortFolder(child)
                    }.sortedBy { it.name }
                    return Sketch.Companion.Folder(
                        name = folder.name,
                        path = folder.path,
                        sketches = sortedSketches,
                        children = sortedChildren
                    )
                }
                sketches.map { folder ->
                    sortFolder(folder)
                }
            }

            val querriedSketches by derivedStateOf {
                if (searchQuery.isBlank()) {
                    alphabeticalSketches
                } else {
                    fun filterFolder(folder: Sketch.Companion.Folder): Sketch.Companion.Folder? {
                        val filteredSketches = folder.sketches.filter { sketch ->
                            sketch.name.contains(searchQuery, ignoreCase = true) ||
                                    sketch.path.contains(searchQuery, ignoreCase = true)
                        }
                        val filteredChildren = folder.children.mapNotNull { child ->
                            filterFolder(child)
                        }
                        return if (filteredSketches.isNotEmpty() || filteredChildren.isNotEmpty()) {
                            Sketch.Companion.Folder(
                                name = folder.name,
                                path = folder.path,
                                sketches = filteredSketches,
                                children = filteredChildren
                            )
                        } else {
                            null
                        }
                    }

                    alphabeticalSketches.mapNotNull { folder ->
                        filterFolder(folder)
                    }
                }
            }

            Column {
                Header(
                    searchable = SearchState(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    ),
                    headlineKey = "",
                    headline = {
                        Text("${mode.getTitle()} Examples")
                    },
                    descriptionKey = "examples.description"
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null)
                        Text("Random")
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxHeight()
                ) {
                    /**
                     * Left navigation column with categories
                     */
                    val previewState = rememberLazyGridState()

                    val openGroups = remember { mutableStateSetOf<Sketch.Companion.Folder>() }
                    val scope = rememberCoroutineScope()
                    LazyVerticalGrid(
                        modifier = Modifier
                            .width(200.dp),
                        columns = GridCells.Adaptive(minSize = 100.dp),
                    ) {
                        sketches.map { group ->
                            val open = openGroups.contains(group)
                            item(
                                span = { GridItemSpan(maxLineSpan) }
                            ) {
                                TextButton(onClick = {
                                    if (!open) {
                                        openGroups.add(group)
                                    } else {
                                        openGroups.remove(group)
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.ArrowDropDown, contentDescription = null,
                                        modifier = Modifier
                                            .rotate(if (open) 0f else -90f)
                                    )
                                    Text(group.name)
                                }
                            }
                            if (open) {
                                group.children.map { category ->
                                    item {
                                        Button(
                                            onClick = {},
                                            modifier = Modifier.animateItem()
                                        ) {
                                            Text(category.name)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    /**
                     * Right content column with examples
                     */
                    LazyVerticalGrid(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 24.dp),
                        columns = GridCells.Adaptive(minSize = 240.dp),
                        state = previewState,
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        fun showSketches(group: Sketch.Companion.Folder, crumbs: List<String> = emptyList()) {
                            if (!group.sketches.isEmpty()) {
                                item(
                                    key = "header-${group.path}-${crumbs.size}",
                                    span = { GridItemSpan(maxLineSpan) }
                                ) {
                                    Text(
                                        text = group.name,
                                        modifier = Modifier
                                            .animateItem(),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                            items(group.sketches, key = { it.path }) { sketch ->
                                Box(
                                    modifier = Modifier.animateItem()
                                ) {
                                    sketch.card(onOpen = {})
                                }
                            }
                            group.children.map { child ->
                                showSketches(child, crumbs + group.name)
                            }
                        }

                        querriedSketches.map { group ->
                            stickyHeader(
                                key = "header-${group.path}"
                            ) {
                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                            showSketches(group)
                        }
                    }

                }
            }
        }
    }
}


fun show(mode: Mode) {
    SwingUtilities.invokeLater {
        PDESwingWindow(
            unique = mode::class,
            titleKey = "examples",
            size = Dimension(850, 600),
            minSize = Dimension(700, 500),
        ) {
            examples(mode)
        }
    }
}

/**
 * Make sure you run Processing with
 * ```
 * ./gradlew run
 * ```
 * at least once so that the java folder exists
 *
 * or
 *
 * use the Processing run configuration in IDEA
 */
fun main() {
    application {
        // TODO: Migrate to using the actual Java mode from the application
        val folder = File("app/build/resources-bundled/common/modes/java")
        if (!folder.exists()) {
            error("The java mode folder does not exist: ${folder.absolutePath}\nMake sure to run Processing at least once using './gradlew run' or the Processing run configuration in IDEA")
        }
        val javaMode = object : Mode(folder) {
            override fun getIdentifier() = "java"
            override fun getTitle() = "Java"
            override fun createEditor(base: Base?, path: String?, state: EditorState?) = TODO("Not yet implemented")
            override fun getDefaultExtension() = "pde"
            override fun getExtensions() = arrayOf("pde", "java")
            override fun getIgnorable() = Platform.getSupportedVariants().keyArray()
        }
        PDEComposeWindow(
            titleKey = "pde.examples.title",
            size = DpSize(1000.dp, 750.dp),
            fullWindowContent = true
        ) {
            PDETheme(darkTheme = false) {
                examples(javaMode)
            }
        }
    }
}