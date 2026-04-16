package uk.ac.tees.mad.dealzone.ui.screens

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import uk.ac.tees.mad.dealzone.Model.Product
import uk.ac.tees.mad.dealzone.ui.theme.DealZoneTheme
import uk.ac.tees.mad.dealzone.viewmodel.AppViewModel
import uk.ac.tees.mad.dealzone.viewmodel.ProductsUiState

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToSaved: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
 
    HomeScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.getProducts() },
        onNavigateToSaved = onNavigateToSaved,
        onNavigateToSettings = onNavigateToSettings,
        onSaveCoupon = { viewModel.saveCoupon(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: ProductsUiState,
    onRefresh: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSaveCoupon: (Product) -> Unit
) {
    val context = LocalContext.current
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("DealZone Deals", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToSaved) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Saved Coupons")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            } else if (uiState.products.isEmpty()) {
                Text(
                    text = "No deals available at the moment.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(uiState.products) { product ->
                        DealCard(
                            product = product,
                            onSave = { onSaveCoupon(product) },
                            onShare = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this deal: ${product.title}\n${product.description}\nPrice: $${product.price}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                            onClick = { selectedProduct = product }
                        )
                    }
                }
            }
        }

        selectedProduct?.let { product ->
            AlertDialog(
                onDismissRequest = { selectedProduct = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                title = { 
                    Text(
                        text = product.title, 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                text = {
                    Column {
                        AsyncImage(
                            model = product.image,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Category: ${product.category}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "⭐ ${product.rating}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Price: $${product.price}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            onSaveCoupon(product)
                            selectedProduct = null 
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Coupon")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedProduct = null }) {
                        Text("Close", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealCard(
    product: Product,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${product.discount}% OFF",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(

                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shadowElevation = 2.dp
                    ) {
                        IconButton(onClick = onShare, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Share, "Share", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shadowElevation = 2.dp
                    ) {
                        IconButton(onClick = onSave, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.FavoriteBorder, "Save", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(
                        onClick = onClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("View Details", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// Previews

val mockProducts = listOf(
    Product(
        id = 1,
        title = "iPhone 15 Pro Max",
        description = "Natural Titanium, 256GB. The most powerful iPhone ever with A17 Pro chip.",
        price = 1199.0,
        discount = 12.5,
        image = "https://cdn.dummyjson.com/product-images/1/thumbnail.jpg",
        category = "smartphones",
        rating = 4.8
    ),
    Product(
        id = 2,
        title = "MacBook Pro 14",
        description = "Apple M3 Chip, 16GB RAM, 512GB SSD. The ultimate pro laptop.",
        price = 1999.0,
        discount = 10.0,
        image = "https://cdn.dummyjson.com/product-images/6/thumbnail.jpg",
        category = "laptops",
        rating = 4.9
    )
)

@Preview(showBackground = true, showSystemUi = true, name = "Home - Populated (Light)", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, showSystemUi = true, name = "Home - Populated (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    DealZoneTheme {
        HomeScreenContent(
            uiState = ProductsUiState(products = mockProducts),
            onRefresh = {},
            onNavigateToSaved = {},
            onNavigateToSettings = {},
            onSaveCoupon = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home - Empty (Light)", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, showSystemUi = true, name = "Home - Empty (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenEmptyPreview() {
    DealZoneTheme {
        HomeScreenContent(
            uiState = ProductsUiState(products = emptyList()),
            onRefresh = {},
            onNavigateToSaved = {},
            onNavigateToSettings = {},
            onSaveCoupon = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home - Loading (Light)", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, showSystemUi = true, name = "Home - Loading (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenLoadingPreview() {
    DealZoneTheme {
        HomeScreenContent(
            uiState = ProductsUiState(isLoading = true),
            onRefresh = {},
            onNavigateToSaved = {},
            onNavigateToSettings = {},
            onSaveCoupon = {}
        )
    }
}
