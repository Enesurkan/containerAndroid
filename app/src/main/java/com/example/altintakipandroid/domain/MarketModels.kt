package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

data class BannerSlide(
    @SerializedName("id") val id: Int,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("link_url") val linkUrl: String? = null,
    @SerializedName("sort_order") val sortOrder: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class BannerSlidesResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<BannerSlide>? = null
)

data class BannerSlideDetailResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: BannerSlide? = null
)

data class CategoryTree(
    @SerializedName("id") val id: Int,
    @SerializedName("parent_id") val parentId: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("sort_order") val sortOrder: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("children") val children: List<CategoryTree>? = null
)

data class CategoryTreeResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<CategoryTree>? = null
)

data class ProductOut(
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val categoryId: Int? = null,
    @SerializedName("subcategory_id") val subcategoryId: Int? = null,
    @SerializedName("category_path") val categoryPath: String? = null,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("image_urls") val imageUrls: List<String>? = null,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("link_url") val linkUrl: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("sort_order") val sortOrder: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true
) {
    val imageList: List<String>
        get() = if (!imageUrls.isNullOrEmpty()) imageUrls else listOf(imageUrl)
}

data class ProductsResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<ProductOut>? = null
)

data class ProductDetailResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: ProductOut? = null
)

data class GenerateShareUrlRequest(
    @SerializedName("product_id") val productId: Int
)

data class GenerateShareUrlResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: ShareUrlData? = null
)

data class ShareUrlData(
    @SerializedName("url") val url: String
)
