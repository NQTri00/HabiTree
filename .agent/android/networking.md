# Android networking

## Client and ownership

Use **Ktor 3.x** and **kotlinx.serialization**. All API requests use the configured `HttpClient` provided by `core.network`. Keep endpoint paths, parameters and wire serialization in data sources. Repositories map responses to the domain contract. See [layer ownership](architecture.md) and [dependency injection](dependency-graph.md).

| Client concern | Standard |
| --- | --- |
| Authentication | Attach Bearer credentials for authenticated requests. Handle 401 responses with bounded refresh and retry. |
| JSON | Configure kotlinx.serialization centrally and register serializers required by the wire contract. |
| Logging | Debug-only request and response logging. Redact credentials and sensitive data. |
| Timeouts | Configure timeouts per client. |
| TLS | Configure production certificate pinning and maintain a rotation plan. |

## Data source

Route endpoints through focused `DataSource` classes. Use the shared safe request helpers and pass dynamic path segments through their URL encoding mechanism. These example endpoints show the data source shape for a plant catalog.

```kotlin
class PlantRemoteDataSource(private val apiClient: HttpClient) {
    suspend fun getPlants(): NetworkResult<List<PlantDto>> =
        apiClient.safeGet("/api/plants")

    suspend fun getPlantById(id: String): NetworkResult<PlantDto> =
        apiClient.safeGet("/api/plants/${id.encodeURLPathPart()}")

    suspend fun recordCare(request: PlantCareRequestDto): NetworkResult<PlantCareResultDto> =
        apiClient.safePost("/api/plant-care", body = request)
}
```

## DTOs and mapping

Use `@Serializable` DTOs with nullable properties and defaults for tolerant decoding. Preserve wire field names. Validate required domain fields after decoding. Use `BigDecimal` for monetary values with the serializer configured for the API's numeric or string representation. Carry the currency through the mapping. HabiTree coins are gameplay counters and retain their integer semantics.

```kotlin
@Serializable
data class PlantDto(
    val id: String? = null,
    val name: String? = null,
    @Contextual val price: BigDecimal? = null,
    val currency: String? = null,
    val rating: RatingDto? = null,
)
```

Repositories convert `NetworkResult<Dto>` into `RepositoryResult<Domain>`. Keep endpoint and JSON types out of presentation. Map DTOs and lists with the owning `DataMapper` and propagate failures in the domain result. Preserve coroutine cancellation through safe request and repository helpers.

```kotlin
class PlantRepositoryImpl(
    private val remoteDataSource: PlantRemoteDataSource,
    private val mapper: PlantDataMapper,
) : PlantRepository {
    override suspend fun getPlants(): RepositoryResult<List<Plant>> =
        when (val result = remoteDataSource.getPlants()) {
            is NetworkResult.Success ->
                RepositoryResult.Success(mapper.toDomainList(result.data))
            is NetworkResult.Error ->
                RepositoryResult.Error(result.exception)
        }
}
```

Implement endpoints against their actual contract. Test success, malformed or nullable payloads, non-success responses, timeout, cancellation and loss of connectivity. Preserve offline gameplay for local features.
