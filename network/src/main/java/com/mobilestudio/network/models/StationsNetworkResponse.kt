package com.mobilestudio.network.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StationsNetworkResponse(

	@field:JsonProperty("network")
	val network: Network? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Extra(

	@field:JsonProperty("zip")
	val zip: String? = null,

	@field:JsonProperty("NearbyStationList")
	val nearbyStationList: List<Int?>? = null,

	@field:JsonProperty("uid")
	val uid: Int? = null,

	@field:JsonProperty("address")
	val address: String? = null,

	@field:JsonProperty("districtCode")
	val districtCode: String? = null,

	@field:JsonProperty("status")
	val status: String? = null,

	@field:JsonProperty("ebikes")
	val ebikes: Boolean? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StationsItem(

	@field:JsonProperty("free_bikes")
	val freeBikes: Int? = null,

	@field:JsonProperty("extra")
	val extra: Extra? = null,

	@field:JsonProperty("latitude")
	val latitude: Double? = null,

	@field:JsonProperty("name")
	val name: String? = null,

	@field:JsonProperty("id")
	val id: String? = null,

	@field:JsonProperty("empty_slots")
	val emptySlots: Int? = null,

	@field:JsonProperty("longitude")
	val longitude: Double? = null,

	@field:JsonProperty("timestamp")
	val timestamp: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(

	@field:JsonProperty("country")
	val country: String? = null,

	@field:JsonProperty("city")
	val city: String? = null,

	@field:JsonProperty("latitude")
	val latitude: Double? = null,

	@field:JsonProperty("longitude")
	val longitude: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Network(

	@field:JsonProperty("name")
	val name: String? = null,

	@field:JsonProperty("company")
	val company: List<String?>? = null,

	@field:JsonProperty("location")
	val location: Location? = null,

	@field:JsonProperty("href")
	val href: String? = null,

	@field:JsonProperty("id")
	val id: String? = null,

	@field:JsonProperty("stations")
	val stations: List<StationsItem?>? = null
)
