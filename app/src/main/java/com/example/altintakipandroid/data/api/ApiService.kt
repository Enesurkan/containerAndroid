package com.example.altintakipandroid.data.api

import com.example.altintakipandroid.domain.ActivationResponse
import com.example.altintakipandroid.domain.AppConstants
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.AppInformationResponse
import com.example.altintakipandroid.domain.ExchangeRatesResponse
import com.example.altintakipandroid.domain.BannerSlide
import com.example.altintakipandroid.domain.BannerSlideDetailResponse
import com.example.altintakipandroid.domain.BannerSlidesResponse
import com.example.altintakipandroid.domain.CategoryTree
import com.example.altintakipandroid.domain.CategoryTreeResponse
import com.example.altintakipandroid.domain.ProductOut
import com.example.altintakipandroid.domain.PortalLoginRequest
import com.example.altintakipandroid.domain.PortalLoginResponse
import com.example.altintakipandroid.domain.ProductDetailResponse
import com.example.altintakipandroid.domain.GoldMultiplier
import com.example.altintakipandroid.domain.GoldMultipliersResponse
import com.example.altintakipandroid.domain.GenerateShareUrlRequest
import com.example.altintakipandroid.domain.GenerateShareUrlResponse
import com.example.altintakipandroid.domain.SaveGoldMultiplierItem
import com.example.altintakipandroid.domain.ProductsResponse
import com.example.altintakipandroid.domain.PushRegisterRequest
import com.example.altintakipandroid.domain.PushRegisterResponse
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.domain.UIConfigResponse
import com.example.altintakipandroid.domain.WsTokenResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API endpoints for ui-config, app-information, and activation (Faz 0 scope).
 */
interface ApiService {

    @GET(AppConstants.Api.UI_CONFIG)
    suspend fun fetchUIConfig(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<UIConfigResponse>

    @GET(AppConstants.Api.APP_INFORMATION)
    suspend fun fetchAppInformation(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<AppInformationResponse>

    /**
     * Activate with client name (GET /activate?clientName=xxx).
     * Returns apiKey in response data.
     */
    @GET(AppConstants.Api.ACTIVATE)
    suspend fun activate(
        @Query("clientName") clientName: String
    ): Response<ActivationResponse>

    @GET(AppConstants.Api.GOLD_CURRENCY)
    suspend fun fetchExchangeRates(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<ExchangeRatesResponse>

    @GET(AppConstants.Api.MARKET_BANNER_SLIDES)
    suspend fun fetchBannerSlides(
        @Header("X-Api-Key") apiKey: String
    ): Response<BannerSlidesResponse>

    @GET("${AppConstants.Api.MARKET_BANNER_SLIDES}/{slideId}")
    suspend fun fetchBannerSlide(
        @Header("X-Api-Key") apiKey: String,
        @Path("slideId") slideId: Int
    ): Response<BannerSlideDetailResponse>

    @GET(AppConstants.Api.MARKET_CATEGORIES)
    suspend fun fetchCategories(
        @Header("X-Api-Key") apiKey: String
    ): Response<CategoryTreeResponse>

    @GET(AppConstants.Api.MARKET_PRODUCTS)
    suspend fun fetchProducts(
        @Header("X-Api-Key") apiKey: String,
        @Query("category_id") categoryId: Int? = null,
        @Query("subcategory_id") subcategoryId: Int? = null,
        @Query("sort") sort: String = "default",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ProductsResponse>

    @GET("${AppConstants.Api.MARKET_PRODUCTS}/{id}")
    suspend fun fetchProductById(
        @Header("X-Api-Key") apiKey: String,
        @Path("id") id: Int
    ): Response<ProductDetailResponse>

    /**
     * Portal (Admin) login. Requires X-Api-Key (client api key).
     */
    @POST(AppConstants.Api.PORTAL_LOGIN)
    suspend fun portalLogin(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: PortalLoginRequest
    ): Response<PortalLoginResponse>

    /**
     * WebSocket token for live prices (GET /client-apps/ws-token).
     */
    @GET(AppConstants.Api.WS_TOKEN)
    suspend fun getWsToken(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<WsTokenResponse>

    @POST(AppConstants.Api.PUSH_REGISTER)
    suspend fun pushRegister(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: PushRegisterRequest
    ): Response<PushRegisterResponse>

    @POST(AppConstants.Api.PUSH_UNREGISTER)
    suspend fun pushUnregister(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: PushRegisterRequest
    ): Response<PushRegisterResponse>

    @POST(AppConstants.Api.GENERATE_SHARE_URL)
    suspend fun generateShareUrl(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: GenerateShareUrlRequest
    ): Response<GenerateShareUrlResponse>

    /**
     * Fetch gold multipliers (portal). GET /client-apps/gold-multipliers.
     */
    @GET(AppConstants.Api.GOLD_MULTIPLIERS)
    suspend fun fetchGoldMultipliers(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<GoldMultipliersResponse>

    /**
     * Save gold multipliers (portal). POST /client-apps/gold-multipliers.
     */
    @POST(AppConstants.Api.GOLD_MULTIPLIERS)
    suspend fun saveGoldMultipliers(
        @Header("X-Api-Key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: List<SaveGoldMultiplierItem>
    ): Response<ResponseBody>
}
