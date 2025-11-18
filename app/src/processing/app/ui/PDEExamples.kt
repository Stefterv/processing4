package processing.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import processing.app.Base
import processing.app.Mode
import processing.app.Platform
import processing.app.api.Sketch
import processing.app.ui.PDEExamples.Companion.examples
import processing.app.ui.theme.LocalLocale
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
            /**
             * Swapping primary and tertiary colors for the preferences window, probably should do that program-wide
             */
            val originalScheme = MaterialTheme.colorScheme
            MaterialTheme(
                colorScheme = originalScheme.copy(
                    primary = originalScheme.tertiary,
                    onPrimary = originalScheme.onTertiary,
                    primaryContainer = originalScheme.tertiaryContainer,
                    onPrimaryContainer = originalScheme.onTertiaryContainer,

                    tertiary = originalScheme.primary,
                    onTertiary = originalScheme.onPrimary,
                    tertiaryContainer = originalScheme.primaryContainer,
                    onTertiaryContainer = originalScheme.onPrimaryContainer,
                )
            ) {
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

                val querriedSketches by derivedStateOf {
                    if (searchQuery.isBlank()) {
                        sketches
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

                        sketches.mapNotNull { folder ->
                            filterFolder(folder)
                        }
                    }
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
                    querriedSketches.map { folder ->
                        sortFolder(folder)
                    }
                }


                /**
                 * Flatting the structure into a flat list as scrolling to an item can only be done with an index
                 */
                data class SketchItem(
                    val type: String,
                    val item: Any
                )

                val flatSketches by derivedStateOf {
                    alphabeticalSketches.flatMap { group ->
                        val children = group.children.flatMap { category ->
                            fun pairs(category: Sketch.Companion.Folder): List<SketchItem> = listOf(
                                SketchItem(
                                    "category",
                                    category
                                )
                            ) + category.sketches.map { sketch ->
                                SketchItem(
                                    "sketch",
                                    sketch
                                )
                            } + category.children.flatMap { pairs(it) }

                            pairs(category)
                        }
                        val itself = SketchItem(
                            "group",
                            group
                        )
                        listOf(itself) + group.sketches.map { sketch ->
                            SketchItem(
                                "sketch",
                                sketch
                            )
                        } + children
                    }
                }

                val finalSketches = flatSketches

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
                        val sidebarState = rememberLazyGridState()
                        val previewState = rememberLazyGridState()

                        val openGroups = remember { mutableStateSetOf<Sketch.Companion.Folder>() }
//                        LaunchedEffect(alphabeticalSketches) {
//                            if (openGroups.isEmpty()) {
//                                alphabeticalSketches.firstOrNull()?.let {
//                                    openGroups.add(it)
//                                }
//                            }
//                        }
                        val scope = rememberCoroutineScope()
                        val current by derivedStateOf {
                            val visibleItems = previewState.layoutInfo.visibleItemsInfo
                            // Grab the first visible category or sketch to determine the current location
                            visibleItems.firstNotNullOfOrNull {
                                val item = finalSketches.getOrNull(it.index) ?: return@firstNotNullOfOrNull null
                                if (item.type != "category") return@firstNotNullOfOrNull null
                                val category = item.item as Sketch.Companion.Folder
                                category.path
                            } ?: visibleItems.slice(visibleItems.size / 2..<visibleItems.size).firstNotNullOfOrNull {
                                val item = finalSketches.getOrNull(it.index) ?: return@firstNotNullOfOrNull null
                                if (item.type != "sketch") return@firstNotNullOfOrNull null

                                val sketch = item.item as Sketch.Companion.Sketch
                                sketch.path
                            }
                        }
                        LazyVerticalGrid(
                            state = sidebarState,
                            modifier = Modifier
                                .width(280.dp)
                                .padding(24.dp),
                            columns = GridCells.Fixed(1),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            alphabeticalSketches.map { group ->
                                val isOpen = current?.startsWith(group.path) == true
                                stickyHeader {
                                    TextButton(
                                        onClick = {
                                            scope.launch {
                                                val index = finalSketches.indexOfFirst {
                                                    it.type == "group" && (it.item as Sketch.Companion.Folder).path == group.path
                                                }
                                                if (index >= 0) {
                                                    previewState.animateScrollToItem(index)
                                                }
                                            }
                                        },
                                        colors = if (current?.startsWith(group.path) == true) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(start = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = group.name,
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }
                                if (isOpen) {
                                    group.children.map { category ->
                                        item {
                                            TextButton(
                                                onClick = {
                                                    scope.launch {
                                                        val index = finalSketches.indexOfFirst {
                                                            it.type == "category" && (it.item as Sketch.Companion.Folder).path == category.path
                                                        }
                                                        if (index >= 0) {
                                                            previewState.animateScrollToItem(index)
                                                        }
                                                    }
                                                },
                                                colors = if (current?.startsWith(category.path) == true) ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.surface,
                                                    contentColor = MaterialTheme.colorScheme.onSurface

                                                ) else ButtonDefaults.outlinedButtonColors(),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp),
                                                modifier = Modifier
                                                    .height(36.dp)
                                            ) {
                                                Text(
                                                    text = category.name,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    style = MaterialTheme.typography.labelMedium,
                                                )
                                            }
                                            }
                                        }
                                    }
                                }
                            }
                        /**
                         * Right content column with examples
                         */
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 24.dp),
                                columns = GridCells.Adaptive(minSize = 240.dp),
                                contentPadding = PaddingValues(vertical = 24.dp),
                                state = previewState,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                finalSketches.map { item ->
                                    when (item.type) {
                                        "group" -> {
                                            val group = item.item as Sketch.Companion.Folder
                                            item(
                                                span = { GridItemSpan(maxLineSpan) }
                                            ) {
                                                Text(
                                                    text = group.name,
                                                    style = MaterialTheme.typography.headlineSmall,
                                                )
                                            }
                                        }

                                        "category" -> {
                                            val category = item.item as Sketch.Companion.Folder
                                            item(
                                                span = { GridItemSpan(maxLineSpan) }
                                            ) {
                                                Text(
                                                    text = category.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                            }
                                        }

                                        "sketch" -> {
                                            val sketch = item.item as Sketch.Companion.Sketch
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .animateItem(),
                                                ) {
                                                    sketch.exampleCard(onOpen = {})
                                                }
                                            }
                                        }

                                        else -> {
                                            item {
                                                Box {

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            VerticalScrollbar(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .fillMaxHeight(),
                                adapter = rememberScrollbarAdapter(
                                    scrollState = previewState,
                                )
                            )
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
            fullWindowContent = true,
            size = Dimension(1100, 700),
            minSize = Dimension(700, 500),
        ) {
            examples(mode)
        }
    }
}


@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun Sketch.Companion.Sketch.exampleCard(onOpen: () -> Unit = {}) {
    val locale = LocalLocale.current
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onOpen)
            .padding(12.dp)
    ) {
        Box(
            Modifier
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shape = MaterialTheme.shapes.medium
                )
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium)
                .fillMaxSize()
                .aspectRatio(16 / 9f)
        ) {
            val image = remember {
                File(path, "${name}.png").takeIf { it.exists() }
            }
            if (image == null) {
                Icon(
                    painter = painterResource("logo.svg"),
                    modifier = Modifier
                        .size(75.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    contentDescription = "Processing Logo"
                )
                HorizontalDivider()
            } else {
                val imageBitmap: ImageBitmap = remember(image) {
                    image.inputStream().readAllBytes().decodeToImageBitmap()
                }
                Image(
                    painter = BitmapPainter(imageBitmap),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentDescription = name
                )
            }

        }
        Text(name)
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
            size = DpSize(1100.dp, 700.dp),
            fullWindowContent = true
        ) {
            PDETheme(darkTheme = false) {
                examples(javaMode)
            }
        }
    }
}