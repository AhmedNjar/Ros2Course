package com.openamr.ros2course.features.course.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.openamr.ros2course.core.theme.AmberPrimary
import com.openamr.ros2course.core.theme.DeepBlack
import com.openamr.ros2course.core.theme.SurfaceVariantDark
import com.openamr.ros2course.core.theme.TextGrey
import com.openamr.ros2course.features.course.domain.entity.CourseModule
import com.openamr.ros2course.features.course.domain.entity.ModuleContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.text.TextLinkStyles

data class ModuleDetailScreen(private val moduleId: String) : Screen {

    override val key: String = "module_detail_$moduleId"

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ModuleDetailScreenModel> { parametersOf(moduleId) }
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        val module = uiState.modules.find { it.id == moduleId }
        val content = uiState.contentCache[moduleId]

        if (module == null || content == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        Scaffold(
            containerColor = DeepBlack,
            topBar = {
                CustomTopBar(
                    currentChapter = module.order,
                    totalChapters = uiState.modules.size,
                    onBack = { navigator.pop() }
                )
            }
        ) { padding ->
            ModulePage(module, content, Modifier.padding(padding))
        }
    }
}

@Composable
private fun CustomTopBar(currentChapter: Int, totalChapters: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "Ch. $currentChapter of $totalChapters",
            color = TextGrey,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ModulePage(module: CourseModule, content: ModuleContent, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        // ModuleHeader is now FIXED at the top of every page
        ModuleHeader(module)

        if (module.hasTabs && content is ModuleContent.Structured) {
            StructuredContent(content)
        } else if (content is ModuleContent.SingleSection) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                ThemedMarkdown(
                    content = content.content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ModuleHeader(module: CourseModule) {
    Column(modifier = Modifier.padding(16.dp)) {
        Surface(
            color = Color(0xFF3E2723),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "Chapter ${module.order}",
                color = AmberPrimary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = module.title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                lineHeight = 32.sp
            ),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Understand what \"great software\" really means",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = TextGrey
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StructuredContent(content: ModuleContent.Structured) {
    val tabs = listOf("Overview", "Content", "Key points")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = AmberPrimary,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                    color = AmberPrimary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = label,
                            color = if (pagerState.currentPage == index) AmberPrimary else TextGrey,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            val sectionContent = when (pageIndex) {
                0 -> content.overview
                1 -> content.content
                else -> content.activity
            }
            
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                ThemedMarkdown(
                    content = sectionContent,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ThemedMarkdown(content: String, modifier: Modifier = Modifier) {
    Markdown(
        content = content,
        modifier = modifier,
        colors = markdownColor(
            text = Color.White,
            codeBackground = AmberPrimary.copy(alpha = 0.1f),
            inlineCodeBackground = AmberPrimary.copy(alpha = 0.1f),
            dividerColor = AmberPrimary.copy(alpha = 0.2f),
            tableBackground = SurfaceVariantDark
        ),
        typography = markdownTypography(
            h1 = MaterialTheme.typography.headlineSmall.copy(color = AmberPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp),
            h2 = MaterialTheme.typography.titleLarge.copy(color = AmberPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp),
            h3 = MaterialTheme.typography.titleMedium.copy(color = AmberPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp),
            textLink = TextLinkStyles(style = SpanStyle(color = AmberPrimary, textDecoration = TextDecoration.Underline)),
            code = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 14.sp),
            inlineCode = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 14.sp),
            quote = MaterialTheme.typography.bodyMedium.copy(color = TextGrey, fontStyle = FontStyle.Italic),
            text = TextStyle(color = Color.White, fontSize = 15.sp, lineHeight = 21.sp)
        ),
        components = markdownComponents(
            codeFence = { model -> CopyableCodeBlock(model.content) }
        )
    )
}

@Composable
private fun CopyableCodeBlock(content: String) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AmberPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = content,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            modifier = Modifier.padding(end = 40.dp, top = 4.dp, bottom = 4.dp)
        )

        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(content))
                copied = true
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
        ) {
            Icon(
                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = if (copied) AmberPrimary else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
