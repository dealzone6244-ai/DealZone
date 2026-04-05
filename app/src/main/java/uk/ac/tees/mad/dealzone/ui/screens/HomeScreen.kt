package uk.ac.tees.mad.dealzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                confirmButton = {
                    Button(onClick = { 
                        onSaveCoupon(product)
                        selectedProduct = null 
                    }) {
                        Text("Save Coupon")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedProduct = null }) {
                        Text("Close")
                    }
                },
                title = { Text(product.title) },
                text = {
                    Column {
                        AsyncImage(
                            model = product.image,
                            contentDescription = product.title,
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Category: ${product.category}")
                        Text("Rating: ${product.rating} ⭐")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(product.description)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Price: $${product.price}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${product.discount}% OFF",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, "Share", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        IconButton(onClick = onSave) {
                            Icon(Icons.Default.FavoriteBorder, "Save", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    TextButton(onClick = onClick) {
                        Text("View Details")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
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
    
    DealZoneTheme(darkTheme = false) {
        HomeScreenContent(
            uiState = ProductsUiState(products = mockProducts),
            onRefresh = {},
            onNavigateToSaved = {},
            onNavigateToSettings = {},
            onSaveCoupon = {}
        )
    }
}
